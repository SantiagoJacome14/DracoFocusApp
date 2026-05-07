<?php

namespace App\Data;

class ExerciseBank
{
    /**
     * Obtiene el banco completo filtrado por tema.
     */
    public static function get(string $topic): array
    {
        return match ($topic) {
            'variables'    => self::getVariables(),
            'operadores'   => self::getOperadores(),
            'bucles'       => self::getBucles(),
            'funciones'    => self::getFunciones(),
            'pseudocodigo' => self::getPseudocodigo(),
            'poo'          => self::getPOO(),
            default        => self::getVariables(),
        };
    }

    /**
     * Selecciona N ejercicios aleatorios de un tema.
     */
    public static function random(string $topic, int $count = 8): array
    {
        $bank = self::get($topic);
        shuffle($bank);
        return array_slice($bank, 0, $count);
    }

    private static function getVariables(): array
    {
        return [
            ['question'=>'Declarar variable que puede cambiar:','code_before'=>'','code_after'=>' puntos = 0;','answer'=>'let','hint'=>'3 letras, empieza con l.'],
            ['question'=>'Declarar valor inmutable:','code_before'=>'','code_after'=>' GRAVEDAD = 9.8;','answer'=>'const','hint'=>'Abreviatura de constante.'],
            ['question'=>'Tipo de dato para texto:','code_before'=>'let msg = "Hola"; // Tipo: ','code_after'=>'','answer'=>'string','hint'=>'Cadena en inglés.'],
            ['question'=>'Tipo de dato para números enteros:','code_before'=>'let x = 10; // Tipo: ','code_after'=>'','answer'=>'number','hint'=>'En JS, todos los números son...'],
            ['question'=>'Valor de variable sin inicializar:','code_before'=>'let a; // a es: ','code_after'=>'','answer'=>'undefined','hint'=>'No definido.'],
            ['question'=>'Declaración antigua de variables:','code_before'=>'','code_after'=>' global = true;','answer'=>'var','hint'=>'3 letras, evitar su uso.'],
            ['question'=>'¿Qué tipo es true?','code_before'=>'typeof true; // → ','code_after'=>'','answer'=>'boolean','hint'=>'Lógica booleana.'],
            ['question'=>'Convertir string a número entero:','code_before'=>'','code_after'=>'("10");','answer'=>'parseInt','hint'=>'Parse + Int.'],
            ['question'=>'Convertir string a número decimal:','code_before'=>'','code_after'=>'("10.5");','answer'=>'parseFloat','hint'=>'Parse + Float.'],
            ['question'=>'Verificar tipo de una variable:','code_before'=>'','code_after'=>' x;','answer'=>'typeof','hint'=>'Type of...'],
            ['question'=>'¿Es null un objeto?','code_before'=>'typeof null; // → ','code_after'=>'','answer'=>'object','hint'=>'Error histórico de JS.'],
            ['question'=>'Símbolo para comentarios de una línea:','code_before'=>'','code_after'=>' Mi comentario','answer'=>'//','hint'=>'Doble barra.'],
            ['question'=>'Representa ausencia intencional de valor:','code_before'=>'let x = ','code_after'=>';','answer'=>'null','hint'=>'Nulo.'],
            ['question'=>'Nombre de variable con varias palabras:','code_before'=>'let ','code_after'=>'Usuario;','answer'=>'nombre','hint'=>'Usa camelCase.'],
            ['question'=>'¿Qué imprime "5" + 5?','code_before'=>'','code_after'=>' // como string','answer'=>'55','hint'=>'Coerción de tipos.'],
            ['question'=>'Tipo de dato para listas:','code_before'=>'let lista = ','code_after'=>'1,2,3];','answer'=>'[','hint'=>'Corchete.'],
            ['question'=>'Acceder al primer elemento de un array:','code_before'=>'arr','code_after'=>' ','answer'=>'[0]','hint'=>'Índice cero.'],
            ['question'=>'Propiedad para longitud de texto:','code_before'=>'"Hola".','code_after'=>'','answer'=>'length','hint'=>'Largo en inglés.'],
            ['question'=>'Interpolación de variables (template string):','code_before'=>'`Hola ${','code_after'=>' Strength}`','answer'=>'nombre','hint'=>'Usa el signo pesos.'],
            ['question'=>'¿Qué valor tiene NaN?','code_before'=>'typeof NaN; // → ','code_after'=>'','answer'=>'number','hint'=>'Not a Number es un...'],
            ['question'=>'Declarar múltiples variables:','code_before'=>'let a=1','code_after'=>' b=2;','answer'=>',','hint'=>'Usa coma.'],
            ['question'=>'¿Variable local o global?','code_before'=>'// let dentro de bloque es: ','code_after'=>'','answer'=>'local','hint'=>'Ámbito limitado.'],
            ['question'=>'¿Qué tipo es Symbol()?','code_before'=>'typeof Symbol(); // → ','code_after'=>'','answer'=>'symbol','hint'=>'Tipo primitivo único.'],
            ['question'=>'¿Qué tipo es BigInt(10)?','code_before'=>'typeof 10n; // → ','code_after'=>'','answer'=>'bigint','hint'=>'Números muy grandes.'],
            ['question'=>'Símbolo para template strings:','code_before'=>'','code_after'=>'texto${v}','answer'=>'`','hint'=>'Acento grave.'],
            ['question'=>'¿Se puede reasignar un const?','code_before'=>'// Respuesta (si/no): ','code_after'=>'','answer'=>'no','hint'=>'Es inmutable.'],
            ['question'=>'Extraer parte de un string:','code_before'=>'str.','code_after'=>'(0, 2);','answer'=>'slice','hint'=>'Rebanar.'],
            ['question'=>'Pasar a mayúsculas:','code_before'=>'str.','code_after'=>'();','answer'=>'toUpperCase','hint'=>'To Upper Case.'],
            ['question'=>'Pasar a minúsculas:','code_before'=>'str.','code_after'=>'();','answer'=>'toLowerCase','hint'=>'To Lower Case.'],
            ['question'=>'Quitar espacios al inicio y final:','code_before'=>'str.','code_after'=>'();','answer'=>'trim','hint'=>'Recortar.'],
            ['question'=>'¿Cómo se llama "let x = 10"?','code_before'=>'// Es una ','code_after'=>'','answer'=>'asignacion','hint'=>'Dar valor.'],
            ['question'=>'¿Qué imprime console.log(1 / 0)?','code_before'=>'','code_after'=>'','answer'=>'Infinity','hint'=>'Infinito.'],
            ['question'=>'¿Qué imprime console.log(0 / 0)?','code_before'=>'','code_after'=>'','answer'=>'NaN','hint'=>'No es un número.'],
            ['question'=>'Definir un objeto vacío:','code_before'=>'let obj = ','code_after'=>';','answer'=>'{}','hint'=>'Llaves.'],
            ['question'=>'¿Cómo se llama la parte "x" en "let x"?','code_before'=>'// Es el ','code_after'=>'','answer'=>'identificador','hint'=>'Nombre técnico.'],
        ];
    }

