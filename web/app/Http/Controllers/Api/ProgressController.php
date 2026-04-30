<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
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
            ->get()
            ->map(function ($item) {
                return [
                    'id' => $item->id,
                    'user_id' => $item->user_id,
                    'lesson_id' => $item->lesson->slug, // Android expects the slug as the ID
                    'score' => $item->score,
                    'completed_at' => $item->completed_at->toIso8601String(),
                ];
            });

        return response()->json([
            'status' => 'success',
            'data' => $progress
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
                'completed_at' => now(),
            ]
        );

        return response()->json([
            'status' => 'success',
            'message' => 'Progreso actualizado correctamente.',
            'data' => $progress
        ]);
    }
}
