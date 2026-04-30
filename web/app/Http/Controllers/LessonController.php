<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\Lesson;
use App\Models\User;
use App\Models\UserProgress;
use Carbon\Carbon;

class LessonController extends Controller
{
    /**
     * Bank of exercises per lesson topic.
     */
    private function getLessonExercises(): array
    {
        return [
            'variables' => [
                [
                    'question'    => 'Escribe la palabra reservada para declarar una variable que pueda cambiar de valor:',
                    'code_before' => '',
                    'code_after'  => ' vidas = 10;',
                    'answer'      => 'let',
                    'hint'        => 'Pista: Empieza con "l" y tiene 3 letras.',
                ],
                [
                    'question'    => 'Escribe la palabra reservada para declarar una constante:',
                    'code_before' => '',
                    'code_after'  => ' PI = 3.14;',
                    'answer'      => 'const',
                    'hint'        => 'Pista: Viene de "constante".',
                ],
                [
                    'question'    => '¿Qué tipo de dato es "Hola Mundo"?',
                    'code_before' => 'typeof "Hola Mundo" // → ',
                    'code_after'  => '',
                    'answer'      => 'string',
                    'hint'        => 'Pista: Texto entre comillas es un...',
                ],
                [
                    'question'    => '¿Qué tipo de dato es el número 42?',
                    'code_before' => 'typeof 42 // → ',
                    'code_after'  => '',
                    'answer'      => 'int',
                    'hint'        => 'Pista: Los números son de tipo...',
                ],
                [
                    'question'    => '¿Qué tipo de dato es true?',
                    'code_before' => 'typeof true // → ',
                    'code_after'  => '',
                    'answer'      => 'boolean',
                    'hint'        => 'Pista: Solo puede ser verdadero o falso.',
                ],
                [
                    'question'    => 'Completa para crear una variable con var:',
                    'code_before' => '',
                    'code_after'  => ' nombre = "Draco";',
                    'answer'      => 'var',
                    'hint'        => 'Pista: La forma antigua de declarar variables.',
                ],
                [
                    'question'    => '¿Qué operador se usa para asignar un valor a una variable?',
                    'code_before' => 'let edad ',
                    'code_after'  => ' 25;',
                    'answer'      => '=',
                    'hint'        => 'Pista: Es el signo de igual.',
                ],
                [
                    'question'    => '¿Qué valor tiene una variable declarada pero sin asignar?',
                    'code_before' => 'let x; console.log(x); // → ',
                    'code_after'  => '',
                    'answer'      => 'undefined',
                    'hint'        => 'Pista: Significa "no definido".',
                ],
            ],

            'operadores' => [
                [
                    'question'    => '¿Qué operador se usa para sumar dos números?',
                    'code_before' => '5 ',
                    'code_after'  => ' 3 // → 8',
                    'answer'      => '+',
                    'hint'        => 'Pista: Es el signo de adición.',
                ],
                [
                    'question'    => '¿Qué operador se usa para restar?',
                    'code_before' => '10 ',
                    'code_after'  => ' 4 // → 6',
                    'answer'      => '-',
                    'hint'        => 'Pista: Es el signo de sustracción.',
                ],
                [
                    'question'    => '¿Qué operador multiplica dos valores?',
                    'code_before' => '3 ',
                    'code_after'  => ' 7 // → 21',
                    'answer'      => '*',
                    'hint'        => 'Pista: Es un asterisco.',
                ],
                [
                    'question'    => '¿Qué operador divide dos valores?',
                    'code_before' => '20 ',
                    'code_after'  => ' 5 // → 4',
                    'answer'      => '/',
                    'hint'        => 'Pista: Es la barra diagonal.',
                ],
                [
                    'question'    => '¿Qué operador obtiene el residuo de una división?',
                    'code_before' => '10 ',
                    'code_after'  => ' 3 // → 1',
                    'answer'      => '%',
                    'hint'        => 'Pista: También se llama módulo.',
                ],
                [
                    'question'    => '¿Cómo se compara si dos valores son iguales (estricto)?',
                    'code_before' => '5 ',
                    'code_after'  => ' 5 // → true',
                    'answer'      => '==',
                    'hint'        => 'Pista: Son 2 signos de igual.',
                ],
                [
                    'question'    => '¿Qué operador lógico representa el "Y"?',
                    'code_before' => 'true ',
                    'code_after'  => ' false // → false',
                    'answer'      => '&&',
                    'hint'        => 'Pista: Son dos ampersand.',
                ],
                [
                    'question'    => '¿Qué operador lógico representa el "O"?',
                    'code_before' => 'true ',
                    'code_after'  => ' false // → true',
                    'answer'      => '||',
                    'hint'        => 'Pista: Son dos barras verticales.',
                ],
            ],

            'bucles' => [
                [
                    'question'    => 'Escribe la palabra clave para un bucle con contador:',
                    'code_before' => '',
                    'code_after'  => ' (let i = 0; i < 5; i++) { }',
                    'answer'      => 'for',
                    'hint'        => 'Pista: Empieza con "f" y tiene 3 letras.',
                ],
                [
                    'question'    => 'Escribe la palabra clave para un bucle condicional:',
                    'code_before' => '',
                    'code_after'  => ' (condicion) { }',
                    'answer'      => 'while',
                    'hint'        => 'Pista: Significa "mientras" en inglés.',
                ],
                [
                    'question'    => '¿Qué palabra se usa para salir de un bucle?',
                    'code_before' => 'if (i === 3) ',
                    'code_after'  => ';',
                    'answer'      => 'break',
                    'hint'        => 'Pista: Significa "romper" en inglés.',
                ],
                [
                    'question'    => '¿Qué palabra salta a la siguiente iteración?',
                    'code_before' => 'if (i === 2) ',
                    'code_after'  => ';',
                    'answer'      => 'continue',
                    'hint'        => 'Pista: Significa "continuar" en inglés.',
                ],
                [
                    'question'    => 'Completa el inicio del for:',
                    'code_before' => 'for (',
                    'code_after'  => ' i = 0; i < 10; i++)',
                    'answer'      => 'let',
                    'hint'        => 'Pista: Se declara la variable del contador.',
                ],
                [
                    'question'    => '¿Qué operador incrementa i en 1?',
                    'code_before' => 'i',
                    'code_after'  => ' // i = i + 1',
                    'answer'      => '++',
                    'hint'        => 'Pista: Son dos signos de más.',
                ],
                [
                    'question'    => '¿Cuántas veces se ejecuta: for(i=0; i<3; i++)?',
                    'code_before' => '// respuesta: ',
                    'code_after'  => ' veces',
                    'answer'      => '3',
                    'hint'        => 'Pista: i va de 0, 1, 2.',
                ],
                [
                    'question'    => 'Escribe el bucle que se ejecuta al menos una vez:',
                    'code_before' => '',
                    'code_after'  => ' { } while(condicion);',
                    'answer'      => 'do',
                    'hint'        => 'Pista: Significa "hacer" en inglés.',
                ],
            ],

            'funciones' => [
                [
                    'question'    => 'Escribe la palabra para declarar una función:',
                    'code_before' => '',
                    'code_after'  => ' saludar() { }',
                    'answer'      => 'function',
                    'hint'        => 'Pista: Completa "func...".',
                ],
                [
                    'question'    => '¿Qué palabra devuelve un valor desde una función?',
                    'code_before' => 'function suma(a,b) { ',
                    'code_after'  => ' a + b; }',
                    'answer'      => 'return',
                    'hint'        => 'Pista: Significa "retornar".',
                ],
                [
                    'question'    => 'Completa la función flecha:',
                    'code_before' => 'const doble = (x) ',
                    'code_after'  => ' x * 2;',
                    'answer'      => '=>',
                    'hint'        => 'Pista: Es igual seguido de mayor que.',
                ],
                [
                    'question'    => '¿Cómo se llama a una función llamada "miFuncion"?',
                    'code_before' => '',
                    'code_after'  => ';',
                    'answer'      => 'miFuncion()',
                    'hint'        => 'Pista: Nombre seguido de paréntesis.',
                ],
                [
                    'question'    => '¿Cómo se llaman los valores que recibe una función?',
                    'code_before' => 'function saludar(',
                    'code_after'  => ') { }  // se llaman...',
                    'answer'      => 'nombre',
                    'hint'        => 'Pista: Son los parámetros de la función. Escribe "nombre".',
                ],
                [
                    'question'    => 'Escribe la palabra para declarar una función flecha constante:',
                    'code_before' => '',
                    'code_after'  => ' sumar = (a, b) => a + b;',
                    'answer'      => 'const',
                    'hint'        => 'Pista: Las funciones flecha se guardan en constantes.',
                ],
                [
                    'question'    => '¿Qué imprime: function f(){return 5;} console.log(f())?',
                    'code_before' => '// imprime: ',
                    'code_after'  => '',
                    'answer'      => '5',
                    'hint'        => 'Pista: La función retorna un número.',
                ],
                [
                    'question'    => '¿Qué retorna una función sin return?',
                    'code_before' => 'function vacia(){} // retorna: ',
                    'code_after'  => '',
                    'answer'      => 'undefined',
                    'hint'        => 'Pista: Sin return explícito, el valor es...',
                ],
            ],

            'pseudocodigo' => [
                [
                    'question'    => '¿Qué palabra inicia un bloque condicional en pseudocódigo?',
                    'code_before' => '',
                    'code_after'  => ' (edad >= 18) ENTONCES',
                    'answer'      => 'SI',
                    'hint'        => 'Pista: Equivale a "if" en español.',
                ],
                [
                    'question'    => '¿Qué palabra se usa para la alternativa en pseudocódigo?',
                    'code_before' => 'SI ... ENTONCES ... ',
                    'code_after'  => '',
                    'answer'      => 'SINO',
                    'hint'        => 'Pista: Equivale a "else".',
                ],
                [
                    'question'    => '¿Qué instrucción muestra datos al usuario?',
                    'code_before' => '',
                    'code_after'  => ' "Hola Mundo"',
                    'answer'      => 'ESCRIBIR',
                    'hint'        => 'Pista: Equivale a "print" o "console.log".',
                ],
                [
                    'question'    => '¿Qué instrucción captura datos del usuario?',
                    'code_before' => '',
                    'code_after'  => ' nombre',
                    'answer'      => 'LEER',
                    'hint'        => 'Pista: Equivale a "input" o "read".',
                ],
                [
                    'question'    => '¿Qué instrucción repite MIENTRAS se cumpla una condición?',
                    'code_before' => '',
                    'code_after'  => ' (contador < 10) HACER',
                    'answer'      => 'MIENTRAS',
                    'hint'        => 'Pista: Equivale a "while".',
                ],
                [
                    'question'    => '¿Qué instrucción repite con un rango definido?',
                    'code_before' => '',
                    'code_after'  => ' i = 1 HASTA 10 HACER',
                    'answer'      => 'PARA',
                    'hint'        => 'Pista: Equivale a "for".',
                ],
                [
                    'question'    => '¿Con qué palabra se cierra un bloque SI?',
                    'code_before' => 'SI ... ENTONCES ... SINO ... ',
                    'code_after'  => '',
                    'answer'      => 'FINSI',
                    'hint'        => 'Pista: Es "FIN" + "SI" junto.',
                ],
                [
                    'question'    => '¿Con qué se inicia un algoritmo en pseudocódigo?',
                    'code_before' => '',
                    'code_after'  => ' NombreAlgoritmo',
                    'answer'      => 'ALGORITMO',
                    'hint'        => 'Pista: Es la primera línea del pseudocódigo.',
                ],
            ],

            'poo' => [
                [
                    'question'    => 'Escribe la palabra para declarar una clase:',
                    'code_before' => '',
                    'code_after'  => ' Animal { }',
                    'answer'      => 'class',
                    'hint'        => 'Pista: Empieza con "cl" y tiene 5 letras.',
                ],
                [
                    'question'    => '¿Qué método se ejecuta al crear un objeto?',
                    'code_before' => 'class Auto { ',
                    'code_after'  => '() { } }',
                    'answer'      => 'constructor',
                    'hint'        => 'Pista: Es el método inicializador.',
                ],
                [
                    'question'    => '¿Qué palabra crea un nuevo objeto de una clase?',
                    'code_before' => 'let miAuto = ',
                    'code_after'  => ' Auto();',
                    'answer'      => 'new',
                    'hint'        => 'Pista: Significa "nuevo" en inglés.',
                ],
                [
                    'question'    => '¿Qué palabra indica herencia de otra clase?',
                    'code_before' => 'class Gato ',
                    'code_after'  => ' Animal { }',
                    'answer'      => 'extends',
                    'hint'        => 'Pista: Significa "extiende".',
                ],
                [
                    'question'    => '¿Qué palabra referencia al objeto actual dentro de la clase?',
                    'code_before' => '',
                    'code_after'  => '.nombre = "Draco";',
                    'answer'      => 'this',
                    'hint'        => 'Pista: Significa "este" en inglés.',
                ],
                [
                    'question'    => '¿Qué palabra llama al constructor padre?',
                    'code_before' => 'class Gato extends Animal { constructor() { ',
                    'code_after'  => '(); } }',
                    'answer'      => 'super',
                    'hint'        => 'Pista: Significa "superior".',
                ],
                [
                    'question'    => '¿Cómo se llama el pilar de POO que oculta datos internos?',
                    'code_before' => '// Pilar POO: ',
                    'code_after'  => '',
                    'answer'      => 'encapsulamiento',
                    'hint'        => 'Pista: Ocultar los detalles internos del objeto.',
                ],
                [
                    'question'    => '¿Cómo se llama el pilar de POO donde una clase hereda de otra?',
                    'code_before' => '// Pilar POO: ',
                    'code_after'  => '',
                    'answer'      => 'herencia',
                    'hint'        => 'Pista: Heredar propiedades y métodos.',
                ],
            ],
        ];
    }

