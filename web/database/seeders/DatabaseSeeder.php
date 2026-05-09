<?php

namespace Database\Seeders;

use App\Models\User;
use App\Models\Lesson;
use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;

class DatabaseSeeder extends Seeder
{
    use WithoutModelEvents;

    /**
     * Seed the application's database.
     */
    public function run(): void
    {
        // Create demo user
        User::updateOrCreate(
            ['email' => 'test@example.com'],
            [
                'name'           => 'Estudiante Draco',
                'password'       => 'password',
                'daily_goal'     => 50,
                'current_streak' => 0,
                'total_xp'       => 0,
                'is_admin'       => false,
            ]
        );

        // Create admin user
        User::updateOrCreate(
            ['email' => 'admin@draco.app'],
            [
                'name'           => 'Admin Draco',
                'password'       => 'admin123',
                'daily_goal'     => 50,
                'current_streak' => 0,
                'total_xp'       => 0,
                'is_admin'       => true,
            ]
        );

        // Seed all lessons from Android app
        $this->call(LessonSeeder::class);

        // Migrate lessons.content Kotlin exercises into the exercises table
        $this->call(ExerciseSeeder::class);
    }
}