    private static function getOperadores(): array
    {
        return [
            ['question'=>'Suma:','code_before'=>'10 ','code_after'=>' 5 // → 15','answer'=>'+','hint'=>'Plus.'],
            ['question'=>'Resta:','code_before'=>'20 ','code_after'=>' 8 // → 12','answer'=>'-','hint'=>'Minus.'],
            ['question'=>'Multiplicación:','code_before'=>'4 ','code_after'=>' 3 // → 12','answer'=>'*','hint'=>'Asterisco.'],
            ['question'=>'División:','code_before'=>'10 ','code_after'=>' 2 // → 5','answer'=>'/','hint'=>'Barra.'],
            ['question'=>'Resto de división (módulo):','code_before'=>'10 ','code_after'=>' 3 // → 1','answer'=>'%','hint'=>'Porcentaje.'],
            ['question'=>'Igualdad simple (valor):','code_before'=>'5 ','code_after'=>' "5" // → true','answer'=>'==','hint'=>'Doble igual.'],
            ['question'=>'Igualdad estricta (valor y tipo):','code_before'=>'5 ','code_after'=>' 5 // → true','answer'=>'===','hint'=>'Triple igual.'],
            ['question'=>'Diferente de:','code_before'=>'5 ','code_after'=>' 3 // → true','answer'=>'!=','hint'=>'Exclamación e igual.'],
            ['question'=>'Mayor que:','code_before'=>'10 ','code_after'=>' 5','answer'=>'>','hint'=>'Símbolo mayor.'],
            ['question'=>'Menor que:','code_before'=>'2 ','code_after'=>' 8','answer'=>'<','hint'=>'Símbolo menor.'],
            ['question'=>'Lógico AND:','code_before'=>'true ','code_after'=>' false // → false','answer'=>'&&','hint'=>'Doble ampersand.'],
            ['question'=>'Lógico OR:','code_before'=>'true ','code_after'=>' false // → true','answer'=>'||','hint'=>'Doble pipe.'],
            ['question'=>'Lógico NOT:','code_before'=>'','code_after'=>'true // → false','answer'=>'!','hint'=>'Exclamación.'],
            ['question'=>'Asignación con suma:','code_before'=>'x ','code_after'=>' 5; // x = x + 5','answer'=>'+=','hint'=>'Suma e igual.'],
            ['question'=>'Potencia (exponente):','code_before'=>'2 ','code_after'=>' 3 // → 8','answer'=>'**','hint'=>'Doble asterisco.'],
            ['question'=>'Incremento en 1:','code_before'=>'i','code_after'=>' ','answer'=>'++','hint'=>'Doble más.'],
            ['question'=>'Decremento en 1:','code_before'=>'i','code_after'=>' ','answer'=>'--','hint'=>'Doble menos.'],
            ['question'=>'Ternario (condición):','code_before'=>'edad >= 18 ','code_after'=>' "Adulto" : "Menor"','answer'=>'?','hint'=>'Interrogación.'],
            ['question'=>'Ternario (separador):','code_before'=>'cond ? "A" ','code_after'=>' "B"','answer'=>':','hint'=>'Dos puntos.'],
            ['question'=>'Mayor o igual:','code_before'=>'x ','code_after'=>' 10','answer'=>'>=','hint'=>'Mayor + igual.'],
            ['question'=>'Menor o igual:','code_before'=>'x ','code_after'=>' 5','answer'=>'<=','hint'=>'Menor + igual.'],
            ['question'=>'Desplazamiento a la izquierda:','code_before'=>'5 ','code_after'=>' 1 // → 10','answer'=>'<<','hint'=>'Doble menor.'],
            ['question'=>'Operador Nullish Coalescing:','code_before'=>'user.name ','code_after'=>' "Invitado"','answer'=>'??','hint'=>'Doble interrogación.'],
            ['question'=>'Asignación de resto:','code_before'=>'x ','code_after'=>' 2; // x = x % 2','answer'=>'%=','hint'=>'Modulo igual.'],
            ['question'=>'¿Qué devuelve 10 === "10"?','code_before'=>'','code_after'=>'','answer'=>'false','hint'=>'Tipos diferentes.'],
            ['question'=>'¿Qué devuelve true && !false?','code_before'=>'','code_after'=>'','answer'=>'true','hint'=>'AND con NOT.'],
            ['question'=>'Pre-incremento:','code_before'=>'','code_after'=>'x','answer'=>'++','hint'=>'Antes de la variable.'],
            ['question'=>'Operador de bitwise AND:','code_before'=>'5 ','code_after'=>' 1','answer'=>'&','hint'=>'Un solo ampersand.'],
            ['question'=>'Operador de bitwise OR:','code_before'=>'5 ','code_after'=>' 1','answer'=>'|','hint'=>'Un solo pipe.'],
            ['question'=>'Operador in (propiedad en objeto):','code_before'=>'"name" ','code_after'=>' user','answer'=>'in','hint'=>'Palabra in.'],
            ['question'=>'Operador instanceof:','code_before'=>'obj ','code_after'=>' Array','answer'=>'instanceof','hint'=>'Instancia de.'],
            ['question'=>'Eliminar propiedad de objeto:','code_before'=>'','code_after'=>' user.age;','answer'=>'delete','hint'=>'Borrar.'],
            ['question'=>'Operador void:','code_before'=>'','code_after'=>' 0;','answer'=>'void','hint'=>'Vacío.'],
            ['question'=>'¿Prioridad de operadores: * o +?','code_before'=>'// El mayor es: ','code_after'=>'','answer'=>'*','hint'=>'Matemática básica.'],
            ['question'=>'¿Prioridad: && o ||?','code_before'=>'// El mayor es: ','code_after'=>'','answer'=>'&&','hint'=>'AND va primero.'],
        ];
    }

