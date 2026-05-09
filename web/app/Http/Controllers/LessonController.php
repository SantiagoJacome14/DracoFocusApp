<?php

namespace App\Http\Controllers;

use App\Models\Lesson;
use App\Models\Question;
use App\Models\UserProgress;
use Carbon\Carbon;
use App\Data\ExerciseBank;
use Illuminate\Http\Request;
use App\Models\Exercise;

class LessonController extends Controller
{
    /**
     * Get all lessons for Android dynamic slug mapping.
     */
    public function index()
    {
        $lessons = Lesson::select('id', 'slug', 'title', 'xp_reward', 'exercises')->get();

        return response()->json($lessons);
    }

    /**
     * API: Get exercises for a specific lesson slug.
     */
    public function getExercisesApi($slug)
    {
        $lesson = Lesson::where('slug', $slug)->first();

        if (!$lesson) {
            return response()->json(['message' => 'Lesson not found'], 404);
        }

        $exercises = $lesson->exercises()
            ->where('language', 'kotlin')
            ->where('is_active', true)
            ->orderBy('sort_order', 'asc')
            ->get();

        return response()->json([
            'lesson' => [
                'id' => $lesson->id,
                'slug' => $lesson->slug,
                'title' => $lesson->title,
                'description' => $lesson->description,
                'xp_reward' => $lesson->xp_reward,
            ],
            'exercises' => $exercises
        ]);
    }


    public function show(Request $request, $slug = null)
    {
        $topic = $request->query('topic') ?? $slug;
        $lesson = Lesson::where('slug', $topic)->first();

        if (!$lesson) {
            return redirect()->route('dashboard')->with('error', 'Lección no encontrada.');
        }

        // 1. Fetch exercises ONLY from the dedicated table
        $exercisesTable = $lesson->exercises()
            ->where('language', 'kotlin')
            ->where('is_active', true)
            ->orderBy('sort_order', 'asc')
            ->get();

        // 2. Determine source with fallback to Bank ONLY if table is empty
        if ($exercisesTable->isNotEmpty()) {
            $rawExercises = $exercisesTable;
        } else {
            $rawExercises = ExerciseBank::random($topic, 8);
        }

        // 3. Transform exercises
        $exercises = collect($rawExercises)->map(function ($ex) {
            $data = is_object($ex) ? $ex->toArray() : (array)$ex;
            
            // Flatten the 'data' JSON column if it exists (for code_before/after)
            if (isset($data['data']) && is_array($data['data'])) {
                $data = array_merge($data, $data['data']);
            }

            if (!isset($data['type'])) $data['type'] = 'fill';
            if (!isset($data['correct_answer']) && isset($data['answer'])) {
                $data['correct_answer'] = $data['answer'];
            }
            return $data;
        })->toArray();

        if (empty($exercises)) {
            $exercises = ExerciseBank::random('variables', 8);
        }

        // Log the exercise count for diagnostic purposes
        \Log::info('Exercises loaded for web view', ['slug' => $topic, 'count' => count($exercises)]);

        // 4. Load saved progress
        $currentExercise = 0;
        $progress = UserProgress::where('user_id', auth()->id())
            ->where('lesson_id', $lesson->id)
            ->first();

        if ($progress && !$progress->completed && count($exercises) > 0) {
            $currentExercise = min($progress->current_exercise ?? 0, count($exercises) - 1);
        }

        return view('lesson', compact('lesson', 'exercises', 'currentExercise'));
    }

    public function updateProgress(Request $request, string $slug)
    {
        $request->validate([
            'current_exercise' => ['required', 'integer', 'min:0'],
        ]);

        $lesson = Lesson::where('slug', $slug)->firstOrFail();

        $progress = UserProgress::firstOrNew([
            'user_id' => auth()->id(),
            'lesson_id' => $lesson->id,
        ]);

        if ($progress->completed) {
            return response()->json([
                'ok' => true,
                'completed' => true,
                'current_exercise' => $progress->current_exercise,
            ]);
        }

        $currentExercise = $request->integer('current_exercise');
        $progress->current_exercise = $currentExercise;
        $progress->completed = false;
        $progress->save();

        return response()->json([
            'ok' => true,
            'current_exercise' => $progress->current_exercise,
        ]);
    }

    public function aiFeedback(Request $request)
    {
        $data = $request->validate([
            'lesson_slug' => ['required', 'string', 'exists:lessons,slug'],
            'question' => ['required', 'string', 'max:1000'],
            'user_answer' => ['nullable'],
            'correct_answer' => ['nullable'],
            'type' => ['required', 'string', 'max:50'],
        ]);

        $fallback = $this->fallbackAiFeedback();

        if (!config('services.openai.key')) {
            return response()->json(['feedback' => $fallback]);
        }

        try {
            $response = \Illuminate\Support\Facades\Http::withToken(config('services.openai.key'))
                ->timeout(8)
                ->post('https://api.openai.com/v1/chat/completions', [
                    'model' => config('services.openai.model', 'gpt-4o-mini'),
                    'messages' => [
                        [
                            'role' => 'system',
                            'content' => 'Eres Draco, tutor de programacion en una app tipo RPG. Da pistas cortas en espanol, maximo 2 frases, sin revelar la respuesta correcta exacta.',
                        ],
                        [
                            'role' => 'user',
                            'content' => "El usuario fallo un ejercicio.\nPregunta: {$data['question']}\nRespuesta del usuario: " . $this->answerToText($data['user_answer'] ?? '') . "\nTipo de ejercicio: {$data['type']}\nRespuesta correcta interna: " . $this->answerToText($data['correct_answer'] ?? '') . "\nDa una pista amigable de dragon sin decir la respuesta exacta.",
                        ],
                    ],
                    'temperature' => 0.5,
                    'max_tokens' => 80,
                ]);

            if (!$response->successful()) {
                return response()->json(['feedback' => $fallback]);
            }

            $feedback = trim($response->json('choices.0.message.content', ''));

            return response()->json([
                'feedback' => $feedback !== '' ? $feedback : $fallback,
            ]);
        } catch (\Throwable $e) {
            return response()->json(['feedback' => $fallback]);
        }
    }

    public function complete(Request $request, $slug = null)
    {
        $lesson = Lesson::where('slug', $slug)->first();

        if (!$lesson) {
            return redirect()->route('dashboard')->with('error', 'Error al procesar la lección.');
        }

        $user = auth()->user();

        UserProgress::updateOrCreate(
            [
                'user_id' => auth()->id(),
                'lesson_id' => $lesson->id,
            ],
            [
                'score' => 100,
                'completed' => true,
                'completed_at' => now(),
            ]
        );

        $user->total_xp += $lesson->xp_reward;
        $user->save();

        return redirect()
            ->route('dashboard')
            ->with('success', "¡Lección completada! +{$lesson->xp_reward} XP 🎉");
    }

    private function fallbackAiFeedback(): string
    {
        return 'Draco detectó un error. Revisa el concepto principal e inténtalo otra vez.';
    }

    private function answerToText($answer): string
    {
        if (is_array($answer)) {
            return implode(' ', $answer);
        }

        return (string) $answer;
    }
}
