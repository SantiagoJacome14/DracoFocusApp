<?php

namespace Database\Seeders;

use App\Models\Lesson;
use Illuminate\Database\Seeder;

class LessonSeeder extends Seeder
{
    /**
     * Run the database seeds.
     * All lessons from Android app migrated here.
     */
    public function run(): void
    {
        $lessons = [
            // SOLO Fundamentals (from Android local IDs "1", "2", "3")
            [
                'slug' => 'decisiones_de_fuego',
                'title' => 'Decisiones de Fuego',
                'description' => 'Declara una variable entera llamada puntos con valor 10. Aprende condicionales e if/else.',
                'content' => [
                    'puzzle_pieces' => ['val', 'puntos', '=', '10', ': Int', 'var'],
                    'solution' => ['val', 'puntos', ': Int', '=', '10'],
                    'quiz_options' => ['var puntos: Int = 10', 'val puntos: Int = 10', 'val puntos = "10"', 'println(puntos)'],
                    'correct_quiz_index' => 1,
                    'fill_line' => '_____ puntos: Int = 10',
                    'fill_answer' => 'val',
                ],
                'difficulty' => 'beginner',
                'type' => 'solitario',
                'xp_reward' => 50,
                'order' => 1,
            ],
            [
                'slug' => 'vuelo_infinito',
                'title' => 'Vuelo Infinito',
                'description' => 'Crea un bucle que se repita 5 veces en Kotlin. Aprende bucles y repeat/while.',
                'content' => [
                    'puzzle_pieces' => ['repeat(5)', '{', '}', 'for', 'while'],
                    'solution' => ['repeat(5)', '{', '}'],
                    'quiz_options' => ['repeat(5) { }', 'while (true) { }', 'for (i in 1..100) break', 'do { } while (false)'],
                    'correct_quiz_index' => 0,
                    'fill_line' => 'repeat(_____) { }',
                    'fill_answer' => '5',
                    'extended_exercise' => 'Draco vuela 5 veces al día. Imprime "Vuelo 1", "Vuelo 2"... Si energía < 20, detén el ciclo.',
                ],
                'difficulty' => 'beginner',
                'type' => 'solitario',
                'xp_reward' => 100,
                'order' => 2,
            ],
            [
                'slug' => 'el_libro_de_tareas',
                'title' => 'El Libro de las Tareas',
                'description' => 'Si vida es menor a 20, activa el escudo. Aprende listas y lógica condicional.',
                'content' => [
                    'puzzle_pieces' => ['if', '(vida < 20)', 'activarEscudo()', '{', '}', 'else'],
                    'solution' => ['if', '(vida < 20)', '{', 'activarEscudo()', '}'],
                    'quiz_options' => ['if (vida == 20) activarEscudo()', 'if (vida < 20) { activarEscudo() }', 'while (vida < 20) { }', 'else activarEscudo()'],
                    'correct_quiz_index' => 1,
                    'fill_line' => 'if (______ < 20) { activarEscudo() }',
                    'fill_answer' => 'vida',
                    'extended_exercise' => 'Crea una lista con las tareas de Draco: ["estudiar", "volar", "descansar", "tomar café"]. Muestra cada tarea con su número de orden.',
                ],
                'difficulty' => 'intermediate',
                'type' => 'solitario',
                'xp_reward' => 150,
                'order' => 3,
            ],

            // GRUPAL Lessons (3 topics × 2 roles = 6 screens)
            [
                'slug' => 'guardianes_tesoro_programador',
                'title' => 'Guardianes del Tesoro (Rol: Programador)',
                'description' => 'Draco custodia un cofre mágico que solo puede abrirse si el usuario ingresa correctamente una contraseña secreta. Si el intento falla tres veces, el cofre se bloquea.',
                'content' => [
                    'context' => 'Manejo de variables y condicionales',
                    'task' => 'Implementa el código en Python aplicando estructuras if, while y contadores.',
                ],
                'difficulty' => 'intermediate',
                'type' => 'grupal',
                'xp_reward' => 200,
                'order' => 4,
            ],
            [
                'slug' => 'guardianes_tesoro_analista',
                'title' => 'Guardianes del Tesoro (Rol: Analista)',
                'description' => 'Draco custodia un cofre mágico que solo puede abrirse si el usuario ingresa correctamente una contraseña secreta. Si el intento falla tres veces, el cofre se bloquea.',
                'content' => [
                    'context' => 'Manejo de variables y condicionales',
                    'task' => 'Redacta el pseudocódigo que indique: cómo solicitar la contraseña, cómo validar el intento, y qué hacer si se supera el límite de intentos.',
                ],
                'difficulty' => 'intermediate',
                'type' => 'grupal',
                'xp_reward' => 200,
                'order' => 5,
            ],
            [
                'slug' => 'vuelos_entrenamiento_programador',
                'title' => 'Vuelos de Entrenamiento (Rol: Programador)',
                'description' => 'Draco está entrenando para un gran torneo de vuelo. Registra los resultados de varios entrenamientos (por ejemplo, velocidades o tiempos). Al final, quiere conocer su promedio y mejor resultado.',
                'content' => [
                    'context' => 'Ciclos, listas y acumuladores',
                    'task' => 'Implementa en Python usando listas, ciclos for y funciones como sum() y max().',
                ],
                'difficulty' => 'advanced',
                'type' => 'grupal',
                'xp_reward' => 250,
                'order' => 6,
            ],
            [
                'slug' => 'vuelos_entrenamiento_analista',
                'title' => 'Vuelos de Entrenamiento (Rol: Analista)',
                'description' => 'Draco está entrenando para un gran torneo de vuelo. Registra los resultados de varios entrenamientos (por ejemplo, velocidades o tiempos). Al final, quiere conocer su promedio y mejor resultado.',
                'content' => [
                    'context' => 'Ciclos, listas y acumuladores',
                    'task' => 'Redacta el pseudocódigo que indique: cómo solicitar los datos de vuelo, cómo almacenarlos en una lista, cómo calcular el promedio y el máximo.',
                ],
                'difficulty' => 'advanced',
                'type' => 'grupal',
                'xp_reward' => 250,
                'order' => 7,
            ],
            [
                'slug' => 'acertijos_programador',
                'title' => 'El Reto de los Acertijos (Rol: Programador)',
                'description' => 'Draco debe resolver tres acertijos mágicos para abrir una puerta secreta. Cada acertijo tiene una pregunta distinta (por ejemplo, adivinar un número, calcular un área o clasificar una palabra como larga o corta).',
                'content' => [
                    'context' => 'El reto de los acertijos',
                    'task' => 'Implementa cada función en Python y llama a todas desde una función principal.',
                ],
                'difficulty' => 'advanced',
                'type' => 'grupal',
                'xp_reward' => 300,
                'order' => 8,
            ],
            [
                'slug' => 'acertijos_analista',
                'title' => 'El Reto de los Acertijos (Rol: Analista)',
                'description' => 'Draco debe resolver tres acertijos mágicos para abrir una puerta secreta. Cada acertijo tiene una pregunta distinta (por ejemplo, adivinar un número, calcular un área o clasificar una palabra como larga o corta).',
                'content' => [
                    'context' => 'El reto de los acertijos',
                    'task' => 'Crea el pseudocódigo que divida el problema en tres funciones distintas, una por acertijo.',
                ],
                'difficulty' => 'advanced',
                'type' => 'grupal',
                'xp_reward' => 300,
                'order' => 9,
            ],
        ];

        foreach ($lessons as $lesson) {
            Lesson::updateOrCreate(
                ['slug' => $lesson['slug']],
                $lesson
            );
        }
    }
}
