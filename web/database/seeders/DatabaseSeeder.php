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

        // Create the 6 lessons in order
        $lessons = [
            ['slug' => 'variables',     'title' => 'Variables y Tipos de Datos',        'description' => 'Aprende a guardar datos usando let, const y var.',        'xp_reward' => 10, 'order' => 1],
            ['slug' => 'operadores',    'title' => 'Operadores',                         'description' => 'Conoce los operadores aritméticos, de comparación y lógicos.', 'xp_reward' => 10, 'order' => 2],
            ['slug' => 'bucles',        'title' => 'Bucles',                             'description' => 'Aprende a repetir acciones con for, while y do-while.',  'xp_reward' => 15, 'order' => 3],
            ['slug' => 'funciones',     'title' => 'Funciones',                          'description' => 'Crea bloques de código reutilizables con funciones.',     'xp_reward' => 15, 'order' => 4],
            ['slug' => 'pseudocodigo',  'title' => 'Pseudocódigo',                       'description' => 'Expresa algoritmos en lenguaje natural estructurado.',    'xp_reward' => 20, 'order' => 5],
            ['slug' => 'poo',           'title' => 'Programación Orientada a Objetos',   'description' => 'Domina clases, objetos, herencia y encapsulamiento.',     'xp_reward' => 25, 'order' => 6],
        ];

        foreach ($lessons as $data) {
            Lesson::updateOrCreate(
                ['slug' => $data['slug']],
                $data
            );
        }
    }
}
