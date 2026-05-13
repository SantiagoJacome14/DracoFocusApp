@extends('layouts.app')

@section('title', 'Avances - Draco')

@section('content')
<div class="p-6 lg:p-10 animate-fade-in-up">
    <div class="mb-10">
        <h1 class="text-4xl font-black text-white mb-2 tracking-tight">Mis <span class="text-transparent bg-clip-text bg-gradient-to-r from-orange-400 to-rose-500">Avances</span></h1>
        <p class="text-slate-400">Monitorea tu progreso, rachas y logros desbloqueados.</p>
    </div>

    <div class="row g-6">
        <!-- Tarjetas de Estadísticas Rápidas -->
        <div class="col-12 col-xl-4">
            <div class="bg-slate-800/30 border border-slate-700/50 rounded-2xl p-6 mb-6">
                <div class="flex items-center gap-4 mb-6">
                    @php
                        $hasStreak = ($user->current_streak ?? 0) > 0;
                    @endphp
                    <div class="w-14 h-14 {{ $hasStreak ? 'bg-orange-500/10 text-orange-500' : 'bg-slate-700/30 text-slate-500' }} rounded-2xl flex items-center justify-center shadow-inner transition-colors duration-500">
                        <svg class="w-8 h-8" fill="currentColor" viewBox="0 0 24 24">
                            <path d="M17.66 11.5c-.21-.07-.41-.14-.61-.21C16.32 8.75 15.35 6.5 12 2c-4 6-3.33 8.5-4 11-.56 2.13-1.61 3.51-3.13 4.54a10 10 0 1 0 15.65-4.14c-.88-.71-1.83-1.39-2.86-1.9z" />
                        </svg>
                    </div>
                    <div>
                        <p class="text-slate-400 text-sm font-bold uppercase tracking-wider">Racha Actual</p>
                        <h2 class="text-3xl font-black text-white">
                            <span class="{{ $hasStreak ? 'text-orange-400' : 'text-slate-500' }}">{{ $user->current_streak ?? 0 }}</span>
                            <span class="text-lg font-normal text-slate-500">días</span>
                        </h2>
                    </div>
                </div>
                <div class="space-y-4">
                    <p class="text-sm text-slate-400">Meta semanal: <span class="text-white font-bold">5/7 días</span></p>
                    <div class="flex justify-between gap-1">
                        @php
                            $days = ['L', 'M', 'M', 'J', 'V', 'S', 'D'];
                            $today = now()->dayOfWeekIso;
                        @endphp
                        @foreach($days as $index => $day)
                            <div class="flex-1 h-2 rounded-full {{ ($index + 1) <= $today ? 'bg-orange-500 shadow-lg shadow-orange-500/20' : 'bg-slate-700' }}"></div>
                        @endforeach
                    </div>
                </div>
            </div>

            <div class="bg-slate-800/30 border border-slate-700/50 rounded-2xl p-6">
                <p class="text-slate-400 text-sm font-bold uppercase tracking-wider mb-6">Resumen Global</p>
                <div class="space-y-6">
                    <div class="flex justify-between items-center">
                        <span class="text-slate-300">XP Total acumulado</span>
                        <span class="text-teal-400 font-black text-xl">{{ number_format($user->total_xp) }}</span>
                    </div>
                    <div class="flex justify-between items-center">
                        <span class="text-slate-300">Lecciones aprobadas</span>
                        <span class="text-white font-black text-xl">{{ $completedLessonsCount }}</span>
                    </div>
                    <div class="flex justify-between items-center">
                        <span class="text-slate-300">Precisión promedio</span>
                        <span class="text-emerald-400 font-black text-xl">88%</span>
                    </div>
                </div>
            </div>
        </div>

        <!-- Gráfico de Actividad Semanal -->
        <div class="col-12 col-xl-8">
            <div class="bg-slate-800/30 border border-slate-700/50 rounded-2xl p-8 h-full">
                <div class="flex justify-between items-center mb-10">
                    <h3 class="text-xl font-bold text-white">Actividad de la Semana</h3>
                    <select class="bg-slate-900 border border-slate-700 text-slate-400 text-xs rounded-lg px-3 py-2 outline-none">
                        <option>Esta semana</option>
                        <option>Semana pasada</option>
                    </select>
                </div>
                
                <div class="flex items-end justify-between h-64 gap-2 px-2">
                    @foreach($weeklyProgress as $day => $xp)
                        @php
                            $height = $xp > 0 ? min(($xp / 1000) * 100, 100) : 10;
                        @endphp
                        <div class="flex-1 flex flex-col items-center gap-4 group">
                            <div class="w-full relative">
                                <div class="absolute -top-10 left-1/2 -translate-x-1/2 bg-slate-900 text-teal-400 text-[10px] font-bold px-2 py-1 rounded opacity-0 group-hover:opacity-100 transition-opacity whitespace-nowrap">
                                    {{ $xp }} XP
                                </div>
                                <div class="w-full bg-gradient-to-t from-teal-500/20 to-teal-400 rounded-t-lg transition-all duration-700 shadow-lg shadow-teal-500/10" 
                                     style="height: {{ $height }}%;"></div>
                            </div>
                            <span class="text-slate-500 text-xs font-bold">{{ $day }}</span>
                        </div>
                    @endforeach
                </div>
            </div>
        </div>

        <!-- Sección de Logros -->
        <div class="col-12">
            <div class="bg-slate-800/30 border border-slate-700/50 rounded-2xl p-8 mt-6">
                <h3 class="text-xl font-bold text-white mb-8">Tus Medallas y Logros</h3>
                <div class="row g-4">
                    @forelse($achievements as $userAchievement)
                        <div class="col-6 col-md-4 col-lg-2">
                            <div class="bg-slate-900/50 border border-slate-700/30 rounded-2xl p-4 text-center group hover:border-teal-500/50 transition-all">
                                <div class="w-16 h-16 bg-slate-800 rounded-full mx-auto mb-4 flex items-center justify-center text-3xl group-hover:scale-110 transition-transform">
                                    {{ $userAchievement->achievement->icon ?? '🏆' }}
                                </div>
                                <p class="text-white text-xs font-bold mb-1">{{ $userAchievement->achievement->title }}</p>
                                <p class="text-[10px] text-slate-500 leading-tight">{{ $userAchievement->achievement->description }}</p>
                            </div>
                        </div>
                    @empty
                        <div class="col-12 text-center py-10">
                            <div class="w-20 h-20 bg-slate-800 rounded-full mx-auto mb-4 flex items-center justify-center text-slate-600">
                                <svg class="w-10 h-10" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
                                </svg>
                            </div>
                            <p class="text-slate-500 font-medium italic">Aún no has desbloqueado logros. ¡Sigue aprendiendo!</p>
                        </div>
                    @endforelse
                </div>
            </div>
        </div>
    </div>
</div>

<style>
    .animate-fade-in-up {
        animation: fadeInUp 0.5s ease-out;
    }
    @keyframes fadeInUp {
        from { opacity: 0; transform: translateY(20px); }
        to { opacity: 1; transform: translateY(0); }
    }
</style>
@endsection
