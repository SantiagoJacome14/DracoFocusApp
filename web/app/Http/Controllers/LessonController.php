<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\Lesson;
use App\Models\User;
use App\Models\UserProgress;
use Carbon\Carbon;
use App\Data\ExerciseBank;

class LessonController extends Controller
{
    /**
     * Get all lessons for Android dynamic slug mapping.
     */
    public function index()
    {
        $lessons = Lesson::select('id', 'slug', 'title', 'xp_reward')->get();

        return response()->json($lessons);
    }

    /**
     * Show a lesson with a random selection of 8 exercises from the bank.
     * Each visit draws a different random set, encouraging re-attempts.
     */
    public function show(Request $request, $slug = null)
    {
        // Use topic query param if provided, otherwise slug, otherwise default to variables
        $topic = $request->query('topic') ?? $slug ?? 'variables';

        $lesson = Lesson::where('slug', $topic)->first();

        if (!$lesson) {
            return redirect()->route('dashboard')->with('error', 'Lección no encontrada.');
        }

        // Pull 8 random exercises from the ExerciseBank (15 available per topic)
        $exercises = ExerciseBank::random($topic, 8);

        return view('lesson', compact('lesson', 'exercises'));
    }

    public function complete(Request $request, $slug = null)
    {
        $lesson = Lesson::where('slug', $slug)->first();

        if (!$lesson) {
            return redirect()->route('dashboard')->with('error', 'Error al procesar la lección.');
        }

        // Use authenticated user
        $user = auth()->user();

        // 1. Record progress
        UserProgress::updateOrCreate(
            [
                'user_id' => $user->id,
                'lesson_id' => $lesson->id,
            ],
            [
                'score' => 100,
                'completed_at' => now(),
            ]
        );

        // 2. Add XP to user
        $user->total_xp += $lesson->xp_reward;
        $user->save();

        return redirect()->route('dashboard')->with('success', "¡Lección completada! +{$lesson->xp_reward} XP 🎉");
    }
}
