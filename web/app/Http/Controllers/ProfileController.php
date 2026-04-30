<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;

class ProfileController extends Controller
{
    public function show()
    {
        // Use the authenticated user
        $userModel = auth()->user();

        // Translating month for Spanish display
        $months = [
            'January' => 'Enero', 'February' => 'Febrero', 'March' => 'Marzo', 
            'April' => 'Abril', 'May' => 'Mayo', 'June' => 'Junio', 
            'July' => 'Julio', 'August' => 'Agosto', 'September' => 'Septiembre', 
            'October' => 'Octubre', 'November' => 'Noviembre', 'December' => 'Diciembre'
        ];
        $monthName = $months[$userModel->created_at->format('F')] ?? $userModel->created_at->format('F');
        
        $user = [
            'name'           => $userModel->name,
            'email'          => $userModel->email,
            'is_admin'       => $userModel->is_admin,
            'total_xp'       => $userModel->total_xp,
            'current_streak' => $userModel->current_streak,
            'daily_goal'     => $userModel->daily_goal,
            'member_since'   => $monthName . ' ' . $userModel->created_at->format('Y'),
        ];

        // 2. Daily XP earned per day of the week (Mon-Sun)
        $dailyProgress = [
            'Lun' => 0, 'Mar' => 0, 'Mie' => 0, 'Jue' => 0, 
            'Vie' => 0, 'Sab' => 0, 'Dom' => 0
        ];
        
        $startOfWeek = \Carbon\Carbon::now()->startOfWeek();
        $endOfWeek = \Carbon\Carbon::now()->endOfWeek();

        $progressThisWeek = \App\Models\UserProgress::where('user_id', $userModel->id)
            ->whereBetween('completed_at', [$startOfWeek, $endOfWeek])
            ->join('lessons', 'user_progress.lesson_id', '=', 'lessons.id')
            ->select('user_progress.completed_at', 'lessons.xp_reward')
            ->get();

        $dayMap = [1 => 'Lun', 2 => 'Mar', 3 => 'Mie', 4 => 'Jue', 5 => 'Vie', 6 => 'Sab', 7 => 'Dom'];

        foreach ($progressThisWeek as $prog) {
            $dayOfWeek = \Carbon\Carbon::parse($prog->completed_at)->dayOfWeekIso;
            if (isset($dayMap[$dayOfWeek])) {
                $dailyProgress[$dayMap[$dayOfWeek]] += $prog->xp_reward;
            }
        }

        // 3. Monthly XP per week
        $monthlyProgress = ['Sem 1' => 0, 'Sem 2' => 0, 'Sem 3' => 0, 'Sem 4' => 0];
        
        $startOfMonth = \Carbon\Carbon::now()->startOfMonth();
        $endOfMonth = \Carbon\Carbon::now()->endOfMonth();

        $progressThisMonth = \App\Models\UserProgress::where('user_id', $userModel->id)
            ->whereBetween('completed_at', [$startOfMonth, $endOfMonth])
            ->join('lessons', 'user_progress.lesson_id', '=', 'lessons.id')
            ->select('user_progress.completed_at', 'lessons.xp_reward')
            ->get();

        foreach ($progressThisMonth as $prog) {
            $dayOfMonth = \Carbon\Carbon::parse($prog->completed_at)->day;
            $weekNum = ceil($dayOfMonth / 7);
            if ($weekNum > 4) $weekNum = 4;
            $monthlyProgress['Sem ' . $weekNum] += $prog->xp_reward;
        }

        // 4. Lesson progress percentages per topic
        $allLessons = \App\Models\Lesson::orderBy('order')->get();
        $userProgressList = \App\Models\UserProgress::where('user_id', $userModel->id)->pluck('lesson_id')->toArray();

        $lessons = [];
        $lessonCount = 0;

        $colors = [
            'variables'    => '#10b981', // emerald
            'operadores'   => '#f59e0b', // amber
            'bucles'       => '#3b82f6', // blue
            'funciones'    => '#6366f1', // indigo
            'pseudocodigo' => '#8b5cf6', // violet
            'poo'          => '#ec4899', // pink
        ];
        
        $emojis = [
            'variables'    => '📦',
            'operadores'   => '➕',
            'bucles'       => '🔄',
            'funciones'    => '🔧',
            'pseudocodigo' => '📝',
            'poo'          => '🏗️',
        ];

        foreach ($allLessons as $l) {
            $isCompleted = in_array($l->id, $userProgressList);
            if ($isCompleted) $lessonCount++;

            $lessons[] = [
                'name'  => $l->title,
                'emoji' => $emojis[$l->slug] ?? '📚',
                'pct'   => $isCompleted ? 100 : 0,
                'color' => $colors[$l->slug] ?? '#10b981',
            ];
        }

        // 5. Study statistics (Estimated time: 15 mins per lesson)
        $totalMins = $lessonCount * 15;
        $hours = floor($totalMins / 60);
        $mins = $totalMins % 60;
        $timeStr = ($hours > 0 ? "{$hours}h " : "") . "{$mins}m";
        if ($totalMins === 0) $timeStr = "0m";

        $stats = [
            ['icon' => '⏱️', 'label' => 'Estudio estimado',  'value' => $timeStr],
            ['icon' => '✅', 'label' => 'Lecciones terminadas', 'value' => (string)$lessonCount],
            ['icon' => '🔥', 'label' => 'Racha actual',      'value' => "{$userModel->current_streak} días"],
            ['icon' => '⭐', 'label' => 'XP total',          'value' => "{$userModel->total_xp} XP"],
        ];

        // Ensure variables max variables exist for view
        $maxDaily = max($dailyProgress) ?: 1;
        $maxMonthly = max($monthlyProgress) ?: 1;

        return view('profile', compact(
            'user', 'dailyProgress', 'maxDaily',
            'monthlyProgress', 'maxMonthly', 'stats', 'lessons', 'monthName'
        ));
    }
}
