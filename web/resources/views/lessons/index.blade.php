@extends('layouts.app')

@section('title', 'Catálogo de Lecciones | Draco')

@section('content')
<div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
    
    <!-- Encabezado de la Sección -->
    <div class="mb-10 text-center md:text-left">
        <h1 class="text-3xl md:text-4xl font-extrabold text-white tracking-tight">
            📚 Catálogo de Lecciones
        </h1>
        <p class="text-slate-400 mt-2 text-lg">Explora todos los temas disponibles y mejora tus habilidades de código.</p>
    </div>

    <!-- Grid de Lecciones (Modular y Responsivo) -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        
        <!-- Ejemplo de Tarjeta de Lección -->
        <div class="bg-slate-800 rounded-3xl p-6 border border-slate-700 hover:border-slate-500 transition-all group flex flex-col h-full shadow-lg hover:shadow-xl">
            <!-- Icono y Título -->
            <div class="flex items-start gap-4 mb-4">
                <div class="w-14 h-14 bg-gradient-to-br from-blue-500 to-indigo-600 rounded-2xl flex items-center justify-center text-2xl shadow-inner flex-shrink-0">
                    🐍
                </div>
                <div>
                    <h3 class="text-xl font-bold text-white group-hover:text-blue-400 transition-colors">Python Básico</h3>
                    <p class="text-sm text-slate-400 mt-1 line-clamp-2">Aprende los fundamentos de Python, variables y estructuras de control.</p>
                </div>
            </div>

            <!-- Progreso y Metadatos -->
            <div class="mt-auto pt-4 border-t border-slate-700/50">
                <div class="flex justify-between text-xs font-bold mb-2">
                    <span class="text-slate-300">Progreso</span>
                    <span class="text-blue-400">45%</span>
                </div>
                <!-- Barra de progreso -->
                <div class="w-full bg-slate-900 rounded-full h-2.5 mb-4 border border-slate-800">
                    <div class="bg-blue-500 h-2.5 rounded-full" style="width: 45%"></div>
                </div>

                <a href="#" class="w-full block text-center bg-slate-700 hover:bg-blue-600 text-white font-bold py-3 rounded-xl transition-colors">
                    Continuar Lección
                </a>
            </div>
        </div>

        <!-- Tarjeta de Lección Bloqueada -->
        <div class="bg-slate-800/50 rounded-3xl p-6 border border-slate-700/50 flex flex-col h-full opacity-75 grayscale hover:grayscale-0 transition-all">
            <div class="flex items-start gap-4 mb-4">
                <div class="w-14 h-14 bg-slate-700 rounded-2xl flex items-center justify-center text-2xl shadow-inner flex-shrink-0">
                    🔒
                </div>
                <div>
                    <h3 class="text-xl font-bold text-slate-300">Java Orientado a Objetos</h3>
                    <p class="text-sm text-slate-500 mt-1 line-clamp-2">Clases, objetos, herencia y polimorfismo en Java.</p>
                </div>
            </div>

            <div class="mt-auto pt-4 border-t border-slate-700/50">
                <button disabled class="w-full block text-center bg-slate-800 text-slate-500 font-bold py-3 rounded-xl cursor-not-allowed">
                    Nivel Insuficiente
                </button>
            </div>
        </div>

    </div>
</div>
@endsection
