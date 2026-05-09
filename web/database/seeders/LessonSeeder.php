<?php

namespace Database\Seeders;

use App\Models\Lesson;
use Illuminate\Database\Seeder;

class LessonSeeder extends Seeder
{
    public function run(): void
    {
        $soloExercises = [
            'decisiones_de_fuego' => [
                ['id'=>1,'order'=>1,'type'=>'multiple','question'=>'🔥 Draco entra a la Cueva de Fuego con una antorcha mágica que no debe cambiar. ¿Qué palabra usa en Kotlin para declararla?','options'=>['var','val','let','const'],'correct_answer'=>'val'],
                ['id'=>2,'order'=>2,'type'=>'fill','question'=>'Completa el hechizo: _____ antorcha: String = "Fuego Eterno"','correct_answer'=>'val'],
                ['id'=>3,'order'=>3,'type'=>'order','question'=>'Ordena la declaración de la antorcha inmutable de Draco.','items'=>[': String','"Fuego Eterno"','val','antorcha','='],'correct_answer'=>['val','antorcha',': String','=','"Fuego Eterno"']],
                ['id'=>4,'order'=>4,'type'=>'multiple','question'=>'Draco encuentra una llave que no debe cambiar durante la misión. ¿Cuál declaración es correcta?','options'=>['var llave: String = "Draco"','val llave: String = "Draco"','val llave = 123','println(llave)'],'correct_answer'=>'val llave: String = "Draco"'],
                ['id'=>5,'order'=>5,'type'=>'true_false','question'=>'Si Draco declara una variable con val, puede cambiar su valor después.','options'=>['Verdadero','Falso'],'correct_answer'=>'Falso'],
                ['id'=>6,'order'=>6,'type'=>'multiple','question'=>'Draco revisa sus vidas antes de cruzar la lava. ¿Qué tipo representa un número entero en Kotlin?','options'=>['String','Boolean','Int','Float'],'correct_answer'=>'Int'],
                ['id'=>7,'order'=>7,'type'=>'fill','question'=>'Completa el contador de vidas de Draco: val vidas: _____ = 3','correct_answer'=>'Int'],
                ['id'=>8,'order'=>8,'type'=>'multiple','question'=>'El escudo de Draco no debe cambiar durante la batalla final. ¿Qué decisión es mejor?','options'=>['Usar var','Usar val','No declararlo','Usar while'],'correct_answer'=>'Usar val'],
            ],

            'vuelo_infinito' => [
                ['id'=>1,'order'=>1,'type'=>'multiple','question'=>'🐉 Draco debe volar 5 veces para entrenar resistencia. ¿Qué instrucción repite un bloque 5 veces en Kotlin?','options'=>['repeat(5) { }','while(false) { }','if(5) { }','val vuelos = 5'],'correct_answer'=>'repeat(5) { }'],
                ['id'=>2,'order'=>2,'type'=>'fill','question'=>'Completa el entrenamiento: repeat(_____) { volar() }','correct_answer'=>'5'],
                ['id'=>3,'order'=>3,'type'=>'order','question'=>'Ordena el bucle para que Draco complete su entrenamiento de vuelo.','items'=>['println("Vuelo completado")','repeat(5)','}','{'],'correct_answer'=>['repeat(5)','{','println("Vuelo completado")','}']],
                ['id'=>4,'order'=>4,'type'=>'true_false','question'=>'repeat(5) ejecuta el bloque exactamente cinco veces.','options'=>['Verdadero','Falso'],'correct_answer'=>'Verdadero'],
                ['id'=>5,'order'=>5,'type'=>'multiple','question'=>'Draco quiere volar mientras tenga energía mayor a 20. ¿Qué estructura debe usar?','options'=>['while','val','String','println'],'correct_answer'=>'while'],
                ['id'=>6,'order'=>6,'type'=>'fill','question'=>'Completa el vuelo seguro: while (energia > _____) { volar() }','correct_answer'=>'20'],
                ['id'=>7,'order'=>7,'type'=>'multiple','question'=>'Draco casi entra en un vuelo infinito. ¿Qué ayuda a evitar un bucle infinito?','options'=>['Actualizar la condición de energía','Quitar variables','Usar solo println','No cerrar llaves'],'correct_answer'=>'Actualizar la condición de energía'],
                ['id'=>8,'order'=>8,'type'=>'multiple','question'=>'Para cerrar el entrenamiento, Draco necesita repetir 5 vuelos claramente. ¿Qué código es mejor?','options'=>['repeat(5) { entrenarVuelo() }','if entrenar 5','var vuelos = 5','while(true)'],'correct_answer'=>'repeat(5) { entrenarVuelo() }'],
            ],

            'el_libro_de_tareas' => [
                ['id'=>1,'order'=>1,'type'=>'multiple','question'=>'📖 El Libro Mágico de Draco guarda varias misiones. ¿Qué estructura permite guardar varias tareas?','options'=>['Lista','Boolean','Int','if'],'correct_answer'=>'Lista'],
               ['id'=>2,'order'=>2,'type'=>'fill','question'=>'📖 El Libro Mágico muestra tres misiones del día:\n\n1. estudiar\n2. volar\n3. descansar\n\nCompleta la tercera misión:\nval tareas = listOf("estudiar", "volar", "_____")','correct_answer'=>'descansar'],
                ['id'=>3,'order'=>3,'type'=>'multiple','question'=>'Draco pierde energía y debe activar su escudo. ¿Qué condición lo activa correctamente?','options'=>['vida < 20','vida == 100','tareas > vida','repeat vida'],'correct_answer'=>'vida < 20'],
                ['id'=>4,'order'=>4,'type'=>'order','question'=>'Ordena el hechizo condicional para activar el escudo de Draco.','items'=>['activarEscudo()','if','}','(vida < 20)','{'],'correct_answer'=>['if','(vida < 20)','{','activarEscudo()','}']],
                ['id'=>5,'order'=>5,'type'=>'true_false','question'=>'Dividir tareas grandes en pequeñas ayuda a Draco a completarlas mejor.','options'=>['Verdadero','Falso'],'correct_answer'=>'Verdadero'],
                ['id'=>6,'order'=>6,'type'=>'multiple','question'=>'Draco debe organizar su día. ¿Qué tarea debe ir primero en su libro mágico?','options'=>['La más importante','La última que recuerde','La más larga','Ninguna'],'correct_answer'=>'La más importante'],
                ['id'=>7,'order'=>7,'type'=>'fill','question'=>'Completa el hechizo del escudo: if (_____ < 20) { activarEscudo() }','correct_answer'=>'vida'],
                ['id'=>8,'order'=>8,'type'=>'multiple','question'=>'Al final de la misión, ¿qué hace un buen libro de tareas para Draco?','options'=>['Ordena prioridades','Duplica trabajo','Borra objetivos','Evita revisar'],'correct_answer'=>'Ordena prioridades'],
            ],
        ];

        $lessons = [
            [
                'slug' => 'decisiones_de_fuego',
                'title' => 'Decisiones de Fuego',
                'description' => 'Draco entra a una cueva de lava y debe usar variables, tipos y decisiones correctas para sobrevivir.',
                'content' => [
                    'puzzle_pieces' => ['val', 'antorcha', '=', '"Fuego Eterno"', ': String', 'var'],
                    'solution' => ['val', 'antorcha', ': String', '=', '"Fuego Eterno"'],
                    'quiz_options' => ['var llave: String = "Draco"', 'val llave: String = "Draco"', 'val llave = 123', 'println(llave)'],
                    'correct_quiz_index' => 1,
                    'fill_line' => '_____ antorcha: String = "Fuego Eterno"',
                    'fill_answer' => 'val',
                ],
                'exercises' => $soloExercises['decisiones_de_fuego'],
                'difficulty' => 'beginner',
                'type' => 'solitario',
                'xp_reward' => 50,
                'order' => 1,
            ],
            [
                'slug' => 'vuelo_infinito',
                'title' => 'Vuelo Infinito',
                'description' => 'Draco aprende a controlar su vuelo usando repeticiones, bucles y energía para no caer en un ciclo infinito.',
                'content' => [
                    'puzzle_pieces' => ['repeat(5)', '{', '}', 'while', 'energia > 20'],
                    'solution' => ['repeat(5)', '{', '}'],
                    'quiz_options' => ['repeat(5) { }', 'while(false) { }', 'if(5) { }', 'val vuelos = 5'],
                    'correct_quiz_index' => 0,
                    'fill_line' => 'repeat(_____) { volar() }',
                    'fill_answer' => '5',
                    'extended_exercise' => 'Draco debe repetir vuelos de entrenamiento y detenerse cuando su energía sea baja.',
                ],
                'exercises' => $soloExercises['vuelo_infinito'],
                'difficulty' => 'beginner',
                'type' => 'solitario',
                'xp_reward' => 100,
                'order' => 2,
            ],
            [
                'slug' => 'el_libro_de_tareas',
                'title' => 'El Libro de las Tareas',
                'description' => 'Draco usa un libro mágico para ordenar misiones, crear listas y activar su escudo cuando su vida baja.',
                'content' => [
                    'puzzle_pieces' => ['if', '(vida < 20)', 'activarEscudo()', '{', '}', 'listOf'],
                    'solution' => ['if', '(vida < 20)', '{', 'activarEscudo()', '}'],
                    'quiz_options' => ['vida < 20', 'vida == 100', 'tareas > vida', 'repeat vida'],
                    'correct_quiz_index' => 0,
                    'fill_line' => 'if (______ < 20) { activarEscudo() }',
                    'fill_answer' => 'vida',
                    'extended_exercise' => 'Draco crea una lista de tareas mágicas y decide cuál completar primero según su prioridad.',
                ],
                'exercises' => $soloExercises['el_libro_de_tareas'],
                'difficulty' => 'intermediate',
                'type' => 'solitario',
                'xp_reward' => 150,
                'order' => 3,
            ],

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
                'description' => 'Draco está entrenando para un gran torneo de vuelo. Registra los resultados de varios entrenamientos. Al final, quiere conocer su promedio y mejor resultado.',
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
                'description' => 'Draco está entrenando para un gran torneo de vuelo. Registra los resultados de varios entrenamientos. Al final, quiere conocer su promedio y mejor resultado.',
                'content' => [
                    'context' => 'Ciclos, listas y acumuladores',
                    'task' => 'Redacta el pseudocódigo que indique cómo solicitar datos, almacenarlos, calcular promedio y obtener el máximo.',
                ],
                'difficulty' => 'advanced',
                'type' => 'grupal',
                'xp_reward' => 250,
                'order' => 7,
            ],
            [
                'slug' => 'acertijos_programador',
                'title' => 'El Reto de los Acertijos (Rol: Programador)',
                'description' => 'Draco debe resolver tres acertijos mágicos para abrir una puerta secreta.',
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
                'description' => 'Draco debe resolver tres acertijos mágicos para abrir una puerta secreta.',
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