    private static function getBucles(): array
    {
        return [
            ['question'=>'Bucle con contador definido:','code_before'=>'','code_after'=>' (let i=0; i<10; i++)','answer'=>'for','hint'=>'Para.'],
            ['question'=>'Bucle mientras sea cierto:','code_before'=>'','code_after'=>' (condicion)','answer'=>'while','hint'=>'Mientras.'],
            ['question'=>'Bucle que ejecuta al menos una vez:','code_before'=>'','code_after'=>' { } while(cond);','answer'=>'do','hint'=>'Hacer.'],
            ['question'=>'Salir de un bucle:','code_before'=>'if(c) ','code_after'=>';','answer'=>'break','hint'=>'Romper.'],
            ['question'=>'Saltar a la siguiente iteración:','code_before'=>'if(c) ','code_after'=>';','answer'=>'continue','hint'=>'Continuar.'],
            ['question'=>'Recorrer elementos de un array:','code_before'=>'for(let x ','code_after'=>' array)','answer'=>'of','hint'=>'for...of.'],
            ['question'=>'Recorrer llaves de un objeto:','code_before'=>'for(let k ','code_after'=>' objeto)','answer'=>'in','hint'=>'for...in.'],
            ['question'=>'¿Cuántas veces corre for(i=0;i<5;i++)?','code_before'=>'','code_after'=>'','answer'=>'5','hint'=>'De 0 a 4.'],
            ['question'=>'Iterar array con callback:','code_before'=>'arr.','code_after'=>'(x => ...)','answer'=>'forEach','hint'=>'Para cada.'],
            ['question'=>'Condición en un for (parte 2):','code_before'=>'for(init; ','code_after'=>'; inc)','answer'=>'condicion','hint'=>'Lo que se evalúa.'],
            ['question'=>'Actualización en un for (parte 3):','code_before'=>'for(init; cond; ','code_after'=>')','answer'=>'incremento','hint'=>'Suele ser i++.'],
            ['question'=>'¿Qué pasa si la condición es siempre true?','code_before'=>'// Bucle ','code_after'=>'','answer'=>'infinito','hint'=>'Sin fin.'],
            ['question'=>'Índice inicial común:','code_before'=>'for(let i = ','code_after'=>'; ...)','answer'=>'0','hint'=>'Base cero.'],
            ['question'=>'Bucle anidado:','code_before'=>'for(...) { ','code_after'=>' (...) { } }','answer'=>'for','hint'=>'Bucle dentro de bucle.'],
            ['question'=>'Recorrer un string carácter por carácter:','code_before'=>'for(let char ','code_after'=>' "Hola")','answer'=>'of','hint'=>'Usa of.'],
            ['question'=>'¿Qué imprime while(false)?','code_before'=>'// Ejecuciones: ','code_after'=>'','answer'=>'0','hint'=>'Nunca entra.'],
            ['question'=>'Map devuelve un nuevo...','code_before'=>'','code_after'=>'','answer'=>'array','hint'=>'Colección transformada.'],
            ['question'=>'Filtrar elementos:','code_before'=>'arr.','code_after'=>'(x => x > 0)','answer'=>'filter','hint'=>'Filtrar.'],
            ['question'=>'Reducir array a un valor:','code_before'=>'arr.','code_after'=>'((a,b) => a+b)','answer'=>'reduce','hint'=>'Reducir.'],
            ['question'=>'Buscar primer elemento que cumple:','code_before'=>'arr.','code_after'=>'(x => x === 5)','answer'=>'find','hint'=>'Buscar.'],
            ['question'=>'Verificar si alguno cumple:','code_before'=>'arr.','code_after'=>'(x => x > 10)','answer'=>'some','hint'=>'Alguno.'],
            ['question'=>'Verificar si todos cumplen:','code_before'=>'arr.','code_after'=>'(x => x > 0)','answer'=>'every','hint'=>'Todos.'],
            ['question'=>'¿Bucle para menú de opciones (al menos una vez)?','code_before'=>'','code_after'=>'','answer'=>'do while','hint'=>'Hacer mientras.'],
            ['question'=>'¿Bucle para recorrer objeto JSON?','code_before'=>'for(... ','code_after'=>' obj)','answer'=>'in','hint'=>'Usa in.'],
            ['question'=>'¿Qué imprime for(i=1;i<=3;i++)?','code_before'=>'// Valores: 1, 2, ','code_after'=>'','answer'=>'3','hint'=>'Menor o igual.'],
            ['question'=>'¿Sentencia para evitar un else if gigante?','code_before'=>'','code_after'=>'(val) { case 1: ... }','answer'=>'switch','hint'=>'Interruptor.'],
            ['question'=>'Caso por defecto en switch:','code_before'=>'','code_after'=>':','answer'=>'default','hint'=>'Defecto.'],
            ['question'=>'¿Qué palabra falta en case para no seguir?','code_before'=>'case 1: do(); ','code_after'=>'','answer'=>'break','hint'=>'Para salir.'],
            ['question'=>'Bucle sobre claves: Object.','code_before'=>'','code_after'=>'(obj)','answer'=>'keys','hint'=>'Llaves.'],
            ['question'=>'Bucle sobre valores: Object.','code_before'=>'','code_after'=>'(obj)','answer'=>'values','hint'=>'Valores.'],
            ['question'=>'Bucle sobre ambos: Object.','code_before'=>'','code_after'=>'(obj)','answer'=>'entries','hint'=>'Entradas.'],
            ['question'=>'¿while(1) es válido?','code_before'=>'// Respuesta (si/no): ','code_after'=>'','answer'=>'si','hint'=>'1 es truthy.'],
            ['question'=>'Detener ejecución 1 seg (conceptual):','code_before'=>'','code_after'=>'(1000)','answer'=>'sleep','hint'=>'Dormir.'],
            ['question'=>'Iterador manual:','code_before'=>'gen.','code_after'=>'()','answer'=>'next','hint'=>'Siguiente.'],
            ['question'=>'¿Cómo se llama "i" en un bucle?','code_before'=>'// Es la variable ','code_after'=>'','answer'=>'de control','hint'=>'Maneja el flujo.'],
        ];
    }