    public function show(Request $request, $slug = null)
    {
        // Use topic query param if provided, otherwise slug, otherwise default to variables
        $topic = $request->query('topic') ?? $slug ?? 'variables';

        $lesson = Lesson::where('slug', $topic)->first();

        if (!$lesson) {
            return redirect()->route('dashboard')->with('error', 'Lección no encontrada.');
        }

        $allExercises = $this->getLessonExercises();
        $exercises = $allExercises[$topic] ?? $allExercises['variables'];

        return view('lesson', compact('lesson', 'exercises'));
    }

    public function complete(Request $request, $slug = null)
    {
        $lesson = Lesson::where('slug', $slug)->first();

        if (!$lesson) {
            return redirect()->route('dashboard')->with('error', 'Error al procesar la lección.');
        }

        // Use authenticated user
        $user = auth()->user();

        // 1. Record progress
        UserProgress::updateOrCreate(
            [
                'user_id' => $user->id,
                'lesson_id' => $lesson->id,
            ],
            [
                'score' => 100, // Completed
                'completed_at' => now(),
            ]
        );

        // 2. Add XP to user
        $user->total_xp += $lesson->xp_reward;
        $user->save();

        return redirect()->route('dashboard')->with('success', "¡Lección completada! +{$lesson->xp_reward} XP 🎉");
    }
}
