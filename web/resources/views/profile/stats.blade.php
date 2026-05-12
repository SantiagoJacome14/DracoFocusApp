@extends('layouts.app')

@section('title', 'Mis Estadísticas | Draco')

@section('content')
<div class="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
    
    <!-- Layout de Dos Columnas para Desktop -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
        
        <!-- Columna Izquierda: Perfil Resumido -->
        <div class="lg:col-span-1 space-y-6">
            <div class="bg-slate-800 rounded-3xl p-6 text-center border border-slate-700">
                <img src="{{ auth()->user()->avatar ?? asset('img/default-avatar.png') }}" alt="Avatar" class="w-32 h-32 mx-auto rounded-full border-4 border-draco-emerald/30 shadow-lg object-cover">
                <h2 class="text-2xl font-bold text-white mt-4">{{ auth()->user()->name }}</h2>
                <p class="text-draco-emerald-light font-medium">{{ auth()->user()->role ?? 'Estudiante' }}</p>
                
                <div class="mt-6 flex justify-center gap-4">
                    <div class="text-center">
                        <span class="block text-2xl font-black text-white">12</span>
                        <span class="text-xs text-slate-400 uppercase tracking-wide">Racha 🔥</span>
                    </div>
                    <div class="w-px bg-slate-700"></div>
                    <div class="text-center">
                        <span class="block text-2xl font-black text-white">4.5k</span>
                        <span class="text-xs text-slate-400 uppercase tracking-wide">XP ⚡</span>
                    </div>
                </div>
            </div>
        </div>

        <!-- Columna Derecha: Estadísticas Detalladas -->
        <div class="lg:col-span-2 space-y-6">
            
            <h3 class="text-xl font-extrabold text-white flex items-center gap-2">
                📊 Rendimiento Semanal
            </h3>
            
            <!-- Bento Grid de Estadísticas -->
            <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
                <div class="bg-slate-800 rounded-2xl p-4 border border-slate-700">
                    <span class="text-slate-400 text-sm font-medium">Ejercicios</span>
                    <p class="text-3xl font-black text-white mt-1">142</p>
                </div>
                <div class="bg-slate-800 rounded-2xl p-4 border border-slate-700">
                    <span class="text-slate-400 text-sm font-medium">Precisión</span>
                    <p class="text-3xl font-black text-draco-emerald mt-1">87%</p>
                </div>
                <div class="bg-slate-800 rounded-2xl p-4 border border-slate-700">
                    <span class="text-slate-400 text-sm font-medium">Lecciones</span>
                    <p class="text-3xl font-black text-blue-400 mt-1">8</p>
                </div>
                <div class="bg-slate-800 rounded-2xl p-4 border border-slate-700">
                    <span class="text-slate-400 text-sm font-medium">Liga</span>
                    <p class="text-3xl font-black text-draco-gold mt-1">Oro</p>
                </div>
            </div>

            <!-- Gráfico / Logros (Placeholder visual) -->
            <div class="bg-slate-800 rounded-3xl p-6 border border-slate-700 mt-6">
                <h4 class="text-lg font-bold text-white mb-4">Últimos Logros</h4>
                <div class="flex flex-col gap-3">
                    <div class="flex items-center gap-4 bg-slate-900/50 p-3 rounded-xl border border-slate-700/50">
                        <div class="text-3xl">🎯</div>
                        <div>
                            <h5 class="text-white font-bold">Francotirador</h5>
                            <p class="text-slate-400 text-sm">Completaste 5 lecciones sin errores.</p>
                        </div>
                    </div>
                    <div class="flex items-center gap-4 bg-slate-900/50 p-3 rounded-xl border border-slate-700/50">
                        <div class="text-3xl">🦉</div>
                        <div>
                            <h5 class="text-white font-bold">Búho Nocturno</h5>
                            <p class="text-slate-400 text-sm">Estudiaste después de la medianoche.</p>
                        </div>
                    </div>
                </div>
            </div>

        </div>
    </div>
</div>
@endsection
