<?php

namespace Database\Seeders;

use App\Models\Lesson;
use Illuminate\Database\Seeder;
use Illuminate\Support\Str;

class LessonSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        $lessons = [
            [
                'title' => 'Variables mágicas',
                'description' => 'Aprende a guardar hechizos en variables.',
                'content' => ['intro' => 'Las variables son como cofres...'],
                'difficulty' => 'Básica',
                'type' => 'solitario',
                'xp_reward' => 50,
                'order' => 1,
            ],
            [
                'title' => 'Condicionales del dragón',
                'description' => 'Toma decisiones basadas en el fuego del dragón.',
                'content' => ['intro' => 'Si el dragón tiene hambre...'],
                'difficulty' => 'Intermedia',
                'type' => 'solitario',
                'xp_reward' => 100,
                'order' => 2,
            ],
            [
                'title' => 'Bucles del tesoro',
                'description' => 'Repite acciones para encontrar el tesoro escondido.',
                'content' => ['intro' => 'Mientras no encuentres el oro...'],
                'difficulty' => 'Avanzada',
                'type' => 'solitario',
                'xp_reward' => 150,
                'order' => 3,
            ],
        ];

        foreach ($lessons as $lesson) {
            Lesson::create(array_merge($lesson, [
                'slug' => Str::slug($lesson['title']),
            ]));
        }
    }
}
