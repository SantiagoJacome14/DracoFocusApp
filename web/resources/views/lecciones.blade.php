@extends('layouts.app')

@section('title', 'Lecciones - Draco')

@section('content')
<div class="p-6 lg:p-10 animate-fade-in-up" x-data="{ filter: 'all' }">
    <div class="mb-10 flex flex-col md:flex-row md:items-end justify-between gap-6">
        <div>
            <h1 class="text-4xl font-black text-white mb-2 tracking-tight">Ruta de <span class="text-transparent bg-clip-text bg-gradient-to-r from-teal-400 to-emerald-500">Aprendizaje</span></h1>
            <p class="text-slate-400">Completa los módulos secuencialmente para dominar la programación.</p>
        </div>
        <div class="flex bg-slate-800/50 p-1 rounded-xl border border-slate-700/50">
            <button @click="filter = 'all'" :class="filter === 'all' ? 'bg-slate-700 text-white border-turquesa/50 shadow-lg shadow-turquesa/10' : 'text-slate-400 border-slate-700/50 hover:bg-slate-800/50'" class="px-6 py-2.5 rounded-xl border font-bold transition-all flex items-center gap-2">
            <span class="w-2 h-2 rounded-full bg-slate-400"></span>
            Todas
        </button>
        <button @click="filter = 'pending'" :class="filter === 'pending' ? 'bg-amber-500/20 text-amber-400 border-amber-500/30' : 'text-slate-400 border-slate-700/50 hover:bg-slate-800/50'" class="px-6 py-2.5 rounded-xl border font-bold transition-all flex items-center gap-2">
            <span class="w-2 h-2 rounded-full bg-amber-400"></span>
            Pendientes
        </button>
        <button @click="filter = 'completed'" :class="filter === 'completed' ? 'bg-emerald-500/20 text-emerald-400 border-emerald-500/30' : 'text-slate-400 border-slate-700/50 hover:bg-slate-800/50'" class="px-6 py-2.5 rounded-xl border font-bold transition-all flex items-center gap-2">
            <span class="w-2 h-2 rounded-full bg-emerald-400"></span>
            Completadas
        </button>
        </div>
    </div>

    <!-- Rejilla de Lecciones -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        @foreach($lessonPath as $lesson)
        <div x-show="filter === 'all' || 
                   (filter === 'pending' && ('{{ $lesson['status'] }}' === 'active' || '{{ $lesson['status'] }}' === 'locked')) || 
                   (filter === 'completed' && '{{ $lesson['status'] }}' === 'completed')"
             x-transition:enter="transition ease-out duration-300"
                 x-transition:enter-start="opacity-0 transform scale-95"
                 x-transition:enter-end="opacity-100 transform scale-100">
                <div class="group relative bg-slate-800/30 border border-slate-700/50 rounded-2xl p-6 h-full transition-all duration-300 {{ $lesson['status'] === 'locked' ? 'opacity-40 grayscale pointer-events-none' : 'hover:border-teal-500/50 hover:bg-slate-800/50 hover:-translate-y-1' }}">
                    
                    @if($lesson['status'] === 'locked')
                        <div class="absolute top-4 right-4 text-slate-500">
                            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
                            </svg>
                        </div>
                    @elseif($lesson['status'] === 'completed')
                        <div class="absolute top-4 right-4 text-emerald-400">
                            <svg class="w-6 h-6" fill="currentColor" viewBox="0 0 20 20">
                                <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
                            </svg>
                        </div>
                    @endif

                    <div class="flex justify-between items-start mb-6">
                        <div class="w-12 h-12 {{ $lesson['status'] === 'completed' ? 'bg-emerald-500/10 text-emerald-500' : 'bg-teal-500/10 text-teal-500' }} rounded-xl flex items-center justify-center font-black text-xl shadow-inner">
                            {{ strtoupper(substr($lesson['type'], 0, 1)) }}
                        </div>
                        <div class="text-right">
                            <span class="text-[10px] font-bold uppercase tracking-widest text-slate-500 block mb-1">Dificultad</span>
                            <div class="flex gap-0.5 justify-end">
                                @for($i = 1; $i <= 5; $i++)
                                    <div class="w-3 h-1 rounded-full {{ $i <= $lesson['difficulty'] ? 'bg-teal-400' : 'bg-slate-700' }}"></div>
                                @endfor
                            </div>
                        </div>
                    </div>

                    <h3 class="text-xl font-bold text-white mb-2 group-hover:text-teal-400 transition-colors">{{ $lesson['name'] }}</h3>
                    <p class="text-slate-400 text-sm mb-6 line-clamp-2">Aprende los conceptos fundamentales de {{ strtolower($lesson['name']) }} con ejercicios interactivos.</p>

                    <div class="space-y-4 mb-8">
                        <div class="flex justify-between items-end">
                            <span class="text-xs font-bold text-slate-500 uppercase">Progreso</span>
                            <span class="text-xs font-black text-white">{{ $lesson['progress'] }}%</span>
                        </div>
                        <div class="w-full h-2 bg-slate-900 rounded-full overflow-hidden border border-slate-700/30">
                            <div class="h-full {{ $lesson['status'] === 'completed' ? 'bg-emerald-500' : 'bg-teal-400' }} transition-all duration-1000" 
                                 style="width: {{ $lesson['progress'] }}%;"></div>
                        </div>
                        <div class="flex items-center gap-2 text-teal-400">
                            <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                                <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                            </svg>
                            <span class="text-xs font-black">+{{ $lesson['xp'] }} XP</span>
                        </div>
                    </div>

                    <!-- Acción -->
                    @if($lesson['status'] === 'active')
                        <a href="{{ route('lesson.show', $lesson['slug']) }}" class="block w-full bg-teal-500 text-white text-center font-bold py-3 rounded-xl hover:bg-teal-400 active:scale-95 transition-all shadow-lg shadow-teal-500/20 group-hover:shadow-teal-500/40">
                            Continuar Lección
                        </a>
                    @elseif($lesson['status'] === 'completed')
                        <a href="{{ route('lesson.show', $lesson['slug']) }}" class="block w-full bg-slate-700/50 text-emerald-400 text-center font-bold py-3 rounded-xl border border-emerald-500/30 hover:bg-emerald-500/10 transition-all">
                            Repasar
                        </a>
                    @else
                        <button disabled class="w-full bg-slate-800 text-slate-600 text-center font-bold py-3 rounded-xl border border-slate-700/50 cursor-not-allowed">
                            Lección Bloqueada
                        </button>
                    @endif
                </div>
            </div>
        @endforeach
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