    private static function getFunciones(): array
    {
        return [
            ['question'=>'Declarar función estándar:','code_before'=>'','code_after'=>' suma(a, b) { }','answer'=>'function','hint'=>'Palabra clave.'],
            ['question'=>'Retornar un valor:','code_before'=>'function f() { ','code_after'=>' 10; }','answer'=>'return','hint'=>'Regresar.'],
            ['question'=>'Función de flecha (arrow):','code_before'=>'const f = () ','code_after'=>' { }','answer'=>'=>','hint'=>'Flecha.'],
            ['question'=>'Parámetro por defecto:','code_before'=>'function f(n = ','code_after'=>')','answer'=>'0','hint'=>'Valor inicial.'],
            ['question'=>'Llamar a una función:','code_before'=>'saludar','code_after'=>'','answer'=>'()','hint'=>'Paréntesis.'],
            ['question'=>'Función que se llama a sí misma:','code_before'=>'// Función ','code_after'=>'','answer'=>'recursiva','hint'=>'Se autoinvoca.'],
            ['question'=>'¿Qué es (a, b) en una función?','code_before'=>'// Son los ','code_after'=>'','answer'=>'parametros','hint'=>'Entradas.'],
            ['question'=>'¿Qué es (5, 10) al llamar?','code_before'=>'// Son los ','code_after'=>'','answer'=>'argumentos','hint'=>'Valores reales.'],
            ['question'=>'Función anónima:','code_before'=>'const f = ','code_after'=>'() { }','answer'=>'function','hint'=>'Sin nombre.'],
            ['question'=>'Arrow function compacta:','code_before'=>'const d = x ','code_after'=>' x * 2;','answer'=>'=>','hint'=>'Sin llaves.'],
            ['question'=>'Exportar función:','code_before'=>'','code_after'=>' function f() {}','answer'=>'export','hint'=>'Para otros archivos.'],
            ['question'=>'Importar función:','code_before'=>'','code_after'=>' { f } from "./file";','answer'=>'import','hint'=>'Traer de fuera.'],
            ['question'=>'Contexto de "this" en arrow functions:','code_before'=>'// Es el contexto ','code_after'=>'','answer'=>'lexico','hint'=>'Heredado.'],
            ['question'=>'Función asíncrona:','code_before'=>'','code_after'=>' function f() {}','answer'=>'async','hint'=>'No bloqueante.'],
            ['question'=>'Esperar promesa:','code_before'=>'','code_after'=>' fetch();','answer'=>'await','hint'=>'Esperar.'],
            ['question'=>'¿Qué devuelve una función async?','code_before'=>'// Devuelve una ','code_after'=>'','answer'=>'promise','hint'=>'Promesa.'],
            ['question'=>'Método de promesa exitosa:','code_before'=>'p.','code_after'=>'(res => ...)','answer'=>'then','hint'=>'Entonces.'],
            ['question'=>'Método de promesa fallida:','code_before'=>'p.','code_after'=>'(err => ...)','answer'=>'catch','hint'=>'Atrapar.'],
            ['question'=>'Método que siempre corre al final:','code_before'=>'p.','code_after'=>'(() => ...)','answer'=>'finally','hint'=>'Finalmente.'],
            ['question'=>'Ejecutar función tras tiempo:','code_before'=>'','code_after'=>'(f, 1000)','answer'=>'setTimeout','hint'=>'Set Time Out.'],
            ['question'=>'Ejecutar función repetidamente:','code_before'=>'','code_after'=>'(f, 1000)','answer'=>'setInterval','hint'=>'Set Interval.'],
            ['question'=>'Cerrar intervalo:','code_before'=>'','code_after'=>'(id)','answer'=>'clearInterval','hint'=>'Clear Interval.'],
            ['question'=>'Parámetro rest (varios args):','code_before'=>'function f(','code_after'=>'args)','answer'=>'...','hint'=>'Tres puntos.'],
            ['question'=>'Spread operator (expandir):','code_before'=>'let n = [','code_after'=>'arr]','answer'=>'...','hint'=>'Tres puntos.'],
            ['question'=>'Nombre de función que devuelve otra:','code_before'=>'// Es un ','code_after'=>'','answer'=>'closure','hint'=>'Clausura.'],
            ['question'=>'Función que recibe otra:','code_before'=>'// High Order ','code_after'=>'','answer'=>'Function','hint'=>'Orden superior.'],
            ['question'=>'¿Qué devuelve typeof function?','code_before'=>'','code_after'=>'','answer'=>'function','hint'=>'Su propio tipo.'],
            ['question'=>'Callback es una función que se pasa como...','code_before'=>'','code_after'=>'','answer'=>'argumento','hint'=>'Valor de entrada.'],
            ['question'=>'Ámbito de variable:','code_before'=>'// Se conoce como ','code_after'=>'','answer'=>'scope','hint'=>'Alcance.'],
            ['question'=>'Elevación de funciones (hoisting):','code_before'=>'// Las declaradas sufren ','code_after'=>'','answer'=>'hoisting','hint'=>'Elevación.'],
            ['question'=>'Función autoinvocada:','code_before'=>'(function(){})','code_after'=>'','answer'=>'()','hint'=>'Paréntesis finales.'],
            ['question'=>'Propiedad .name de una función:','code_before'=>'f.','code_after'=>'','answer'=>'name','hint'=>'Nombre.'],
            ['question'=>'¿Puede una función no retornar nada?','code_before'=>'// Respuesta (si/no): ','code_after'=>'','answer'=>'si','hint'=>'Retorna undefined.'],
            ['question'=>'Comprobar si es función:','code_before'=>'typeof f === ','code_after'=>'','answer'=>'"function"','hint'=>'Tipo string.'],
            ['question'=>'Generador: function','code_before'=>'','code_after'=>' gen()','answer'=>'*','hint'=>'Asterisco.'],
        ];
    }

