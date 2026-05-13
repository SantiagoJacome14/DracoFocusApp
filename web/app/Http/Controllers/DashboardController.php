<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\User;
use App\Models\Lesson;
use App\Models\UserProgress;
use Carbon\Carbon;

class DashboardController extends Controller
{
    public function lecciones()
    {
        $userModel = auth()->user();
        $allLessons = Lesson::orderBy('order')->get();
        $userProgress = UserProgress::where('user_id', $userModel->id)->pluck('lesson_id')->toArray();

        $lessonPath = [];
        $foundNextActive = false;

        foreach ($allLessons as $lesson) {
            $status = 'locked';
            $progressPercent = 0;
            
            $up = UserProgress::where('user_id', $userModel->id)->where('lesson_id', $lesson->id)->first();
            
            if ($up && $up->completed) {
                $status = 'completed';
                $progressPercent = 100;
            } elseif (!$foundNextActive) {
                $status = 'active';
                $foundNextActive = true;
                if ($up) {
                    $totalEx = $lesson->exercises()->count() ?: 8; // Default to 8 if not defined
                    $progressPercent = min(round(($up->current_exercise / $totalEx) * 100), 99);
                } else {
                    $progressPercent = 0;
                }
            }

            $lessonPath[] = [
                'id'         => $lesson->id,
                'slug'       => $lesson->slug,
                'name'       => $lesson->title,
                'difficulty' => $lesson->difficulty,
                'xp'         => $lesson->xp_reward,
                'status'     => $status,
                'progress'   => $progressPercent,
                'type'       => $lesson->type ?? 'fundamentos',
            ];
        }

        return view('lecciones', compact('lessonPath'));
    }

    public function avances()
    {
        $userModel = auth()->user();
        
        // Activity for current week
        $weeklyProgress = [
            'Lun' => 0, 'Mar' => 0, 'Mie' => 0, 'Jue' => 0, 
            'Vie' => 0, 'Sab' => 0, 'Dom' => 0
        ];
        $startOfWeek = Carbon::now()->startOfWeek();
        $endOfWeek = Carbon::now()->endOfWeek();
        $progressThisWeek = UserProgress::where('user_id', $userModel->id)
            ->whereBetween('completed_at', [$startOfWeek, $endOfWeek])
            ->join('lessons', 'user_progress.lesson_id', '=', 'lessons.id')
            ->select('user_progress.completed_at', 'lessons.xp_reward')
            ->get();

        $dayMap = [1 => 'Lun', 2 => 'Mar', 3 => 'Mie', 4 => 'Jue', 5 => 'Vie', 6 => 'Sab', 7 => 'Dom'];
        foreach ($progressThisWeek as $prog) {
            $dayOfWeek = Carbon::parse($prog->completed_at)->dayOfWeekIso;
            if (isset($dayMap[$dayOfWeek])) {
                $weeklyProgress[$dayMap[$dayOfWeek]] += $prog->xp_reward;
            }
        }

        // Achievements (real data)
        $achievements = $userModel->userAchievements()->with('achievement')->get();
        $completedLessonsCount = UserProgress::where('user_id', $userModel->id)->where('completed', true)->count();

        return view('avances', [
            'user' => $userModel,
            'weeklyProgress' => $weeklyProgress,
            'completedLessonsCount' => $completedLessonsCount,
            'achievements' => $achievements
        ]);
    }

    public function configuracion()
    {
        $user = auth()->user();
        return view('configuracion', compact('user'));
    }

    public function index()
    {
        $userModel = auth()->user();

        // 1. Redirection based on roles
        if ($userModel->isAdmin()) {
            return redirect()->route('admin.users');
        }

        if ($userModel->isTeacher()) {
            return redirect()->route('teacher.dashboard');
        }

        // 2. Calculate XP earned today
        $xpToday = UserProgress::where('user_id', $userModel->id)
            ->whereDate('completed_at', Carbon::today())
            ->join('lessons', 'user_progress.lesson_id', '=', 'lessons.id')
            ->sum('lessons.xp_reward');

        // 3. Prepare user data for view
        $user = [
            'name'           => $userModel->name,
            'avatar'         => $userModel->avatar,
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
