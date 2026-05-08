<?php

namespace App\Http\Controllers;

use App\Models\Lesson;
use App\Models\Question;
use App\Models\UserProgress;
use Carbon\Carbon;
use App\Data\ExerciseBank;
use Illuminate\Http\Request;

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



    public function show(Request $request, $slug = null)
    {
        $topic = $request->query('topic') ?? $slug;
        $lesson = Lesson::where('slug', $topic)->first();

        if (!$lesson) {
            return redirect()->route('dashboard')->with('error', 'Lección no encontrada.');
        }

        // 1. Determine source of exercises
        $dbExercises = $lesson->exercises ?? [];
        
        // Use DB if not empty, otherwise fallback to ExerciseBank
        if (!empty($dbExercises)) {
            $rawExercises = $dbExercises;
        } else {
            $rawExercises = ExerciseBank::random($topic, 8);
        }

        // 2. Transform exercises
        $exercises = collect($rawExercises)->map(function ($ex) {
            $ex = (array) $ex;
            if (!isset($ex['type'])) $ex['type'] = 'fill';
            if (!isset($ex['correct_answer']) && isset($ex['answer'])) {
                $ex['correct_answer'] = $ex['answer'];
            }
            return $ex;
        })->toArray();

        if (empty($exercises)) {
            $exercises = ExerciseBank::random('variables', 8);
        }

        // 3. Load saved progress
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
