@extends('layouts.app')
@section('title', 'Mi Perfil — Draco')

@section('content')
<div class="min-h-screen" x-data>

    {{-- ────────── HERO / PROFILE HEADER ────────── --}}
    <div class="bg-gradient-to-br from-slate-800 via-slate-900 to-slate-800 px-8 py-8 border-b border-slate-700/60">
        <div class="max-w-7xl mx-auto">
            {{-- Top bar: back + role badge --}}
            <div class="flex items-center justify-between mb-6">
                <a href="{{ route('dashboard') }}" class="flex items-center gap-2 text-slate-400 hover:text-white transition text-sm font-bold group">
                    <svg class="w-5 h-5 group-hover:-translate-x-1 transition-transform" fill="none" stroke="currentColor" stroke-width="2.5" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M15 19l-7-7 7-7"/></svg>
                    Volver al Dashboard
                </a>

                @if($user['is_admin'])
                <a href="{{ route('admin.users') }}" class="flex items-center gap-2 bg-draco-gold/15 border border-draco-gold/30 text-draco-gold px-4 py-2 rounded-xl text-xs font-extrabold uppercase tracking-widest hover:bg-draco-gold/25 transition">
                    <svg class="w-3.5 h-3.5" fill="currentColor" viewBox="0 0 24 24"><path d="M12 1l3.09 6.26L22 8.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01z"/></svg>
                    Admin
                </a>
                @else
                <div class="flex items-center gap-1.5 bg-slate-700/40 border border-slate-600/50 text-slate-400 px-4 py-2 rounded-xl text-xs font-bold uppercase tracking-widest">
                    <svg class="w-3.5 h-3.5" fill="currentColor" viewBox="0 0 24 24"><path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/></svg>
                    Estudiante
                </div>
                @endif
            </div>

            {{-- Profile info row --}}
            <div class="flex items-center gap-8">
                <div class="relative flex-shrink-0">
                    <div class="w-28 h-28 bg-gradient-to-br from-draco-emerald to-emerald-700 rounded-3xl border-3 border-draco-gold/60 flex items-center justify-center shadow-2xl shadow-emerald-900/40">
                        <span class="text-5xl font-black text-white">{{ strtoupper(substr($user['name'], 0, 1)) }}</span>
                    </div>
                    <div class="absolute -bottom-2 -right-2 bg-gradient-to-r from-orange-400 to-red-500 rounded-xl w-9 h-9 flex items-center justify-center border-3 border-slate-900 text-sm font-black text-white shadow">
                        🔥
                    </div>
                </div>
                <div class="flex-1">
                    <h1 class="text-3xl font-extrabold text-white tracking-tight">{{ $user['name'] }}</h1>
                    <p class="text-slate-400 text-sm mt-1">{{ $user['email'] }}</p>
                    <p class="text-slate-500 text-xs mt-1">Miembro desde {{ $user['member_since'] }}</p>
                </div>

                {{-- Quick stats pills --}}
                <div class="flex gap-4">
                    <div class="glass-card rounded-2xl px-6 py-4 text-center hover:border-draco-gold/40 transition-colors group cursor-default">
                        <div class="text-2xl font-black text-draco-gold group-hover:scale-110 transition-transform">{{ $user['total_xp'] }}</div>
                        <div class="text-xs text-slate-400 font-semibold mt-1">XP Total</div>
                    </div>
                    <div class="glass-card rounded-2xl px-6 py-4 text-center hover:border-orange-500/40 transition-colors group cursor-default">
                        <div class="text-2xl font-black text-orange-400 group-hover:scale-110 transition-transform">{{ $user['current_streak'] }}</div>
                        <div class="text-xs text-slate-400 font-semibold mt-1">Días racha</div>
                    </div>
                    <div class="glass-card rounded-2xl px-6 py-4 text-center hover:border-sky-500/40 transition-colors group cursor-default">
                        <div class="text-2xl font-black text-sky-400 group-hover:scale-110 transition-transform">{{ $user['daily_goal'] }}</div>
                        <div class="text-xs text-slate-400 font-semibold mt-1">Meta XP/día</div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    {{-- ────────── CONTENT GRID ────────── --}}
    <div class="max-w-7xl mx-auto px-8 py-8">

        {{-- ── Two Column Layout ── --}}
        <div class="grid grid-cols-2 gap-8 mb-8">

            {{-- ────────── PROGRESO DIARIO ────────── --}}
            <section class="animate-fade-in-up">
                <h2 class="text-sm font-extrabold text-slate-400 uppercase tracking-widest mb-4 flex items-center gap-2">
                    <span class="text-base">📅</span> Progreso Diario
                </h2>
                <div class="glass-card rounded-3xl p-6 shadow-xl h-full">
                    <div class="flex items-end justify-between gap-3 h-32">
                        @php $maxDaily = max(array_values($dailyProgress)) ?: 1; @endphp
                        @foreach($dailyProgress as $day => $xp)
                            @php
                                $pct   = round(($xp / $maxDaily) * 100);
                                $isMax = $xp === max(array_values($dailyProgress));
                            @endphp
                            <div class="flex flex-col items-center gap-1 flex-1">
                                <span class="text-xs text-slate-400 font-bold {{ $xp >= $dailyProgress['Vie'] ? 'text-draco-emerald-light' : '' }}">
                                    @if($xp > 0) {{ $xp }} @endif
                                </span>
                                <div class="w-full rounded-lg transition-all duration-700 relative overflow-hidden"
                                     style="height: {{ max(8, $pct) }}%; background: {{ $isMax ? 'linear-gradient(to top, #059669, #34d399)' : 'rgb(51 65 85)' }}">
                                    @if($isMax)
                                    <div class="absolute inset-0 bg-white/10 animate-pulse rounded-lg"></div>
                                    @endif
                                </div>
                                <span class="text-xs text-slate-500 font-bold">{{ $day }}</span>
                            </div>
                        @endforeach
                    </div>
                    <div class="mt-4 pt-4 border-t border-slate-700/50 flex justify-between text-xs text-slate-500 font-semibold">
                        <span>Semana actual</span>
                        <span class="text-draco-emerald-light">{{ array_sum(array_values($dailyProgress)) }} XP esta semana</span>
                    </div>
                </div>
            </section>

            {{-- ────────── PROGRESO MENSUAL ────────── --}}
            <section class="animate-fade-in-up animate-delay-1">
                <h2 class="text-sm font-extrabold text-slate-400 uppercase tracking-widest mb-4 flex items-center gap-2">
                    <span class="text-base">🗓️</span> Progreso Mensual
                </h2>
                <div class="glass-card rounded-3xl p-6 shadow-xl h-full space-y-3">
                    @php $maxMonthly = max(array_values($monthlyProgress)) ?: 1; @endphp
                    @foreach($monthlyProgress as $week => $xp)
                        @php $pct = round(($xp / $maxMonthly) * 100); @endphp
                        <div>
                            <div class="flex justify-between text-xs font-bold mb-1.5">
                                <span class="text-slate-300">{{ $week }}</span>
                                <span class="text-slate-400">{{ $xp > 0 ? $xp . ' XP' : 'En curso...' }}</span>
                            </div>
                            <div class="w-full bg-slate-900 rounded-full h-3 overflow-hidden border border-slate-700">
                                <div class="h-full rounded-full transition-all duration-1000"
                                     style="width:{{ $pct }}%; background: linear-gradient(to right, #059669, #34d399)">
                                </div>
                            </div>
                        </div>
                    @endforeach
                    <div class="pt-3 border-t border-slate-700/50 flex justify-between text-xs text-slate-500 font-semibold">
                        <span>{{ $monthName }} {{ now()->format('Y') }}</span>
                        <span class="text-draco-emerald-light">{{ array_sum(array_values($monthlyProgress)) }} XP acumulados</span>
                    </div>
                </div>
            </section>
        </div>

        {{-- ── Second Row: Stats + Lessons ── --}}
        <div class="grid grid-cols-5 gap-8">

            {{-- ────────── ESTADÍSTICAS DE ESTUDIO ────────── --}}
            <section class="col-span-2 animate-fade-in-up animate-delay-2">
                <h2 class="text-sm font-extrabold text-slate-400 uppercase tracking-widest mb-4 flex items-center gap-2">
                    <span class="text-base">📊</span> Estadísticas de Estudio
                </h2>
                <div class="grid grid-cols-2 gap-4">
                    @foreach($stats as $stat)
                    <div class="glass-card rounded-2xl p-5 flex flex-col gap-1 shadow-lg hover:border-draco-emerald/40 transition-all group cursor-default">
                        <span class="text-2xl group-hover:scale-110 transition-transform inline-block">{{ $stat['icon'] }}</span>
                        <span class="text-xl font-extrabold text-white mt-1">{{ $stat['value'] }}</span>
                        <span class="text-xs text-slate-400 font-semibold leading-tight">{{ $stat['label'] }}</span>
                    </div>
                    @endforeach
                </div>
            </section>

            {{-- ────────── PROGRESO POR LECCIÓN ────────── --}}
            <section class="col-span-3 animate-fade-in-up animate-delay-3">
                <h2 class="text-sm font-extrabold text-slate-400 uppercase tracking-widest mb-4 flex items-center gap-2">
                    <span class="text-base">🎓</span> Progreso por Lección
                </h2>
                <div class="glass-card rounded-3xl p-6 shadow-xl space-y-4">
                    @foreach($lessons as $lesson)
                    <div class="group">
                        <div class="flex justify-between items-center mb-2">
                            <div class="flex items-center gap-3">
                                <span class="text-lg">{{ $lesson['emoji'] }}</span>
                                <span class="text-sm font-bold text-slate-200">{{ $lesson['name'] }}</span>
                            </div>
                            <div class="flex items-center gap-2">
                                @if($lesson['pct'] === 100)
                                    <span class="text-xs bg-draco-emerald/20 text-draco-emerald-light border border-draco-emerald/30 px-3 py-1 rounded-full font-bold">✓ Completado</span>
                                @elseif($lesson['pct'] === 0)
                                    <span class="text-xs bg-slate-700/50 text-slate-400 border border-slate-600 px-3 py-1 rounded-full font-bold">🔒 Bloqueado</span>
                                @else
                                    <span class="text-xs font-extrabold" style="color: {{ $lesson['color'] }}">{{ $lesson['pct'] }}%</span>
                                @endif
                            </div>
                        </div>
                        <div class="w-full bg-slate-900 rounded-full h-2.5 overflow-hidden border border-slate-700 group-hover:h-3 transition-all">
                            <div class="h-full rounded-full transition-all duration-1000"
                                 style="width: {{ $lesson['pct'] }}%; background-color: {{ $lesson['color'] }}">
                            </div>
                        </div>
                    </div>
                    @endforeach
                </div>
            </section>
        </div>

        {{-- Spacer --}}
        <div class="h-8"></div>
    </div>
</div>
@endsection
