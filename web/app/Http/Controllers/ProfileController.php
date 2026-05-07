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

        // ─── Admin gets a completely different profile ───
        if ($userModel->is_admin) {
            return $this->showAdminProfile($user, $userModel, $monthName);
        }

        // ─── Student Profile ───
        return $this->showStudentProfile($user, $userModel, $monthName);
    }

    /**
     * Admin Profile — Platform overview & management dashboard.
     */
    private function showAdminProfile(array $user, $userModel, string $monthName)
    {
        // Platform-wide statistics
        $totalUsers = \App\Models\User::count();
        $students = \App\Models\User::where('is_admin', false)->count();
        $totalLessons = \App\Models\Lesson::count();
        $totalCompletions = \App\Models\UserProgress::count();
        $totalXpEarned = \App\Models\User::sum('total_xp');
        $avgXp = $students > 0 ? round(\App\Models\User::where('is_admin', false)->avg('total_xp')) : 0;
        $maxStreak = \App\Models\User::max('current_streak') ?? 0;

        $platformStats = [
            'total_users'       => $totalUsers,
            'students'          => $students,
            'total_lessons'     => $totalLessons,
            'total_completions' => $totalCompletions,
            'total_xp_earned'   => $totalXpEarned,
            'avg_xp'            => $avgXp,
            'max_streak'        => $maxStreak,
        ];

        // Lesson completion rates
        $allLessons = \App\Models\Lesson::orderBy('order')->get();
        $emojis = [
            'variables' => '📦', 'operadores' => '➕', 'bucles' => '🔄',
            'funciones' => '🔧', 'pseudocodigo' => '📝', 'poo' => '🏗️',
        ];
        $colors = [
            'variables' => '#10b981', 'operadores' => '#f59e0b', 'bucles' => '#3b82f6',
            'funciones' => '#6366f1', 'pseudocodigo' => '#8b5cf6', 'poo' => '#ec4899',
        ];

        $lessonStats = [];
        foreach ($allLessons as $l) {
            $completions = \App\Models\UserProgress::where('lesson_id', $l->id)->count();
            $rate = $students > 0 ? round(($completions / $students) * 100) : 0;
            $lessonStats[] = [
                'title'       => $l->title,
                'emoji'       => $emojis[$l->slug] ?? '📚',
                'color'       => $colors[$l->slug] ?? '#10b981',
                'completions' => $completions,
                'rate'        => $rate,
            ];
        }

        // Recent users (last 8 registrations)
        $recentUsers = \App\Models\User::orderBy('created_at', 'desc')->take(8)->get();

        // Top 3 students by XP
        $topStudents = \App\Models\User::where('is_admin', false)
            ->orderBy('total_xp', 'desc')
            ->take(3)
            ->get();

        // All users with progress count for the table
        $allUsers = \App\Models\User::withCount('progress')
            ->orderBy('total_xp', 'desc')
            ->take(20)
            ->get();

        // Weekly registration trend
        $weeklyRegistrations = [
            'Lun' => 0, 'Mar' => 0, 'Mie' => 0, 'Jue' => 0, 
            'Vie' => 0, 'Sab' => 0, 'Dom' => 0
        ];
        $startOfWeek = \Carbon\Carbon::now()->startOfWeek();
        $endOfWeek = \Carbon\Carbon::now()->endOfWeek();
        $regsThisWeek = \App\Models\User::whereBetween('created_at', [$startOfWeek, $endOfWeek])->get();
        $dayMap = [1 => 'Lun', 2 => 'Mar', 3 => 'Mie', 4 => 'Jue', 5 => 'Vie', 6 => 'Sab', 7 => 'Dom'];
        foreach ($regsThisWeek as $u) {
            $dow = $u->created_at->dayOfWeekIso;
            if (isset($dayMap[$dow])) {
                $weeklyRegistrations[$dayMap[$dow]]++;
            }
        }

        // Recent lesson completions
        $recentCompletions = \App\Models\UserProgress::join('users', 'user_progress.user_id', '=', 'users.id')
            ->join('lessons', 'user_progress.lesson_id', '=', 'lessons.id')
            ->select('users.name as user_name', 'lessons.title as lesson_title', 'user_progress.completed_at')
            ->orderBy('user_progress.completed_at', 'desc')
            ->take(8)
            ->get();

        return view('admin.profile', compact(
            'user', 'platformStats', 'lessonStats', 'recentUsers',
            'topStudents', 'allUsers', 'weeklyRegistrations', 'recentCompletions'
        ));
    }

    /**
     * Student Profile — Personal stats and progress.
     */
    private function showStudentProfile(array $user, $userModel, string $monthName)
    {
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