    private static function getPseudocodigo(): array
    {
        return [
            ['question'=>'Iniciar bloque condicional:','code_before'=>'','code_after'=>' (condicion) ENTONCES','answer'=>'SI','hint'=>'Equivale a if.'],
            ['question'=>'Bloque alternativo:','code_before'=>'SI ... ','code_after'=>' ENTONCES','answer'=>'SINO','hint'=>'Equivale a else.'],
            ['question'=>'Mostrar mensaje:','code_before'=>'','code_after'=>' "Hola"','answer'=>'ESCRIBIR','hint'=>'Salida de datos.'],
            ['question'=>'Pedir dato:','code_before'=>'','code_after'=>' nombre','answer'=>'LEER','hint'=>'Entrada de datos.'],
            ['question'=>'Bucle mientras:','code_before'=>'','code_after'=>' (cond) HACER','answer'=>'MIENTRAS','hint'=>'while.'],
            ['question'=>'Bucle para:','code_before'=>'','code_after'=>' i=1 HASTA 10','answer'=>'PARA','hint'=>'for.'],
            ['question'=>'Declarar variable:','code_before'=>'','code_after'=>' x como Entero','answer'=>'DEFINIR','hint'=>'Especificar tipo.'],
            ['question'=>'Asignación:','code_before'=>'x ','code_after'=>' 10','answer'=>'<-','hint'=>'Flecha izquierda.'],
            ['question'=>'Fin de programa:','code_before'=>'','code_after'=>'Algoritmo','answer'=>'Fin','hint'=>'Terminar.'],
            ['question'=>'Inicio de programa:','code_before'=>'','code_after'=>' SumarNumeros','answer'=>'Algoritmo','hint'=>'Empezar.'],
            ['question'=>'Tipo de dato sin decimales:','code_before'=>'x: ','code_after'=>'','answer'=>'ENTERO','hint'=>'Números base.'],
            ['question'=>'Tipo de dato con decimales:','code_before'=>'x: ','code_after'=>'','answer'=>'REAL','hint'=>'Coma flotante.'],
            ['question'=>'Tipo de dato verdadero/falso:','code_before'=>'x: ','code_after'=>'','answer'=>'LOGICO','hint'=>'Booleano.'],
            ['question'=>'Tipo de dato texto:','code_before'=>'x: ','code_after'=>'','answer'=>'CARACTER','hint'=>'String.'],
            ['question'=>'Cerrar bloque SI:','code_before'=>'','code_after'=>'','answer'=>'FINSI','hint'=>'Fin + Si.'],
            ['question'=>'Cerrar bloque PARA:','code_before'=>'','code_after'=>'','answer'=>'FINPARA','hint'=>'Fin + Para.'],
            ['question'=>'Cerrar bloque MIENTRAS:','code_before'=>'','code_after'=>'','answer'=>'FINMIENTRAS','hint'=>'Fin + Mientras.'],
            ['question'=>'Bucle repetir:','code_before'=>'','code_after'=>' ... HASTA QUE (cond)','answer'=>'REPETIR','hint'=>'Do while.'],
            ['question'=>'Caso múltiple:','code_before'=>'','code_after'=>' (variable) HACER','answer'=>'SEGUN','hint'=>'Switch.'],
            ['question'=>'De otro modo (en Segun):','code_before'=>'','code_after'=>':','answer'=>'DE OTRO MODO','hint'=>'Default.'],
            ['question'=>'Módulo (resto):','code_before'=>'10 ','code_after'=>' 3','answer'=>'MOD','hint'=>'Operador resto.'],
            ['question'=>'Operador Y lógico:','code_before'=>'cond1 ','code_after'=>' cond2','answer'=>'Y','hint'=>'AND.'],
            ['question'=>'Operador O lógico:','code_before'=>'cond1 ','code_after'=>' cond2','answer'=>'O','hint'=>'OR.'],
            ['question'=>'Operador NO lógico:','code_before'=>'','code_after'=>' (cond)','answer'=>'NO','hint'=>'NOT.'],
            ['question'=>'Potencia:','code_before'=>'2 ','code_after'=>' 3','answer'=>'^','hint'=>'Circunflejo.'],
            ['question'=>'¿Cómo se llama el proceso de seguir el código a mano?','code_before'=>'// Prueba de ','code_after'=>'','answer'=>'escritorio','hint'=>'Verificar flujo.'],
            ['question'=>'Limpiar pantalla:','code_before'=>'','code_after'=>'','answer'=>'BORRAR PANTALLA','hint'=>'Comando visual.'],
            ['question'=>'Esperar tecla:','code_before'=>'','code_after'=>'','answer'=>'ESPERAR TECLA','hint'=>'Pausa.'],
            ['question'=>'Subproceso (función):','code_before'=>'','code_after'=>' MiFuncion()','answer'=>'SUBPROCESO','hint'=>'Bloque reusable.'],
            ['question'=>'Retornar valor en subproceso:','code_before'=>'','code_after'=>' res','answer'=>'RETORNAR','hint'=>'Devolver.'],
            ['question'=>'Concatenar texto en pseudocódigo:','code_before'=>'','code_after'=>'','answer'=>',','hint'=>'Coma o más.'],
            ['question'=>'Igualdad en comparaciones:','code_before'=>'x ','code_after'=>' 10','answer'=>'=','hint'=>'Símbolo igual.'],
            ['question'=>'Diferente en comparaciones:','code_before'=>'x ','code_after'=>' 10','answer'=>'<>','hint'=>'Menor y mayor.'],
            ['question'=>'Truncar número:','code_before'=>'','code_after'=>'(10.5)','answer'=>'TRUNC','hint'=>'Quitar decimales.'],
            ['question'=>'Raíz cuadrada:','code_before'=>'','code_after'=>'(16)','answer'=>'RC','hint'=>'R y C.'],
        ];
    }

