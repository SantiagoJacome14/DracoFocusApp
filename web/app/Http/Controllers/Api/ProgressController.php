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
            ->with('lesson:id,title,slug') // Include basic lesson info if needed
            ->get();

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
            'lesson_id' => ['required', 'exists:lessons,id'],
            'score' => ['nullable', 'numeric', 'min:0', 'max:100'],
        ]);

        $progress = UserProgress::updateOrCreate(
            [
                'user_id' => Auth::id(),
                'lesson_id' => $request->lesson_id,
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
