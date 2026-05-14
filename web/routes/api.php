<?php

use App\Http\Controllers\Api\AuthController;
use App\Http\Controllers\Api\GroupController;
use App\Http\Controllers\Api\MuseumRewardController;
use App\Http\Controllers\Api\ProgressController;
use App\Http\Controllers\LessonController;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;

// Public routes
Route::post('/register', [AuthController::class, 'register']);
Route::post('/login', [AuthController::class, 'login']);
Route::post('/auth/google', [AuthController::class, 'loginWithGoogle']);
Route::get('/lessons', [LessonController::class, 'index']);
Route::get('/lessons/{slug}/exercises', [LessonController::class, 'getExercisesApi']);

// Protected routes
Route::middleware('auth:sanctum')->group(function () {
    Route::get('/me', [AuthController::class, 'me']);
    Route::post('/logout', [AuthController::class, 'logout']);

    // Progress routes
    Route::get('/progress', [ProgressController::class, 'index']);
    Route::post('/progress/sync', [ProgressController::class, 'sync']);
    Route::post('/progress', [ProgressController::class, 'store']);
    Route::get('/user/stats', [ProgressController::class, 'stats']);

    // Museum rewards (source of truth for collectible images)
    Route::get('/museum/rewards', [MuseumRewardController::class, 'index']);
    Route::post('/museum/rewards/claim', [MuseumRewardController::class, 'claim']);

    // Group sessions (modo en grupo)
    Route::post('/groups', [GroupController::class, 'create']);
    Route::post('/groups/join', [GroupController::class, 'join']);
    Route::get('/groups/{code}', [GroupController::class, 'show']);
    Route::get('/groups/{code}/members', [GroupController::class, 'members']);
    Route::post('/groups/{code}/start', [GroupController::class, 'start']);
    Route::post('/groups/{code}/role', [GroupController::class, 'setRole']);
    Route::post('/groups/{code}/progress', [GroupController::class, 'saveProgress']);
    Route::post('/groups/{code}/complete', [GroupController::class, 'complete']);
});