    private static function getPOO(): array
    {
        return [
            ['question'=>'Declarar una clase:','code_before'=>'','code_after'=>' Usuario { }','answer'=>'class','hint'=>'Plantilla.'],
            ['question'=>'Crear instancia:','code_before'=>'let u = ','code_after'=>' Usuario();','answer'=>'new','hint'=>'Nuevo.'],
            ['question'=>'Método inicial:','code_before'=>'','code_after'=>'() { }','answer'=>'constructor','hint'=>'Construir.'],
            ['question'=>'Referencia al objeto actual:','code_before'=>'','code_after'=>'.nombre = "N";','answer'=>'this','hint'=>'Este.'],
            ['question'=>'Heredar de otra clase:','code_before'=>'class A ','code_after'=>' B { }','answer'=>'extends','hint'=>'Extender.'],
            ['question'=>'Llamar al padre:','code_before'=>'','code_after'=>'();','answer'=>'super','hint'=>'Superior.'],
            ['question'=>'Propiedad privada (JS moderno):','code_before'=>'this.','code_after'=>'secreto;','answer'=>'#','hint'=>'Almohadilla.'],
            ['question'=>'Método que no necesita instancia:','code_before'=>'','code_after'=>' static function f()','answer'=>'static','hint'=>'Estático.'],
            ['question'=>'Pilar POO: Ocultar detalles:','code_before'=>'// Pilar: ','code_after'=>'','answer'=>'encapsulamiento','hint'=>'Cápsula.'],
            ['question'=>'Pilar POO: Reutilizar código:','code_before'=>'// Pilar: ','code_after'=>'','answer'=>'herencia','hint'=>'Heredar.'],
            ['question'=>'Pilar POO: Varias formas:','code_before'=>'// Pilar: ','code_after'=>'','answer'=>'polimorfismo','hint'=>'Poli...'],
            ['question'=>'Pilar POO: Simplificar realidad:','code_before'=>'// Pilar: ','code_after'=>'','answer'=>'abstraccion','hint'=>'Abstracto.'],
            ['question'=>'Una clase es una...','code_before'=>'// Una clase es una ','code_after'=>' de un objeto.','answer'=>'plantilla','hint'=>'Molde.'],
            ['question'=>'Un objeto es una...','code_before'=>'// Es una ','code_after'=>' de una clase.','answer'=>'instancia','hint'=>'Ejemplo real.'],
            ['question'=>'Modificar valor (método):','code_before'=>'obj.','code_after'=>'Nombre("Nuevo")','answer'=>'set','hint'=>'Establecer.'],
            ['question'=>'Obtener valor (método):','code_before'=>'obj.','code_after'=>'Nombre()','answer'=>'get','hint'=>'Obtener.'],
            ['question'=>'Clase que no se puede instanciar:','code_before'=>'// Clase ','code_after'=>'','answer'=>'abstracta','hint'=>'Solo para heredar.'],
            ['question'=>'Interfaz define un...','code_before'=>'// Define un ','code_after'=>'','answer'=>'contrato','hint'=>'Obligación.'],
            ['question'=>'¿Qué palabra permite usar una interfaz?','code_before'=>'class A ','code_after'=>' Interfaz','answer'=>'implements','hint'=>'Implementar.'],
            ['question'=>'Miembros protegidos (acceso):','code_before'=>'// Miembros ','code_after'=>'','answer'=>'protected','hint'=>'Protegidos.'],
            ['question'=>'Miembros públicos (acceso):','code_before'=>'// Miembros ','code_after'=>'','answer'=>'public','hint'=>'Públicos.'],
            ['question'=>'Miembros privados (acceso):','code_before'=>'// Miembros ','code_after'=>'','answer'=>'private','hint'=>'Privados.'],
            ['question'=>'Sobrecarga de métodos:','code_before'=>'// Técnica: ','code_after'=>'','answer'=>'overloading','hint'=>'Mismo nombre, diferente firma.'],
            ['question'=>'Sobrescritura de métodos:','code_before'=>'// Técnica: ','code_after'=>'','answer'=>'overriding','hint'=>'Redefinir en hijo.'],
            ['question'=>'Composición sobre...','code_before'=>'// Diseño: Composición sobre ','code_after'=>'','answer'=>'herencia','hint'=>'Mejor práctica.'],
            ['question'=>'Clase que solo tiene datos:','code_before'=>'// Se llama ','code_after'=>' class','answer'=>'data','hint'=>'Datos.'],
            ['question'=>'Patrón para crear objetos:','code_before'=>'// Patrón ','code_after'=>'','answer'=>'factory','hint'=>'Fábrica.'],
            ['question'=>'Instancia única (patrón):','code_before'=>'// Patrón ','code_after'=>'','answer'=>'singleton','hint'=>'Solo uno.'],
            ['question'=>'Relación "es un":','code_before'=>'// Representa ','code_after'=>'','answer'=>'herencia','hint'=>'Es un...'],
            ['question'=>'Relación "tiene un":','code_before'=>'// Representa ','code_after'=>'','answer'=>'composicion','hint'=>'Tiene un...'],
            ['question'=>'¿typeof MiClase devuelve?','code_before'=>'','code_after'=>'','answer'=>'function','hint'=>'En JS las clases son...'],
            ['question'=>'¿typeof instacia devuelve?','code_before'=>'','code_after'=>'','answer'=>'object','hint'=>'Objeto.'],
            ['question'=>'Método para destruir (raro en JS):','code_before'=>'','code_after'=>'()','answer'=>'destructor','hint'=>'Lo opuesto a constructor.'],
            ['question'=>'Palabra clave para atributos constantes:','code_before'=>'','code_after'=>' VERSION = 1;','answer'=>'readonly','hint'=>'Solo lectura.'],
            ['question'=>'Casteo de tipos:','code_before'=>'let x = obj ','code_after'=>' MiClase;','answer'=>'as','hint'=>'Como.'],
        ];
    }
}
