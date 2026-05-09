<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Lesson;
use App\Models\UserProgress;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\Log;

class ProgressController extends Controller
{
    /**
     * Display a listing of the authenticated user's progress.
     */
    public function index()
    {
        $progress = UserProgress::where('user_id', Auth::id())
            ->where('completed', true)
            ->with('lesson:id,slug')
            ->get();

        return response()->json([
            'completed_lessons' => $progress->map(fn($item) => $item->lesson?->slug)->filter()->values(),
            'completed_lesson_ids' => $progress->map(fn($item) => $item->lesson_id)->values(),
        ]);
    }

    /**
     * Store or update the user's progress for a specific lesson.
     */
    public function store(Request $request)
    {
        $request->validate([
            'lesson_id' => ['required_without:lesson_slug', 'exists:lessons,id'],
            'lesson_slug' => ['required_without:lesson_id', 'exists:lessons,slug'],
            'score' => ['nullable', 'numeric', 'min:0', 'max:100'],
            'completed' => ['nullable', 'boolean'],
        ]);

        $lessonId = $request->lesson_id;

        if ($request->has('lesson_slug')) {
            $lessonId = \App\Models\Lesson::where('slug', $request->lesson_slug)->first()->id;
        }

        $progress = UserProgress::updateOrCreate(
            [
                'user_id' => Auth::id(),
                'lesson_id' => $lessonId,
            ],
            [
                'score' => $request->score ?? 100,
                'completed' => $request->completed ?? true,
                'completed_at' => now(),
            ]
        );

        return response()->json([
            'status' => 'success',
            'message' => 'Progreso actualizado correctamente.',
            'data' => [
                'user_id' => $progress->user_id,
                'lesson_slug' => $request->lesson_slug ?? $progress->lesson->slug,
                'completed' => $progress->completed,
                'score' => $progress->score
            ]
        ]);
    }

    public function stats()
    {
        $user = Auth::user();

        $completedProgress = UserProgress::where('user_id', $user->id)
            ->where('completed', true)
            ->with('lesson:id,slug,title,xp_reward')
            ->orderBy('completed_at', 'desc')
            ->get();

        $totalLessons = Lesson::count();
        $completedCount = $completedProgress->count();
        $progressPercent = $totalLessons > 0
            ? (int) round(($completedCount / $totalLessons) * 100)
            : 0;
        $level = (int) floor($user->total_xp / 200) + 1;
        $xpInLevel = $user->total_xp % 200;

        $badges = [];
        if ($completedCount >= 1) {
            $badges[] = ['id' => 'first_step', 'title' => 'Primer Paso', 'description' => 'Completaste tu primera lección', 'earned' => true];
        }
        if ($completedCount >= 3) {
            $badges[] = ['id' => 'apprentice', 'title' => 'Aprendiz Draco', 'description' => 'Completaste 3 lecciones', 'earned' => true];
        }
        if ($user->current_streak >= 3) {
            $badges[] = ['id' => 'consistent', 'title' => 'Constante', 'description' => 'Racha de 3 días consecutivos', 'earned' => true];
        }
        if ($totalLessons > 0 && $completedCount >= $totalLessons) {
            $badges[] = ['id' => 'explorer', 'title' => 'Explorador', 'description' => 'Completaste todas las lecciones disponibles', 'earned' => true];
        }

        return response()->json([
            'total_xp'               => $user->total_xp,
            'current_streak'         => $user->current_streak,
            'daily_goal'             => $user->daily_goal,
            'level'                  => $level,
            'completed_lessons_count' => $completedCount,
            'total_lessons_count'    => $totalLessons,
            'progress_percent'       => $progressPercent,
            'completed_lessons'      => $completedProgress->map(fn($p) => [
                'slug'         => $p->lesson?->slug,
                'title'        => $p->lesson?->title,
                'completed_at' => $p->completed_at?->toIso8601String(),
                'xp_reward'    => $p->lesson?->xp_reward ?? 0,
            ])->filter(fn($item) => $item['slug'] !== null)->values(),
            'badges'     => $badges,
            'chart_data' => [
                'completed'       => $completedCount,
                'pending'         => max(0, $totalLessons - $completedCount),
                'xp_current_level' => $xpInLevel,
                'xp_next_level'   => 200,
            ],
        ]);
    }

    public function sync(Request $request)
    {
        $request->validate([
            'completed_lessons' => ['required', 'array'],
            'completed_lessons.*' => ['string', 'exists:lessons,slug'],
        ]);

        $user = Auth::user();
        $slugs = $request->completed_lessons;
        $lessons = Lesson::whereIn('slug', $slugs)->get()->keyBy('id');
        $lessonIds = $lessons->pluck('id', 'slug');

        $xpEarned = 0;

        foreach ($lessonIds as $slug => $lessonId) {
            // Solo sumar XP si la lección NO estaba completada antes
            $alreadyDone = UserProgress::where('user_id', $user->id)
                ->where('lesson_id', $lessonId)
                ->where('completed', true)
                ->exists();

            UserProgress::updateOrCreate(
                ['user_id' => $user->id, 'lesson_id' => $lessonId],
                ['completed' => true, 'completed_at' => now(), 'score' => 100]
            );

            if (!$alreadyDone && isset($lessons[$lessonId])) {
                $xpEarned += $lessons[$lessonId]->xp_reward ?? 0;
            }
        }

        if ($xpEarned > 0) {
            $user->total_xp += $xpEarned;
            $user->save();
        }

        $progress = UserProgress::where('user_id', $user->id)
            ->where('completed', true)
            ->with('lesson:id,slug')
            ->get();

        return response()->json([
            'completed_lessons' => $progress->map(fn($item) => $item->lesson?->slug)->filter()->values(),
            'completed_lesson_ids' => $progress->map(fn($item) => $item->lesson_id)->values(),
            'xp_earned' => $xpEarned,
            'total_xp' => $user->fresh()->total_xp,
        ]);
    }
}
