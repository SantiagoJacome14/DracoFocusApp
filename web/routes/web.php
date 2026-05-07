<?php

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\AuthController;
use App\Http\Controllers\DashboardController;
use App\Http\Controllers\LessonController;
use App\Http\Controllers\AdminController;
use App\Http\Controllers\ProfileController;

/*
|--------------------------------------------------------------------------
| Authentication Routes (guest only)
|--------------------------------------------------------------------------
*/
Route::middleware('guest')->group(function () {
    Route::get('/',        [AuthController::class, 'showLogin'])->name('login');
    Route::post('/',  [AuthController::class, 'login'])->name('login.post');
    Route::get('/register',  [AuthController::class, 'showRegister'])->name('register');
    Route::post('/register', [AuthController::class, 'register']);

    // Google OAuth
    Route::get('/auth/google', [AuthController::class, 'redirectToGoogle'])->name('auth.google');
    Route::get('/auth/google/callback', [AuthController::class, 'handleGoogleCallback'])->name('auth.google.callback');
});

/*
|--------------------------------------------------------------------------
| Authenticated Routes
|--------------------------------------------------------------------------
*/
Route::middleware('auth')->group(function () {
    // Dashboard
    Route::get('/dashboard', [DashboardController::class, 'index'])->name('dashboard');

    // Leave Impersonation
    Route::post('/impersonate/leave', [AdminController::class, 'leaveImpersonation'])->name('impersonate.leave');

    // Lessons
    Route::get('/lesson/{slug?}', [LessonController::class, 'show'])->name('lesson.show');
    Route::post('/lesson/ai-feedback', [LessonController::class, 'aiFeedback'])->name('lesson.ai-feedback');
    Route::post('/lesson/{slug}/progress', [LessonController::class, 'updateProgress'])->name('lesson.progress');
    Route::post('/lesson/{slug?}/complete', [LessonController::class, 'complete'])->name('lesson.complete');

    // User Profile
    Route::get('/profile', [ProfileController::class, 'show'])->name('profile');

    // Logout
    Route::match(['get', 'post'], '/logout', [AuthController::class, 'logout'])->name('logout');

    Route::prefix('teacher')->name('teacher.')->middleware(\App\Http\Middleware\TeacherMiddleware::class)->group(function () {
        Route::get('/questions', [\App\Http\Controllers\TeacherQuestionController::class, 'index'])->name('questions.index');
        Route::get('/questions/create', [\App\Http\Controllers\TeacherQuestionController::class, 'create'])->name('questions.create');
        Route::post('/questions', [\App\Http\Controllers\TeacherQuestionController::class, 'store'])->name('questions.store');
        Route::get('/questions/{question}/edit', [\App\Http\Controllers\TeacherQuestionController::class, 'edit'])->name('questions.edit');
        Route::put('/questions/{question}', [\App\Http\Controllers\TeacherQuestionController::class, 'update'])->name('questions.update');
        Route::delete('/questions/{question}', [\App\Http\Controllers\TeacherQuestionController::class, 'destroy'])->name('questions.destroy');
        // Teacher profile
        Route::get('/profile/edit', [ProfileController::class, 'editTeacherProfile'])->name('profile.edit');
        Route::put('/profile', [ProfileController::class, 'updateTeacherProfile'])->name('profile.update');
    });

    Route::prefix('admin')->name('admin.')->middleware('admin')->group(function () {
        Route::get('/users', [AdminController::class, 'users'])->name('users');
        Route::post('/users/{user}/impersonate', [AdminController::class, 'impersonate'])->name('users.impersonate');
        Route::delete('/users/{user}', [AdminController::class, 'destroyUser'])->name('users.destroy');
        Route::get('/teachers', [AdminController::class, 'teachers'])->name('teachers.index');
        Route::get('/teachers/create', [AdminController::class, 'createTeacher'])->name('teachers.create');
        Route::post('/teachers', [AdminController::class, 'storeTeacher'])->name('teachers.store');
        Route::get('/teachers/{user}/edit', [AdminController::class, 'editTeacher'])->name('teachers.edit');
        Route::put('/teachers/{user}', [AdminController::class, 'updateTeacher'])->name('teachers.update');
        Route::delete('/teachers/{user}', [AdminController::class, 'destroyTeacher'])->name('teachers.destroy');
    });
});
