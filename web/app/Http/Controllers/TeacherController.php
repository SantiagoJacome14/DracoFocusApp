<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;

class TeacherController extends Controller
{
    public function dashboard()
    {
        $students = \App\Models\User::where('role', 'estudiante')->get()->map(function ($student) {
            $completedLessons = \App\Models\UserProgress::where('user_id', $student->id)->count();
            $totalLessons = \App\Models\Lesson::count();
            $percentage = $totalLessons > 0 ? round(($completedLessons / $totalLessons) * 100) : 0;
            
            $student->completed_lessons = $completedLessons;
            $student->total_lessons = $totalLessons;
            $student->pending_lessons = $totalLessons - $completedLessons;
            $student->progress_percentage = $percentage;
            return $student;
        });

        return view('teacher.dashboard', compact('students'));
    }
}
