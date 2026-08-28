<?php

namespace Database\Seeders;

use App\Models\Exercise;
use App\Models\Lesson;
use Illuminate\Database\Seeder;

class ExerciseSeeder extends Seeder
{
    /**
     * Contenido narrativo de las 3 lecciones solitarias (Capítulos 1-3 de DracoFocus).
     * Cada lección tiene 9 ejercicios que combinan los 3 tipos que la app ya sabe
     * renderizar (multiple_choice, fill_blank, code_puzzle), enmarcados como "momentos"
     * de una historia corta en vez de preguntas sueltas.
     *
     * data.story_success / data.story_fail: texto de Draco mostrado como feedback tras
     * responder (reemplaza el feedback genérico de IAFeedbackManager cuando existe).
     *
     * Safe to re-run: updateOrCreate keyed en (lesson_id, type, language, sort_order).
     * Si una lección cambia de idioma (como estas 3, que antes eran solo Kotlin), se
     * limpian primero los ejercicios del idioma anterior para no dejar filas huérfanas.
     */
    public function run(): void
    {
        $chapters = [
            // ---------------- CAPÍTULO 1 — PYTHON: "El Portal de Python" ----------------
            'decisiones_de_fuego' => [
                'language' => 'python',
                'items' => [
                    [
                        'type' => 'multiple_choice',
                        'question' => 'Draco quiere grabar tu nombre en la piedra del portal. ¿Qué usamos en Python para guardar un dato?',
                        'data' => [
                            'options' => ['Una variable', 'Un comentario', 'Una función', 'Un bucle'],
                            'correct_index' => 0,
                            'story_success' => '¡Eso es! Una variable es como un cofre donde Draco guarda información para usarla después.',
                            'story_fail' => 'Casi. En Python guardamos datos en variables, no en funciones ni comentarios.',
                        ],
                        'hint' => 'Piensa en un cofre donde guardas algo para usarlo después.',
                        'difficulty' => 'beginner', 'sort_order' => 1,
                    ],
                    [
                        'type' => 'fill_blank',
                        'question' => 'Draco necesita asignarle su nombre a una variable. Completa el hechizo:',
                        'data' => [
                            'code_before' => 'nombre = ',
                            'code_after' => '',
                            'answer' => '"Draco"',
                            'story_success' => '¡La piedra brilla! Ya sabes crear variables de texto en Python.',
                            'story_fail' => 'En Python, el texto va entre comillas. Prueba con "Draco".',
                        ],
                        'hint' => 'El texto en Python siempre va entre comillas.',
                        'difficulty' => 'beginner', 'sort_order' => 2,
                    ],
                    [
                        'type' => 'multiple_choice',
                        'question' => 'A esta runa le falta algo: print("Hola  ¿qué necesita para funcionar?',
                        'data' => [
                            'options' => ['Falta cerrar el paréntesis )', 'Falta un punto y coma', 'Falta un bucle', 'Nada, está bien'],
                            'correct_index' => 0,
                            'story_success' => 'Buen ojo. Draco detecta que en Python cada paréntesis abierto debe cerrarse.',
                            'story_fail' => 'Mira con cuidado los paréntesis: print("Hola" le falta cerrar.',
                        ],
                        'hint' => 'Cuenta los paréntesis: uno abre, uno debe cerrar.',
                        'difficulty' => 'beginner', 'sort_order' => 3,
                    ],
                    [
                        'type' => 'code_puzzle',
                        'question' => 'El hechizo de voz de Draco se desordenó. Ordénalo para que salude:',
                        'data' => [
                            'pieces' => ['print', '(', '"Hola Draco"', ')', 'input'],
                            'solution' => ['print', '(', '"Hola Draco"', ')'],
                            'story_success' => '¡Draco habla! print() muestra texto en pantalla.',
                            'story_fail' => 'print() siempre necesita sus paréntesis justo después del nombre.',
                        ],
                        'hint' => 'print va seguido de paréntesis con el texto adentro.',
                        'difficulty' => 'beginner', 'sort_order' => 4,
                    ],
                    [
                        'type' => 'multiple_choice',
                        'question' => 'La runa dice:  energia = 10  luego  energia = energia - 3  y por último  print(energia)  ¿Qué mostrará?',
                        'data' => [
                            'options' => ['10', '7', '3', '-3'],
                            'correct_index' => 1,
                            'story_success' => '¡Exacto, 7! Reasignar una variable cambia el valor que guarda.',
                            'story_fail' => 'Empieza en 10 y le restamos 3. Vuelve a hacer la cuenta.',
                        ],
                        'hint' => '10 - 3 = ?',
                        'difficulty' => 'beginner', 'sort_order' => 5,
                    ],
                    [
                        'type' => 'multiple_choice',
                        'question' => 'Draco tiene 10 de energía y encontró una poción que la deja en 20. ¿Qué instrucción usa?',
                        'data' => [
                            'options' => ['energia = 20', 'energia == 20', 'print(energia)', 'energia + 20'],
                            'correct_index' => 0,
                            'story_success' => '¡La poción funcionó! Un solo = asigna un valor nuevo.',
                            'story_fail' => 'Cuidado: == compara, no asigna. Para cambiar el valor usa un solo =.',
                        ],
                        'hint' => 'Un signo = asigna. Dos signos == comparan.',
                        'difficulty' => 'beginner', 'sort_order' => 6,
                    ],
                    [
                        'type' => 'multiple_choice',
                        'question' => 'El portal pregunta qué tipos de datos existen en Python. ¿Cuál de estas opciones es válida?',
                        'data' => [
                            'options' => ['Texto, número entero y verdadero/falso son todos tipos válidos', 'Solo existen números', 'Solo existe texto', 'Python no tiene tipos'],
                            'correct_index' => 0,
                            'story_success' => 'El portal se ilumina: texto (str), enteros (int) y booleanos son los tipos básicos de Python.',
                            'story_fail' => 'Python tiene varios tipos básicos: texto, números y verdadero/falso.',
                        ],
                        'hint' => 'Piensa en las tres cosas más comunes que guardarías: un nombre, una edad, una respuesta sí/no.',
                        'difficulty' => 'beginner', 'sort_order' => 7,
                    ],
                    [
                        'type' => 'fill_blank',
                        'question' => 'Combina lo aprendido: crea el mensaje del portal y complétalo para mostrarlo.',
                        'data' => [
                            'code_before' => "mensaje = \"El portal se abre\"\nprint(",
                            'code_after' => ')',
                            'answer' => 'mensaje',
                            'story_success' => '¡El portal cruje y empieza a abrirse! Guardaste el mensaje y lo mostraste.',
                            'story_fail' => 'print() debe recibir el nombre de la variable que creaste arriba: mensaje.',
                        ],
                        'hint' => 'print() necesita el nombre exacto de la variable que quieres mostrar.',
                        'difficulty' => 'beginner', 'sort_order' => 8,
                    ],
                    [
                        'type' => 'code_puzzle',
                        'question' => '🐉 JEFE FINAL: El portal exige un saludo completo con tu nombre. Ordena el hechizo final:',
                        'data' => [
                            'pieces' => ['print', '(', '"Portal abierto, "', '+', 'nombre', ')'],
                            'solution' => ['print', '(', '"Portal abierto, "', '+', 'nombre', ')'],
                            'story_success' => '✨ ¡MISIÓN COMPLETADA! El portal se abre por completo. Draco cruza agradecido — ya hablas Python.',
                            'story_fail' => 'El + une (concatena) un texto con una variable. Revisa el orden: primero el texto, luego +, luego la variable.',
                        ],
                        'hint' => 'El símbolo + une un texto con una variable en Python.',
                        'difficulty' => 'beginner', 'sort_order' => 9,
                    ],
                ],
            ],

            // ---------------- CAPÍTULO 2 — JAVA: "La Máquina de Java" ----------------
            'vuelo_infinito' => [
                'language' => 'java',
                'items' => [
                    [
                        'type' => 'multiple_choice',
                        'question' => 'La máquina de vuelo necesita aletear exactamente 5 veces. ¿Qué estructura repite algo un número fijo de veces en Java?',
                        'data' => [
                            'options' => ['for', 'if', 'una variable', 'print'],
                            'correct_index' => 0,
                            'story_success' => 'Los engranajes giran. for es la instrucción de Java para repetir algo un número exacto de veces.',
                            'story_fail' => 'if decide, no repite. La estructura que repite N veces es for.',
                        ],
                        'hint' => 'Piensa en la palabra que significa "para cada vuelta".',
                        'difficulty' => 'beginner', 'sort_order' => 1,
                    ],
                    [
                        'type' => 'fill_blank',
                        'question' => 'Completa el número de aleteos que necesita la máquina para despegar:',
                        'data' => [
                            'code_before' => 'for (int i = 0; i < ',
                            'code_after' => '; i++) { aletear(); }',
                            'answer' => '5',
                            'story_success' => 'Las alas se mueven al ritmo correcto. ¡5 aleteos exactos!',
                            'story_fail' => 'Recuerda: la máquina necesita aletear 5 veces.',
                        ],
                        'hint' => 'El número va donde dice "menor que ___".',
                        'difficulty' => 'beginner', 'sort_order' => 2,
                    ],
                    [
                        'type' => 'multiple_choice',
                        'question' => 'A esta instrucción le falta algo típico de Java: for (int i = 0; i < 5; i++) { aletear() }  ¿Qué es?',
                        'data' => [
                            'options' => ['Un punto y coma después de aletear()', 'La palabra for', 'Los paréntesis de la condición', 'Nada, está perfecta'],
                            'correct_index' => 0,
                            'story_success' => 'Buen ojo mecánico: en Java, cada instrucción dentro de las llaves termina en punto y coma.',
                            'story_fail' => 'Java es estricto: cada instrucción dentro de { } necesita su punto y coma final.',
                        ],
                        'hint' => 'Java exige punto y coma al final de cada instrucción.',
                        'difficulty' => 'beginner', 'sort_order' => 3,
                    ],
                    [
                        'type' => 'code_puzzle',
                        'question' => 'Ordena la declaración de energía de la máquina (Java es estricto con los tipos):',
                        'data' => [
                            'pieces' => ['int', 'energia', '=', '10', ';'],
                            'solution' => ['int', 'energia', '=', '10', ';'],
                            'story_success' => 'La máquina reconoce su energía. En Java siempre declaras el tipo (int) antes del nombre.',
                            'story_fail' => 'En Java el orden es: tipo, nombre, igual, valor, punto y coma.',
                        ],
                        'hint' => 'Orden en Java: tipo → nombre → = → valor → ;',
                        'difficulty' => 'beginner', 'sort_order' => 4,
                    ],
                    [
                        'type' => 'multiple_choice',
                        'question' => 'La máquina ejecuta:  int energia = 10;  luego  energia = energia - 4;  y  System.out.println(energia);  ¿Qué imprime?',
                        'data' => [
                            'options' => ['10', '6', '4', '-4'],
                            'correct_index' => 1,
                            'story_success' => '¡6 exacto! System.out.println es el print de Java.',
                            'story_fail' => '10 menos 4. Vuelve a intentarlo.',
                        ],
                        'hint' => '10 - 4 = ?',
                        'difficulty' => 'beginner', 'sort_order' => 5,
                    ],
                    [
                        'type' => 'multiple_choice',
                        'question' => 'La máquina debe seguir aleteando MIENTRAS tenga energía mayor a 0. ¿Qué instrucción usamos?',
                        'data' => [
                            'options' => ['while (energia > 0)', 'for (energia > 0)', 'if (energia > 0)', 'print(energia > 0)'],
                            'correct_index' => 0,
                            'story_success' => '¡Las alas no paran! while repite mientras la condición sea verdadera.',
                            'story_fail' => 'Cuando no sabes cuántas veces repetir de antemano, usamos while.',
                        ],
                        'hint' => 'while = "mientras".',
                        'difficulty' => 'beginner', 'sort_order' => 6,
                    ],
                    [
                        'type' => 'multiple_choice',
                        'question' => 'Antes de despegar, la máquina pregunta: ¿cómo se declara un número entero en Java?',
                        'data' => [
                            'options' => ['int', 'var', 'let', 'num'],
                            'correct_index' => 0,
                            'story_success' => 'Correcto. A diferencia de Kotlin (val/var), Java pide el tipo exacto: int.',
                            'story_fail' => 'Java no usa var para esto — necesita el tipo exacto: int.',
                        ],
                        'hint' => 'Es la abreviatura de "integer".',
                        'difficulty' => 'beginner', 'sort_order' => 7,
                    ],
                    [
                        'type' => 'fill_blank',
                        'question' => 'Combina la variable de vueltas con la acción de volar:',
                        'data' => [
                            'code_before' => "int vueltas = 3;\nfor (int i = 0; i < vueltas; i++) { ",
                            'code_after' => '(); }',
                            'answer' => 'volar',
                            'story_success' => 'Las alas giran 3 veces exactas. ¡La máquina cobra vida!',
                            'story_fail' => 'La acción que se repite en el aire es volar().',
                        ],
                        'hint' => 'Es la acción que hace que la máquina vuele.',
                        'difficulty' => 'beginner', 'sort_order' => 8,
                    ],
                    [
                        'type' => 'code_puzzle',
                        'question' => '🐉 JEFE FINAL: escribe el bucle que mantiene a la máquina volando mientras tenga energía.',
                        'data' => [
                            'pieces' => ['while', '(', 'energia', '>', '0', ')', '{', 'volar();', '}'],
                            'solution' => ['while', '(', 'energia', '>', '0', ')', '{', 'volar();', '}'],
                            'story_success' => '✨ ¡MISIÓN COMPLETADA! La máquina despega hacia el cielo. Draco vuela feliz — dominas Java.',
                            'story_fail' => 'Revisa el orden: while, la condición entre paréntesis, y la acción entre llaves.',
                        ],
                        'hint' => 'while (condición) { acción; }',
                        'difficulty' => 'beginner', 'sort_order' => 9,
                    ],
                ],
            ],

            // ---------------- CAPÍTULO 3 — KOTLIN: "El Núcleo de Draco" ----------------
            'el_libro_de_tareas' => [
                'language' => 'kotlin',
                'items' => [
                    [
                        'type' => 'multiple_choice',
                        'question' => 'El núcleo de Draco necesita guardar varias tareas de reparación en un solo lugar. ¿Qué estructura usamos en Kotlin?',
                        'data' => [
                            'options' => ['Una lista', 'Un Boolean', 'Un Int', 'Un if'],
                            'correct_index' => 0,
                            'story_success' => 'El núcleo zumba: una lista guarda varios elementos juntos, en orden.',
                            'story_fail' => 'Para guardar VARIAS tareas juntas necesitas una lista, no un solo valor.',
                        ],
                        'hint' => 'Necesitas guardar más de una cosa a la vez.',
                        'difficulty' => 'intermediate', 'sort_order' => 1,
                    ],
                    [
                        'type' => 'fill_blank',
                        'question' => 'Completa la tercera tarea de reparación del núcleo:',
                        'data' => [
                            'code_before' => 'val tareas = listOf("energia", "alas", "',
                            'code_after' => '")',
                            'answer' => 'nucleo',
                            'story_success' => 'Las tres tareas quedan registradas: energía, alas y núcleo.',
                            'story_fail' => 'La tercera pieza que falta reparar es el propio núcleo.',
                        ],
                        'hint' => 'Es lo que se está reparando en este capítulo.',
                        'difficulty' => 'intermediate', 'sort_order' => 2,
                    ],
                    [
                        'type' => 'multiple_choice',
                        'question' => 'A esta decisión le falta algo:  if (vida < 20) activarEscudo() else  ¿qué necesita para ser válida?',
                        'data' => [
                            'options' => ['El bloque después del else', 'La palabra if', 'Los paréntesis', 'Nada, está bien'],
                            'correct_index' => 0,
                            'story_success' => 'Correcto: un else siempre necesita su propio bloque de acción.',
                            'story_fail' => 'else no puede quedar vacío — necesita indicar qué hacer en ese caso.',
                        ],
                        'hint' => 'else siempre va seguido de una acción.',
                        'difficulty' => 'intermediate', 'sort_order' => 3,
                    ],
                    [
                        'type' => 'code_puzzle',
                        'question' => 'Ordena la decisión que activa el escudo del núcleo si la vida baja de 20:',
                        'data' => [
                            'pieces' => ['if', '(vida < 20)', '{', 'activarEscudo()', '}', 'else'],
                            'solution' => ['if', '(vida < 20)', '{', 'activarEscudo()', '}'],
                            'story_success' => 'El escudo responde al instante. El núcleo confía en tu lógica.',
                            'story_fail' => 'El orden es: if, la condición entre paréntesis, y la acción entre llaves.',
                        ],
                        'hint' => 'if (condición) { acción }',
                        'difficulty' => 'intermediate', 'sort_order' => 4,
                    ],
                    [
                        'type' => 'multiple_choice',
                        'question' => 'El núcleo ejecuta:  val vida = 15  luego  if (vida < 20) { println("Peligro") } else { println("A salvo") }  ¿Qué imprime?',
                        'data' => [
                            'options' => ['Peligro', 'A salvo', 'Nada', 'Error'],
                            'correct_index' => 0,
                            'story_success' => '15 es menor que 20, así que el núcleo grita "Peligro". ¡Correcto!',
                            'story_fail' => 'Compara: ¿15 es menor que 20?',
                        ],
                        'hint' => '¿15 es menor que 20?',
                        'difficulty' => 'intermediate', 'sort_order' => 5,
                    ],
                    [
                        'type' => 'multiple_choice',
                        'question' => 'El núcleo tiene 3 tareas pendientes en la lista tareas. ¿Cómo sabemos cuántas hay, en Kotlin?',
                        'data' => [
                            'options' => ['tareas.size', 'tareas.count', 'tareas.total', 'tareas.length'],
                            'correct_index' => 0,
                            'story_success' => 'El núcleo cuenta: .size es la forma idiomática de Kotlin para saber cuántos elementos tiene una lista.',
                            'story_fail' => 'En Kotlin las listas usan la propiedad .size, no .length ni .count directamente.',
                        ],
                        'hint' => 'Es una propiedad, no una función — no lleva paréntesis.',
                        'difficulty' => 'intermediate', 'sort_order' => 6,
                    ],
                    [
                        'type' => 'multiple_choice',
                        'question' => 'El núcleo pregunta: si vida es exactamente 20, ¿se activa el escudo con la condición vida < 20?',
                        'data' => [
                            'options' => ['No, 20 no es menor que 20', 'Sí, siempre se activa', 'Solo a veces', 'El escudo no depende de vida'],
                            'correct_index' => 0,
                            'story_success' => 'Exacto. < es estrictamente "menor que" — 20 no es menor que 20.',
                            'story_fail' => 'Piensa con cuidado: < es "menor que", no "menor o igual que".',
                        ],
                        'hint' => '< no incluye el valor exacto.',
                        'difficulty' => 'intermediate', 'sort_order' => 7,
                    ],
                    [
                        'type' => 'fill_blank',
                        'question' => 'Combina listas y decisiones: confirma la reparación cuando las 3 tareas estén en la lista.',
                        'data' => [
                            'code_before' => "val tareas = listOf(\"energia\", \"alas\", \"nucleo\")\nif (tareas.size == ",
                            'code_after' => ') { println("Reparación completa") }',
                            'answer' => '3',
                            'story_success' => 'El núcleo confirma: las 3 tareas están completas.',
                            'story_fail' => '¿Cuántas tareas hay en la lista? Cuenta los elementos.',
                        ],
                        'hint' => 'Cuenta cuántos elementos tiene la lista tareas.',
                        'difficulty' => 'intermediate', 'sort_order' => 8,
                    ],
                    [
                        'type' => 'code_puzzle',
                        'question' => '🐉 JEFE FINAL: escribe la decisión completa — protege a Draco si su vida baja, o confirma que está estable.',
                        'data' => [
                            'pieces' => ['if', '(vida < 20)', '{', 'activarEscudo()', '}', 'else', '{', 'println("Estable")', '}'],
                            'solution' => ['if', '(vida < 20)', '{', 'activarEscudo()', '}', 'else', '{', 'println("Estable")', '}'],
                            'story_success' => '✨ ¡MISIÓN COMPLETADA! El núcleo se enciende por completo. Draco vuelve a la vida — el viaje por Python, Java y Kotlin ha terminado.',
                            'story_fail' => 'Necesitas el if completo (con su bloque) y el else completo (con el suyo).',
                        ],
                        'hint' => 'if (condición) { acción } else { otra acción }',
                        'difficulty' => 'intermediate', 'sort_order' => 9,
                    ],
                ],
            ],
        ];

        foreach ($chapters as $slug => $chapter) {
            $lesson = Lesson::where('slug', $slug)->first();

            if (! $lesson) {
                $this->command->warn("Lesson '{$slug}' not found — skipping. Run LessonSeeder first.");
                continue;
            }

            $language = $chapter['language'];
            $lessonExercises = $chapter['items'];

            // Limpia ejercicios de un idioma/estructura anterior (p. ej. estas lecciones
            // eran solo Kotlin antes de convertirse en capítulos Python/Java/Kotlin).
            Exercise::where('lesson_id', $lesson->id)->where('language', '!=', $language)->delete();
            Exercise::where('lesson_id', $lesson->id)->where('language', $language)
                ->where('sort_order', '>', count($lessonExercises))->delete();

            foreach ($lessonExercises as $attrs) {
                Exercise::updateOrCreate(
                    [
                        'lesson_id'  => $lesson->id,
                        'type'       => $attrs['type'],
                        'language'   => $language,
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

            $this->command->info("Seeded " . count($lessonExercises) . " exercises for '{$slug}' ({$language}).");
        }
    }
}
