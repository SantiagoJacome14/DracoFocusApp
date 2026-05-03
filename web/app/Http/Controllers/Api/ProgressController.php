<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Lesson;
use App\Models\UserProgress;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;

class ProgressController extends Controller
{
    /**
     * Display a listing of the authenticated user's progress.
     */
    public function index()
    {
        $progress = UserProgress::where('user_id', Auth::id())
            ->with('lesson:id,slug')
            ->get();

        return response()->json([
            'completed_lessons' => $progress->map(fn($item) => $item->lesson->slug)->values(),
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

    public function sync(Request $request)
    {
        $request->validate([
            'completed_lessons' => ['required', 'array'],
            'completed_lessons.*' => ['string', 'exists:lessons,slug'],
        ]);

        $slugs = $request->completed_lessons;
        $lessonIds = Lesson::whereIn('slug', $slugs)->pluck('id', 'slug');

        foreach ($lessonIds as $slug => $lessonId) {
            UserProgress::firstOrCreate(
                [
                    'user_id' => Auth::id(),
                    'lesson_id' => $lessonId,
                ]
            );
        }

        $progress = UserProgress::where('user_id', Auth::id())
            ->with('lesson:id,slug')
            ->get();

        return response()->json([
            'completed_lessons' => $progress->map(fn($item) => $item->lesson->slug)->values(),
            'completed_lesson_ids' => $progress->map(fn($item) => $item->lesson_id)->values(),
        ]);
    }
}
