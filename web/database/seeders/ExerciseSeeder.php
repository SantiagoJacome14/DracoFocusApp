<?php

namespace Database\Seeders;

use App\Models\Exercise;
use App\Models\Lesson;
use Illuminate\Database\Seeder;

class ExerciseSeeder extends Seeder
{
    /**
     * Migrates the Kotlin exercise content from lessons.content (JSON) into the
     * exercises table. Only seeds the three solitario lessons. Each lesson gets
     * three exercises: one per challenge type (code_puzzle, multiple_choice, fill_blank).
     *
     * Safe to re-run: uses updateOrCreate keyed on (lesson_id, type, language, sort_order).
     */
    public function run(): void
    {
        $exercises = [
            'decisiones_de_fuego' => [
                [
                    'type'       => 'code_puzzle',
                    'question'   => 'Ordena las piezas para declarar la variable puntos:',
                    'data'       => [
                        'pieces'   => ['val', 'puntos', '=', '10', ': Int', 'var'],
                        'solution' => ['val', 'puntos', ': Int', '=', '10'],
                    ],
                    'hint'       => 'Usa val para valores inmutables en Kotlin.',
                    'difficulty' => 'beginner',
                    'sort_order' => 1,
                ],
                [
                    'type'       => 'multiple_choice',
                    'question'   => '¿Cuál es la forma correcta de declarar una constante en Kotlin?',
                    'data'       => [
                        'options'       => [
                            'var puntos: Int = 10',
                            'val puntos: Int = 10',
                            'val puntos = "10"',
                            'println(puntos)',
                        ],
                        'correct_index' => 1,
                    ],
                    'hint'       => 'val declara un valor inmutable (constante).',
                    'difficulty' => 'beginner',
                    'sort_order' => 2,
                ],
                [
                    'type'       => 'fill_blank',
                    'question'   => 'Completa la declaración de la variable:',
                    'data'       => [
                        'code_before' => '',
                        'code_after'  => ' puntos: Int = 10',
                        'answer'      => 'val',
                    ],
                    'hint'       => 'val es inmutable en Kotlin.',
                    'difficulty' => 'beginner',
                    'sort_order' => 3,
                ],
            ],

            'vuelo_infinito' => [
                [
                    'type'       => 'code_puzzle',
                    'question'   => 'Ordena las piezas para crear un bucle que se repita 5 veces:',
                    'data'       => [
                        'pieces'   => ['repeat(5)', '{', '}', 'for', 'while'],
                        'solution' => ['repeat(5)', '{', '}'],
                    ],
                    'hint'       => 'En Kotlin, repeat() es la forma idiomática de repetir N veces.',
                    'difficulty' => 'beginner',
                    'sort_order' => 1,
                ],
                [
                    'type'       => 'multiple_choice',
                    'question'   => '¿Cuál es la forma idiomática de repetir 5 veces en Kotlin?',
                    'data'       => [
                        'options'       => [
                            'repeat(5) { }',
                            'while (true) { }',
                            'for (i in 1..100) break',
                            'do { } while (false)',
                        ],
                        'correct_index' => 0,
                    ],
                    'hint'       => 'repeat() recibe el número de repeticiones directamente.',
                    'difficulty' => 'beginner',
                    'sort_order' => 2,
                ],
                [
                    'type'       => 'fill_blank',
                    'question'   => 'Completa el bucle para que se repita 5 veces:',
                    'data'       => [
                        'code_before' => 'repeat(',
                        'code_after'  => ') { }',
                        'answer'      => '5',
                    ],
                    'hint'       => 'El argumento de repeat() es el número de iteraciones.',
                    'difficulty' => 'beginner',
                    'sort_order' => 3,
                ],
            ],

            'el_libro_de_tareas' => [
                [
                    'type'       => 'code_puzzle',
                    'question'   => 'Ordena las piezas para activar el escudo si vida < 20:',
                    'data'       => [
                        'pieces'   => ['if', '(vida < 20)', 'activarEscudo()', '{', '}', 'else'],
                        'solution' => ['if', '(vida < 20)', '{', 'activarEscudo()', '}'],
                    ],
                    'hint'       => 'El bloque if lleva la condición entre paréntesis y el cuerpo entre llaves.',
                    'difficulty' => 'intermediate',
                    'sort_order' => 1,
                ],
                [
                    'type'       => 'multiple_choice',
                    'question'   => '¿Qué código activa el escudo si vida es menor a 20?',
                    'data'       => [
                        'options'       => [
                            'if (vida == 20) activarEscudo()',
                            'if (vida < 20) { activarEscudo() }',
                            'while (vida < 20) { }',
                            'else activarEscudo()',
                        ],
                        'correct_index' => 1,
                    ],
                    'hint'       => 'Necesitas < para "menor que" y llaves para el cuerpo del if.',
                    'difficulty' => 'intermediate',
                    'sort_order' => 2,
                ],
                [
                    'type'       => 'fill_blank',
                    'question'   => 'Completa la condición para activar el escudo:',
                    'data'       => [
                        'code_before' => 'if (',
                        'code_after'  => ' < 20) { activarEscudo() }',
                        'answer'      => 'vida',
                    ],
                    'hint'       => 'La variable que guarda los puntos de vida.',
                    'difficulty' => 'intermediate',
                    'sort_order' => 3,
                ],
            ],
        ];

        foreach ($exercises as $slug => $lessonExercises) {
            $lesson = Lesson::where('slug', $slug)->first();

            if (! $lesson) {
                $this->command->warn("Lesson '{$slug}' not found — skipping. Run LessonSeeder first.");
                continue;
            }

            foreach ($lessonExercises as $attrs) {
                Exercise::updateOrCreate(
                    [
                        'lesson_id'  => $lesson->id,
                        'type'       => $attrs['type'],
                        'language'   => 'kotlin',
                        'sort_order' => $attrs['sort_order'],
                    ],
                    [
                        'question'   => $attrs['question'],
                        'data'       => $attrs['data'],
                        'hint'       => $attrs['hint'],
                        'difficulty' => $attrs['difficulty'],
                        'xp_reward'  => 0,
                        'is_active'  => true,
                    ]
                );
            }

            $this->command->info("Seeded 3 exercises for '{$slug}'.");
        }
    }
}
