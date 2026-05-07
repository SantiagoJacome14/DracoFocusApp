<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\User;
use App\Models\Lesson;
use App\Models\UserProgress;
use Carbon\Carbon;

class DashboardController extends Controller
{
    public function index()
    {
        // Use the authenticated user
        $userModel = auth()->user();

        if ($userModel->role === 'teacher') {
            $students = User::where('role', 'student')->get()->map(function ($student) {
                $completedLessons = UserProgress::where('user_id', $student->id)->count();
                $totalLessons = Lesson::count();
                $percentage = $totalLessons > 0 ? round(($completedLessons / $totalLessons) * 100) : 0;
                
                $student->completed_lessons = $completedLessons;
                $student->total_lessons = $totalLessons;
                $student->pending_lessons = $totalLessons - $completedLessons;
                $student->progress_percentage = $percentage;
                return $student;
            });
            return view('teacher.dashboard', compact('students'));
        }

        // 2. Calculate XP earned today
        $xpToday = UserProgress::where('user_id', $userModel->id)
            ->whereDate('completed_at', Carbon::today())
            ->join('lessons', 'user_progress.lesson_id', '=', 'lessons.id')
            ->sum('lessons.xp_reward');

        // 3. Prepare user data for view
        $user = [
            'name'           => $userModel->name,
            'daily_goal'     => $userModel->daily_goal ?? 50,
            'current_streak' => $userModel->current_streak ?? 0,
            'total_xp'       => $userModel->total_xp ?? 0,
            'goal_progress'  => $xpToday,
        ];

        // 4. Fetch all lessons and track completion
        $allLessons = Lesson::orderBy('order')->get();
        $userProgress = UserProgress::where('user_id', $userModel->id)->pluck('lesson_id')->toArray();

        $lessonPath = [];
        $foundNextActive = false;

        foreach ($allLessons as $lesson) {
            $status = 'locked';

            if (in_array($lesson->id, $userProgress)) {
                $status = 'completed';
            } elseif (!$foundNextActive) {
                // The first lesson not completed is 'active'
                $status = 'active';
                $foundNextActive = true;
            }

            $lessonPath[] = [
                'id'     => $lesson->id,
                'slug'   => $lesson->slug,
                'name'   => $lesson->title,
                'emoji'  => $this->getEmojiForSlug($lesson->slug),
                'xp'     => $lesson->xp_reward,
                'status' => $status,
            ];
        }

        return view('dashboard', compact('user', 'lessonPath'));
    }

    private function getEmojiForSlug($slug)
    {
        $emojis = [
            'variables'    => '📦',
            'operadores'   => '➕',
            'bucles'       => '🔄',
            'funciones'    => '🔧',
            'pseudocodigo' => '📝',
            'poo'          => '🏗️',
        ];
        return $emojis[$slug] ?? '📚';
    }
}
