@extends('layouts.app')
@section('title', 'Panel de Control — Admin')

@section('content')
<div class="min-h-screen" x-data="{ activeTab: 'overview' }">

    {{-- ────────── ADMIN HERO HEADER ────────── --}}
    <div class="relative overflow-hidden">
        {{-- Animated background gradient --}}
        <div class="absolute inset-0 bg-gradient-to-br from-slate-900 via-indigo-950/40 to-slate-900"></div>
        <div class="absolute top-0 right-0 w-96 h-96 bg-draco-gold/5 rounded-full blur-3xl -translate-y-1/2 translate-x-1/4"></div>
        <div class="absolute bottom-0 left-0 w-72 h-72 bg-indigo-500/5 rounded-full blur-3xl translate-y-1/2 -translate-x-1/4"></div>

        <div class="relative px-8 py-8 border-b border-slate-700/60">
            <div class="max-w-7xl mx-auto">
                {{-- Top bar --}}
                <div class="flex items-center justify-between mb-6">
                    <a href="{{ route('dashboard') }}" class="flex items-center gap-2 text-slate-400 hover:text-white transition text-sm font-bold group">
                        <svg class="w-5 h-5 group-hover:-translate-x-1 transition-transform" fill="none" stroke="currentColor" stroke-width="2.5" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M15 19l-7-7 7-7"/></svg>
                        Volver al Dashboard
                    </a>

                    <div class="flex items-center gap-2 bg-gradient-to-r from-draco-gold/15 to-amber-500/10 border border-draco-gold/30 text-draco-gold px-5 py-2.5 rounded-2xl text-xs font-extrabold uppercase tracking-widest">
                        <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 24 24"><path d="M12 1l3.09 6.26L22 8.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01z"/></svg>
                        Administrador
                    </div>
                </div>

                {{-- Admin info row --}}
                <div class="flex items-center gap-8">
                    <div class="relative flex-shrink-0">
                        <div class="w-28 h-28 bg-gradient-to-br from-indigo-500 via-purple-500 to-pink-500 rounded-3xl border-3 border-draco-gold/60 flex items-center justify-center shadow-2xl shadow-indigo-900/40 relative overflow-hidden">
                            <div class="absolute inset-0 bg-gradient-to-t from-black/20 to-transparent"></div>
                            <span class="text-5xl font-black text-white relative z-10">{{ strtoupper(substr($user['name'], 0, 1)) }}</span>
                        </div>
                        <div class="absolute -bottom-2 -right-2 bg-gradient-to-r from-draco-gold to-amber-500 rounded-xl w-9 h-9 flex items-center justify-center border-3 border-slate-900 text-sm font-black text-slate-900 shadow">
                            ⚡
                        </div>
                    </div>
                    <div class="flex-1">
                        <h1 class="text-3xl font-extrabold text-white tracking-tight">{{ $user['name'] }}</h1>
                        <p class="text-slate-400 text-sm mt-1">{{ $user['email'] }}</p>
                        <p class="text-slate-500 text-xs mt-1">Administrador desde {{ $user['member_since'] }}</p>
                    </div>

                    {{-- Quick admin stats pills --}}
                    <div class="flex gap-4">
                        <div class="glass-card rounded-2xl px-6 py-4 text-center hover:border-indigo-500/40 transition-colors group cursor-default">
                            <div class="text-2xl font-black text-indigo-400 group-hover:scale-110 transition-transform">{{ $platformStats['total_users'] }}</div>
                            <div class="text-xs text-slate-400 font-semibold mt-1">Usuarios</div>
                        </div>
                        <div class="glass-card rounded-2xl px-6 py-4 text-center hover:border-purple-500/40 transition-colors group cursor-default">
                            <div class="text-2xl font-black text-purple-400 group-hover:scale-110 transition-transform">{{ $platformStats['total_lessons'] }}</div>
                            <div class="text-xs text-slate-400 font-semibold mt-1">Lecciones</div>
                        </div>
                        <div class="glass-card rounded-2xl px-6 py-4 text-center hover:border-draco-gold/40 transition-colors group cursor-default">
                            <div class="text-2xl font-black text-draco-gold group-hover:scale-110 transition-transform">{{ number_format($platformStats['total_xp_earned']) }}</div>
                            <div class="text-xs text-slate-400 font-semibold mt-1">XP Global</div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    {{-- ────────── TAB NAVIGATION ────────── --}}
    <div class="max-w-7xl mx-auto px-8 pt-6">
        <div class="flex gap-1 bg-slate-800/50 p-1 rounded-2xl border border-slate-700/50 w-fit">
            <button @click="activeTab = 'overview'" 
                    :class="activeTab === 'overview' ? 'bg-gradient-to-r from-indigo-500/20 to-purple-500/20 text-white border-indigo-500/30' : 'text-slate-400 hover:text-slate-200 border-transparent'"
                    class="px-5 py-2.5 rounded-xl text-sm font-bold transition-all border flex items-center gap-2">
                <span>📊</span> Vista General
            </button>
            <button @click="activeTab = 'users'" 
                    :class="activeTab === 'users' ? 'bg-gradient-to-r from-indigo-500/20 to-purple-500/20 text-white border-indigo-500/30' : 'text-slate-400 hover:text-slate-200 border-transparent'"
                    class="px-5 py-2.5 rounded-xl text-sm font-bold transition-all border flex items-center gap-2">
                <span>👥</span> Usuarios
            </button>
            <button @click="activeTab = 'activity'" 
                    :class="activeTab === 'activity' ? 'bg-gradient-to-r from-indigo-500/20 to-purple-500/20 text-white border-indigo-500/30' : 'text-slate-400 hover:text-slate-200 border-transparent'"
                    class="px-5 py-2.5 rounded-xl text-sm font-bold transition-all border flex items-center gap-2">
                <span>📈</span> Actividad
            </button>
        </div>
    </div>

    {{-- ────────── CONTENT AREA ────────── --}}
    <div class="max-w-7xl mx-auto px-8 py-6">

        {{-- ══════════ TAB: OVERVIEW ══════════ --}}
        <div x-show="activeTab === 'overview'" x-transition:enter="transition ease-out duration-200" x-transition:enter-start="opacity-0 translate-y-2" x-transition:enter-end="opacity-100 translate-y-0">

            {{-- Platform Metrics Row --}}
            <div class="grid grid-cols-4 gap-5 mb-8">
                <div class="glass-card rounded-2xl p-5 animate-fade-in-up group hover:border-emerald-500/40 transition-all cursor-default relative overflow-hidden">
                    <div class="absolute top-0 right-0 w-20 h-20 bg-emerald-500/5 rounded-full blur-2xl"></div>
                    <div class="flex items-center justify-between relative">
                        <div>
                            <p class="text-xs text-slate-400 font-bold uppercase tracking-widest">Estudiantes</p>
                            <p class="text-2xl font-extrabold text-emerald-400 mt-1">{{ $platformStats['students'] }}</p>
                            <p class="text-xs text-slate-500 mt-1">de {{ $platformStats['total_users'] }} totales</p>
                        </div>
                        <div class="w-12 h-12 rounded-xl bg-emerald-500/10 flex items-center justify-center text-2xl group-hover:scale-110 transition-transform">👨‍🎓</div>
                    </div>
                </div>

                <div class="glass-card rounded-2xl p-5 animate-fade-in-up animate-delay-1 group hover:border-sky-500/40 transition-all cursor-default relative overflow-hidden">
                    <div class="absolute top-0 right-0 w-20 h-20 bg-sky-500/5 rounded-full blur-2xl"></div>
                    <div class="flex items-center justify-between relative">
                        <div>
                            <p class="text-xs text-slate-400 font-bold uppercase tracking-widest">Completadas</p>
                            <p class="text-2xl font-extrabold text-sky-400 mt-1">{{ $platformStats['total_completions'] }}</p>
                            <p class="text-xs text-slate-500 mt-1">lecciones terminadas</p>
                        </div>
                        <div class="w-12 h-12 rounded-xl bg-sky-500/10 flex items-center justify-center text-2xl group-hover:scale-110 transition-transform">✅</div>
                    </div>
                </div>

                <div class="glass-card rounded-2xl p-5 animate-fade-in-up animate-delay-2 group hover:border-amber-500/40 transition-all cursor-default relative overflow-hidden">
                    <div class="absolute top-0 right-0 w-20 h-20 bg-amber-500/5 rounded-full blur-2xl"></div>
                    <div class="flex items-center justify-between relative">
                        <div>
                            <p class="text-xs text-slate-400 font-bold uppercase tracking-widest">XP Promedio</p>
                            <p class="text-2xl font-extrabold text-amber-400 mt-1">{{ $platformStats['avg_xp'] }}</p>
                            <p class="text-xs text-slate-500 mt-1">por estudiante</p>
                        </div>
                        <div class="w-12 h-12 rounded-xl bg-amber-500/10 flex items-center justify-center text-2xl group-hover:scale-110 transition-transform">⭐</div>
                    </div>
                </div>

                <div class="glass-card rounded-2xl p-5 animate-fade-in-up animate-delay-3 group hover:border-rose-500/40 transition-all cursor-default relative overflow-hidden">
                    <div class="absolute top-0 right-0 w-20 h-20 bg-rose-500/5 rounded-full blur-2xl"></div>
                    <div class="flex items-center justify-between relative">
                        <div>
                            <p class="text-xs text-slate-400 font-bold uppercase tracking-widest">Racha Máx.</p>
                            <p class="text-2xl font-extrabold text-rose-400 mt-1">{{ $platformStats['max_streak'] }} días</p>
                            <p class="text-xs text-slate-500 mt-1">mejor racha global</p>
                        </div>
                        <div class="w-12 h-12 rounded-xl bg-rose-500/10 flex items-center justify-center text-2xl group-hover:scale-110 transition-transform">🔥</div>
                    </div>
                </div>
            </div>

            {{-- Two Column Layout --}}
            <div class="grid grid-cols-2 gap-8 mb-8">

                {{-- Lesson Completion Rates --}}
                <section class="animate-fade-in-up">
                    <h2 class="text-sm font-extrabold text-slate-400 uppercase tracking-widest mb-4 flex items-center gap-2">
                        <span class="text-base">🎓</span> Tasa de Completitud por Lección
                    </h2>
                    <div class="glass-card rounded-3xl p-6 shadow-xl space-y-4">
                        @foreach($lessonStats as $ls)
                        <div class="group">
                            <div class="flex justify-between items-center mb-2">
                                <div class="flex items-center gap-3">
                                    <span class="text-lg">{{ $ls['emoji'] }}</span>
                                    <span class="text-sm font-bold text-slate-200">{{ $ls['title'] }}</span>
                                </div>
                                <div class="flex items-center gap-3">
                                    <span class="text-xs text-slate-400 font-semibold">{{ $ls['completions'] }}/{{ $platformStats['students'] }}</span>
                                    <span class="text-xs font-extrabold" style="color: {{ $ls['color'] }}">{{ $ls['rate'] }}%</span>
                                </div>
                            </div>
                            <div class="w-full bg-slate-900 rounded-full h-2.5 overflow-hidden border border-slate-700 group-hover:h-3 transition-all">
                                <div class="h-full rounded-full transition-all duration-1000"
                                     style="width: {{ $ls['rate'] }}%; background-color: {{ $ls['color'] }}">
                                </div>
                            </div>
                        </div>
                        @endforeach
                    </div>
                </section>

                {{-- Recent Registrations --}}
                <section class="animate-fade-in-up animate-delay-1">
                    <h2 class="text-sm font-extrabold text-slate-400 uppercase tracking-widest mb-4 flex items-center gap-2">
                        <span class="text-base">🆕</span> Registros Recientes
                    </h2>
                    <div class="glass-card rounded-3xl p-6 shadow-xl space-y-3 h-full">
                        @forelse($recentUsers as $ru)
                        <div class="flex items-center gap-4 p-3 rounded-2xl hover:bg-slate-700/30 transition-colors group">
                            <div class="w-10 h-10 bg-gradient-to-br from-indigo-500 to-purple-600 rounded-xl flex items-center justify-center text-sm font-black text-white flex-shrink-0 group-hover:scale-110 transition-transform">
                                {{ strtoupper(substr($ru->name, 0, 1)) }}
                            </div>
                            <div class="flex-1 min-w-0">
                                <p class="text-sm font-bold text-white truncate">{{ $ru->name }}</p>
                                <p class="text-xs text-slate-400 truncate">{{ $ru->email }}</p>
                            </div>
                            <div class="text-right flex-shrink-0">
                                <p class="text-xs text-slate-500 font-semibold">{{ $ru->created_at->diffForHumans() }}</p>
                                <p class="text-xs font-bold {{ $ru->total_xp > 0 ? 'text-draco-gold' : 'text-slate-600' }}">{{ $ru->total_xp }} XP</p>
                            </div>
                        </div>
                        @empty
                        <div class="text-center py-8 text-slate-500">
                            <div class="text-3xl mb-2">📭</div>
                            <p class="text-sm font-semibold">No hay registros recientes</p>
                        </div>
                        @endforelse
                    </div>
                </section>
            </div>

            {{-- Top Students Ranking --}}
            <section class="animate-fade-in-up animate-delay-2">
                <h2 class="text-sm font-extrabold text-slate-400 uppercase tracking-widest mb-4 flex items-center gap-2">
                    <span class="text-base">🏆</span> Top Estudiantes
                </h2>
                <div class="glass-card rounded-3xl p-6 shadow-xl">
                    <div class="grid grid-cols-3 gap-6">
                        @foreach($topStudents as $idx => $ts)
                        <div class="flex items-center gap-4 p-4 rounded-2xl {{ $idx === 0 ? 'bg-draco-gold/5 border border-draco-gold/20' : 'bg-slate-800/30 border border-slate-700/30' }} hover:border-indigo-500/30 transition-all group">
                            <div class="relative">
                                <div class="w-14 h-14 bg-gradient-to-br {{ $idx === 0 ? 'from-amber-400 to-orange-500' : ($idx === 1 ? 'from-slate-300 to-slate-400' : 'from-amber-600 to-amber-700') }} rounded-2xl flex items-center justify-center text-xl font-black {{ $idx === 0 ? 'text-slate-900' : 'text-white' }} group-hover:scale-105 transition-transform">
                                    {{ $idx + 1 }}
                                </div>
                            </div>
                            <div class="flex-1 min-w-0">
                                <p class="text-sm font-extrabold text-white truncate">{{ $ts->name }}</p>
                                <p class="text-xs text-slate-400 truncate">{{ $ts->email }}</p>
                            </div>
                            <div class="text-right">
                                <p class="text-lg font-black text-draco-gold">{{ $ts->total_xp }}</p>
                                <p class="text-xs text-slate-500 font-semibold">XP</p>
                            </div>
                        </div>
                        @endforeach
                    </div>
                </div>
            </section>
        </div>

        {{-- ══════════ TAB: USERS ══════════ --}}
        <div x-show="activeTab === 'users'" style="display: none;" x-transition:enter="transition ease-out duration-200" x-transition:enter-start="opacity-0 translate-y-2" x-transition:enter-end="opacity-100 translate-y-0">
            <div class="flex items-center justify-between mb-6">
                <h2 class="text-lg font-extrabold text-white flex items-center gap-2">
                    <span>👥</span> Todos los Usuarios
                </h2>
                <a href="{{ route('admin.users') }}" class="flex items-center gap-2 bg-indigo-500/15 border border-indigo-500/30 text-indigo-400 px-5 py-2.5 rounded-xl text-sm font-bold hover:bg-indigo-500/25 transition">
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M10 6H6a2 2 0 00-2 2v10a2 2 0 002 2h10a2 2 0 002-2v-4M14 4h6m0 0v6m0-6L10 14"/></svg>
                    Panel Completo
                </a>
            </div>

            <div class="glass-card rounded-3xl shadow-xl overflow-hidden">
                <div class="overflow-x-auto">
                    <table class="w-full text-left text-slate-300">
                        <thead class="bg-slate-900/60 text-slate-400 uppercase text-xs font-extrabold tracking-wider border-b border-slate-700/60">
                            <tr>
                                <th class="px-6 py-4 whitespace-nowrap">#</th>
                                <th class="px-6 py-4 whitespace-nowrap">Usuario</th>
                                <th class="px-6 py-4 whitespace-nowrap">Rol</th>
                                <th class="px-6 py-4 whitespace-nowrap">XP</th>
                                <th class="px-6 py-4 whitespace-nowrap">Racha</th>
                                <th class="px-6 py-4 whitespace-nowrap">Lecciones</th>
                                <th class="px-6 py-4 whitespace-nowrap">Registro</th>
                            </tr>
                        </thead>
                        <tbody class="divide-y divide-slate-700/30">
                            @foreach($allUsers as $idx => $au)
                            <tr class="hover:bg-slate-700/20 transition-colors">
                                <td class="px-6 py-4 text-sm font-bold text-slate-500">{{ $idx + 1 }}</td>
                                <td class="px-6 py-4 whitespace-nowrap">
                                    <div class="flex items-center gap-3">
                                        <div class="w-9 h-9 bg-gradient-to-br from-slate-600 to-slate-700 rounded-xl flex items-center justify-center text-sm font-bold text-white border border-slate-600/50">
                                            {{ strtoupper(substr($au->name, 0, 1)) }}
                                        </div>
                                        <div>
                                            <p class="text-sm font-bold text-white">{{ $au->name }}</p>
                                            <p class="text-xs text-slate-400">{{ $au->email }}</p>
                                        </div>
                                    </div>
                                </td>
                                <td class="px-6 py-4">
                                    @if($au->is_admin)
                                        <span class="bg-draco-gold/15 text-draco-gold border border-draco-gold/30 px-3 py-1 rounded-xl text-xs font-extrabold uppercase tracking-widest">Admin</span>
                                    @else
                                        <span class="bg-slate-700/30 text-slate-300 border border-slate-600/50 px-3 py-1 rounded-xl text-xs font-extrabold uppercase tracking-widest">Estudiante</span>
                                    @endif
                                </td>
                                <td class="px-6 py-4 text-sm font-bold text-draco-gold">{{ $au->total_xp }}</td>
                                <td class="px-6 py-4 text-sm font-bold text-orange-400">{{ $au->current_streak }}d</td>
                                <td class="px-6 py-4 text-sm font-bold text-emerald-400">{{ $au->progress_count }}</td>
                                <td class="px-6 py-4 text-xs text-slate-400">{{ $au->created_at->format('d M Y') }}</td>
                            </tr>
                            @endforeach
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        {{-- ══════════ TAB: ACTIVITY ══════════ --}}
        <div x-show="activeTab === 'activity'" style="display: none;" x-transition:enter="transition ease-out duration-200" x-transition:enter-start="opacity-0 translate-y-2" x-transition:enter-end="opacity-100 translate-y-0">

            <div class="grid grid-cols-2 gap-8">
                {{-- Weekly Registration Trend --}}
                <section>
                    <h2 class="text-sm font-extrabold text-slate-400 uppercase tracking-widest mb-4 flex items-center gap-2">
                        <span class="text-base">📅</span> Registros Esta Semana
                    </h2>
                    <div class="glass-card rounded-3xl p-6 shadow-xl">
                        <div class="flex items-end justify-between gap-3 h-32">
                            @php $maxReg = max(array_values($weeklyRegistrations)) ?: 1; @endphp
                            @foreach($weeklyRegistrations as $day => $count)
                                @php
                                    $pct = round(($count / $maxReg) * 100);
                                    $isMax = $count === max(array_values($weeklyRegistrations)) && $count > 0;
                                @endphp
                                <div class="flex flex-col items-center gap-1 flex-1">
                                    <span class="text-xs text-slate-400 font-bold {{ $isMax ? 'text-indigo-400' : '' }}">
                                        @if($count > 0) {{ $count }} @endif
                                    </span>
                                    <div class="w-full rounded-lg transition-all duration-700 relative overflow-hidden"
                                         style="height: {{ max(8, $pct) }}%; background: {{ $isMax ? 'linear-gradient(to top, #6366f1, #a78bfa)' : 'rgb(51 65 85)' }}">
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
                            <span class="text-indigo-400">{{ array_sum(array_values($weeklyRegistrations)) }} nuevos registros</span>
                        </div>
                    </div>
                </section>

                {{-- Recent Lesson Completions --}}
                <section>
                    <h2 class="text-sm font-extrabold text-slate-400 uppercase tracking-widest mb-4 flex items-center gap-2">
                        <span class="text-base">✅</span> Últimas Lecciones Completadas
                    </h2>
                    <div class="glass-card rounded-3xl p-6 shadow-xl space-y-3">
                        @forelse($recentCompletions as $rc)
                        <div class="flex items-center gap-4 p-3 rounded-2xl hover:bg-slate-700/30 transition-colors">
                            <div class="w-10 h-10 bg-emerald-500/10 border border-emerald-500/20 rounded-xl flex items-center justify-center text-lg flex-shrink-0">
                                ✅
                            </div>
                            <div class="flex-1 min-w-0">
                                <p class="text-sm font-bold text-white truncate">{{ $rc->user_name }}</p>
                                <p class="text-xs text-slate-400">completó <span class="text-emerald-400 font-semibold">{{ $rc->lesson_title }}</span></p>
                            </div>
                            <p class="text-xs text-slate-500 font-semibold flex-shrink-0">{{ \Carbon\Carbon::parse($rc->completed_at)->diffForHumans() }}</p>
                        </div>
                        @empty
                        <div class="text-center py-8 text-slate-500">
                            <div class="text-3xl mb-2">📭</div>
                            <p class="text-sm font-semibold">No hay actividad reciente</p>
                        </div>
                        @endforelse
                    </div>
                </section>
            </div>
        </div>

    </div>

    {{-- Spacer --}}
    <div class="h-8"></div>
</div>
@endsection
