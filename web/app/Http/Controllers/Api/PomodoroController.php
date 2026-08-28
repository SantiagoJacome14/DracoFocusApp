<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\PomodoroSession;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;

class PomodoroController extends Controller
{
    /**
     * Registra una sesión de trabajo del Dracomodoro completada.
     */
    public function store(Request $request)
    {
        $request->validate([
            'minutes' => ['required', 'integer', 'min:1', 'max:180'],
        ]);

        $session = PomodoroSession::create([
            'user_id' => Auth::id(),
            'minutes' => $request->minutes,
            'completed_at' => now(),
        ]);

        return response()->json([
            'status' => 'success',
            'session' => $session,
        ], 201);
    }
}
