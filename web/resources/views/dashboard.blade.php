@extends('layouts.app')
@section('title', 'Dashboard — Draco')

@section('content')
<div x-data="{
    showProgressModal: false,
    completedSlugs: [],
    totalLessons: {{ count($lessonPath) }},
    lessonData: {{ json_encode(collect($lessonPath)->map(fn($l) => ['id' => $l['id'], 'slug' => $l['slug'], 'status' => $l['status']])) }},
    init() {
        fetch('/api/progress', { headers: { 'Accept': 'application/json', 'X-Requested-With': 'XMLHttpRequest' } })
            .then(r => r.json())
            .then(data => {
                this.completedSlugs = data.completed_lessons || [];
                this.lessonData.forEach(lesson => {
                    if (this.completedSlugs.includes(lesson.slug)) {
                        lesson.status = 'completed';
                    }
                });
            })
            .catch(() => {});
    }
}">
    <section class="dashboard-hero relative bg-gradient-to-r from-slate-800/80 via-slate-900/50 to-slate-800/80 border-b border-slate-700/50 px-8 py-6" style="backdrop-filter: blur(20px);">
        <div class="max-w-7xl mx-auto flex flex-col lg:flex-row justify-between items-center gap-6">

            <!-- Texto principal del dashboard -->
            <div class="flex-1">
                @if(session('error'))
                    <div class="bg-red-500/15 border border-red-500/40 text-red-400 px-4 py-2.5 rounded-2xl mb-4 text-sm font-semibold flex items-center gap-2">
                        <span>⚠️</span> {{ session('error') }}
                    </div>
                @endif

                @if(session('success'))
                    <div class="bg-draco-emerald/15 border border-draco-emerald/40 text-draco-emerald-light px-4 py-2.5 rounded-2xl mb-4 text-sm font-semibold flex items-center gap-2">
                        <span>🎉</span> {{ session('success') }}
                    </div>
                @endif

                <h1 class="text-4xl font-extrabold text-white tracking-tight">
                    ¡Hola, {{ $user['name'] }}!
                </h1>

                <p class="text-draco-emerald-light font-semibold opacity-90 mt-2">
                    ¡Tu aventura de aprendizaje continúa en la app móvil! 🚀
                </p>

                <p class="text-slate-400 mt-2 max-w-lg">
                    Mantente motivado, completa tus lecciones en la app y observa tu progreso brillar en este panel.
                </p>
            </div>

            <!-- Zona derecha: objetivo diario + avatar -->
            <div class="flex items-center gap-6 flex-shrink-0">

                <!-- Objetivo diario -->
                <div class="glass-card rounded-2xl px-5 py-3 min-w-[280px] animate-fade-in-up">
                    <div class="flex justify-between items-center mb-2">
                        <span class="text-xs font-bold text-slate-400 uppercase tracking-widest">
                            🎯 Objetivo Diario
                        </span>

                        <span class="text-sm font-bold text-draco-gold">
                            {{ $user['goal_progress'] }} / {{ $user['daily_goal'] }} XP
                        </span>
                    </div>

                    <div class="w-full bg-slate-800 rounded-full h-3 overflow-hidden relative border border-slate-700 p-0.5">
                        @php
                            $percentage = $user['daily_goal'] > 0
                                ? min(100, ($user['goal_progress'] / $user['daily_goal']) * 100)
                                : 0;
                        @endphp

                        <div class="bg-gradient-to-r from-draco-emerald to-draco-emerald-light h-full rounded-full transition-all duration-1000 relative" style="width: {{ $percentage }}%">
                            <div class="absolute top-0 left-0 right-0 bottom-0 overflow-hidden rounded-full">
                                <div class="w-full h-full opacity-30 bg-stripe-pattern"></div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Avatar / acceso al perfil -->
                <a href="{{ route('profile') }}" class="group relative flex-shrink-0">
                    <div class="w-14 h-14 bg-gradient-to-br from-draco-emerald to-emerald-700 rounded-2xl border-2 border-draco-gold/60 flex items-center justify-center shadow-lg relative group-hover:border-draco-gold transition-all duration-200 group-hover:scale-105">
                        <span class="text-2xl">🐉</span>

                        <div class="absolute -bottom-1.5 -right-1.5 bg-gradient-to-r from-orange-400 to-red-500 rounded-lg w-6 h-6 flex items-center justify-center border-2 border-slate-900 text-xs font-bold text-white shadow-sm">
                            {{ $user['current_streak'] }}
                        </div>
                    </div>
                </a>
            </div>
        </div>
    </section>

    <!-- ── Main Content Area ── -->
    <div class="max-w-7xl mx-auto px-8 py-8">

        <!-- Quick Stats Row -->
        <div class="grid grid-cols-4 gap-5 mb-8">
            <div class="glass-card rounded-2xl p-5 animate-fade-in-up group hover:border-draco-emerald/40 transition-all cursor-default">
                <div class="flex items-center justify-between">
                    <div>
                        <p class="text-xs text-slate-400 font-bold uppercase tracking-widest">XP Total</p>
                        <p class="text-2xl font-extrabold text-draco-gold mt-1">{{ $user['total_xp'] ?? 0 }}</p>
                    </div>
                    <div class="w-12 h-12 rounded-xl bg-draco-gold/10 flex items-center justify-center text-2xl group-hover:scale-110 transition-transform">⭐</div>
                </div>
            </div>
            <div class="glass-card rounded-2xl p-5 animate-fade-in-up animate-delay-1 group hover:border-orange-500/40 transition-all cursor-default">
                <div class="flex items-center justify-between">
                    <div>
                        <p class="text-xs text-slate-400 font-bold uppercase tracking-widest">Racha Actual</p>
                        <p class="text-2xl font-extrabold text-orange-400 mt-1">{{ $user['current_streak'] }} días</p>
                    </div>
                    <div class="w-12 h-12 rounded-xl bg-orange-500/10 flex items-center justify-center text-2xl group-hover:scale-110 transition-transform">🔥</div>
                </div>
            </div>
            <div class="glass-card rounded-2xl p-5 animate-fade-in-up animate-delay-2 group hover:border-sky-500/40 transition-all cursor-default">
                <div class="flex items-center justify-between">
                    <div>
                        <p class="text-xs text-slate-400 font-bold uppercase tracking-widest">Meta Diaria</p>
                        <p class="text-2xl font-extrabold text-sky-400 mt-1">{{ $user['daily_goal'] }} XP</p>
                    </div>
                    <div class="w-12 h-12 rounded-xl bg-sky-500/10 flex items-center justify-center text-2xl group-hover:scale-110 transition-transform">🎯</div>
                </div>
            </div>
            <div class="glass-card rounded-2xl p-5 animate-fade-in-up animate-delay-3 group hover:border-purple-500/40 transition-all cursor-default">
                <div class="flex items-center justify-between">
                    <div>
                        <p class="text-xs text-slate-400 font-bold uppercase tracking-widest">Lecciones</p>
                        <p class="text-2xl font-extrabold text-purple-400 mt-1"><span x-text="completedSlugs.length + '/' + totalLessons">{{ collect($lessonPath)->where('status', 'completed')->count() }}/{{ count($lessonPath) }}</span></p>
                    </div>
                    <div class="w-12 h-12 rounded-xl bg-purple-500/10 flex items-center justify-center text-2xl group-hover:scale-110 transition-transform">📚</div>
                </div>
            </div>
        </div>

        <!-- ── Learning Path Section ── -->
        <div class="glass-card rounded-3xl p-8 animate-fade-in-up animate-delay-3">
            <div class="flex items-center justify-between mb-8">
                <div>
                    <h2 class="text-xl font-extrabold text-white flex items-center gap-3">
                        <span class="text-2xl">🗺️</span> Tu Camino de Aprendizaje
                    </h2>
                    <p class="text-sm text-slate-400 mt-1">Completa cada módulo para desbloquear el siguiente</p>
                </div>
                <button @click="showProgressModal = true" class="flex items-center gap-2 bg-slate-700/50 hover:bg-slate-700 px-4 py-2 rounded-xl text-sm font-bold text-slate-300 hover:text-white transition-all border border-slate-600/50">
                    <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 24 24"><path d="M12 2L2 22h20L12 2zm0 4.5l6.5 13h-13L12 6.5z"/></svg>
                    Ver Progreso
                </button>
            </div>

            <!-- Horizontal Scrollable Path -->
            <div class="relative">
                <!-- Horizontal connector line -->
                <div class="absolute top-[60px] left-0 right-0 h-1 bg-slate-700/60 rounded-full z-0" style="margin: 0 60px;"></div>

                <div class="grid gap-6" style="grid-template-columns: repeat({{ count($lessonPath) }}, minmax(140px, 1fr));">
                    @foreach($lessonPath as $index => $node)
                    <div class="relative z-10 flex flex-col items-center text-center group">

                        {{-- Active node --}}
                        <template x-if="lessonData[{{ $index }}]?.status === 'active'">
                        <a href="{{ route('lesson.show', ['slug' => $node['slug']]) }}"
                           class="block outline-none transform transition hover:scale-110 active:scale-95 duration-200">
                            <div class="w-[120px] h-[120px] mx-auto bg-gradient-to-br from-draco-emerald to-emerald-700 rounded-3xl flex items-center justify-center relative shadow-[0_8px_0_0_#059669] group-active:shadow-[0_2px_0_0_#059669] group-active:translate-y-2 transition-all border-2 border-emerald-400/40 pulse-glow">
                                <span class="text-4xl drop-shadow-md">{{ $node['emoji'] }}</span>
                                <div class="absolute -top-2 -right-2 bg-draco-gold w-8 h-8 rounded-xl border-3 border-slate-900 flex items-center justify-center animate-pulse glow-gold">
                                    <span class="text-sm">⭐</span>
                                </div>
                            </div>
                            <div class="mt-4">
                                <span class="text-xs font-extrabold text-draco-emerald-light uppercase tracking-widest">▶ Estudiar</span>
                                <h3 class="text-sm font-extrabold text-white mt-1 leading-tight">{{ $node['name'] }}</h3>
                                <p class="text-slate-400 text-xs mt-0.5">{{ $node['xp'] }} XP</p>
                            </div>
                        </a>
                        </template>

                        {{-- Completed node --}}
                        <template x-if="lessonData[{{ $index }}]?.status === 'completed'">
                        <a href="{{ route('lesson.show', ['slug' => $node['slug']]) }}"
                           class="block outline-none transform transition hover:scale-105 active:scale-95 duration-200">
                            <div class="w-[100px] h-[100px] mx-auto bg-slate-700/80 rounded-3xl flex items-center justify-center relative shadow-[0_6px_0_0_#10b981] group-active:shadow-[0_2px_0_0_#10b981] group-active:translate-y-2 transition-all border-2 border-draco-emerald/40 mt-[10px]">
                                <span class="text-3xl">{{ $node['emoji'] }}</span>
                                <div class="absolute -top-2 -right-2 bg-draco-emerald w-7 h-7 rounded-xl border-3 border-slate-900 flex items-center justify-center font-black text-white text-xs">
                                    ✓
                                </div>
                            </div>
                            <div class="mt-4">
                                <span class="text-xs font-bold text-draco-emerald-light uppercase tracking-widest">↺ Repasar</span>
                                <h3 class="text-sm font-bold text-slate-300 mt-1 leading-tight">{{ $node['name'] }}</h3>
                            </div>
                        </a>
                        </template>

                        {{-- Locked node --}}
                        <template x-if="lessonData[{{ $index }}]?.status === 'locked'">
                        <div class="opacity-40 cursor-not-allowed">
                            <div class="w-[100px] h-[100px] mx-auto bg-slate-800/80 rounded-3xl flex items-center justify-center shadow-[0_6px_0_0_#1e293b] border-2 border-slate-700 mt-[10px]">
                                <span class="text-3xl grayscale">🔒</span>
                            </div>
                            <div class="mt-4">
                                <h3 class="text-sm font-bold text-slate-500 uppercase tracking-widest leading-tight">{{ $node['name'] }}</h3>
                                <p class="text-slate-600 text-xs mt-0.5">{{ $node['xp'] }} XP</p>
                            </div>
                        </div>
                        </template>
                    </div>
                    @endforeach
                </div>
            </div>
        </div>
    </div>

    <!-- ── Progress Modal ── -->
    <div x-show="showProgressModal" 
         x-transition.opacity
         class="fixed inset-0 bg-slate-900/80 backdrop-blur-sm z-50 flex items-center justify-center px-4"
         style="display: none;">
        <div @click.away="showProgressModal = false" 
             class="bg-slate-800 w-full max-w-xl rounded-3xl p-8 border border-slate-700 shadow-2xl relative animate-fade-in-up">
            
            <button @click="showProgressModal = false" class="absolute top-6 right-6 text-slate-500 hover:text-white transition">
                <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="3" d="M6 18L18 6M6 6l12 12"/></svg>
            </button>

            <h2 class="text-2xl font-black text-white mb-6 flex items-center gap-3">
                <span class="text-3xl text-draco-gold">📊</span> Tu Progreso
            </h2>

            <div class="space-y-5 max-h-[60vh] overflow-y-auto pr-2">
                @foreach($lessonPath as $node)
                <div class="flex items-center gap-4">
                    <div class="w-12 h-12 rounded-2xl bg-slate-900 border border-slate-700 flex items-center justify-center text-xl flex-shrink-0">
                        {{ $node['emoji'] }}
                    </div>
                    <div class="flex-1">
                        <div class="flex justify-between items-center mb-1.5">
                            <span class="text-sm font-bold text-slate-200">{{ $node['name'] }}</span>
                             <span class="text-xs font-black" :class="completedSlugs.includes(@json($node['slug'])) ? 'text-draco-emerald-light' : 'text-slate-500'">
                                 <span x-text="completedSlugs.includes(@json($node['slug'])) ? '100%' : '0%'"></span>
                             </span>
                        </div>
                        <div class="w-full bg-slate-900 rounded-full h-2 border border-slate-700 overflow-hidden">
                                 <div class="h-full rounded-full transition-all duration-1000"
                                      :style="'width: ' + (completedSlugs.includes(@json($node['slug'])) ? '100' : '0') + '%; background: linear-gradient(to right, #059669, #34d399)'">
                                 </div>
                        </div>
                    </div>
                </div>
                @endforeach
            </div>

            <button @click="showProgressModal = false" 
                    class="w-full mt-8 bg-draco-emerald py-4 rounded-2xl text-lg font-extrabold text-white shadow-[0_6px_0_0_#059669] active:shadow-[0_0px_0_0_#059669] active:translate-y-1.5 transition-all outline-none uppercase tracking-widest hover:brightness-110">
                ¡Entendido!
            </button>
        </div>
    </div>
</div>
@endsection
