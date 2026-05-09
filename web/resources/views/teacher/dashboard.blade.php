@extends('layouts.app')
@section('title', 'Teacher Dashboard')

@section('content')
<div class="min-h-screen">
    <!-- Header -->
    <div class="px-8 py-6 border-b border-slate-700/50 bg-gradient-to-r from-slate-800/80 via-slate-900/50 to-slate-800/80" style="backdrop-filter: blur(20px);">
        <div class="max-w-7xl mx-auto flex items-center justify-between">
            <div>
                <div class="flex items-center gap-3 mb-2">
                    <span class="bg-draco-gold/15 border border-draco-gold/30 text-draco-gold px-3 py-1 rounded-xl text-xs font-extrabold uppercase tracking-widest">👨‍🏫 Panel de Profesor</span>
                </div>
                <h1 class="text-3xl font-extrabold text-white tracking-tight">Bienvenido, {{ auth()->user()->name }}</h1>
                <p class="text-slate-400 mt-1 text-sm">Supervisa el progreso de tus estudiantes.</p>
            </div>
            <div class="flex gap-3 items-center">
                <a href="{{ route('teacher.questions.index') }}" class="bg-draco-emerald hover:bg-draco-emerald-light text-slate-900 px-5 py-2.5 rounded-xl font-bold text-sm transition-colors shadow-lg shadow-draco-emerald/20 flex items-center gap-2">
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-5 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V7h10v2z"/></svg>
                    Mis Preguntas
                </a>
            </div>
        </div>
    </div>

    <div class="max-w-7xl mx-auto px-8 py-8">
        <!-- Cards Resumen -->
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
            <div class="glass-card p-6 rounded-3xl animate-fade-in-up">
                <div class="text-slate-400 text-sm font-bold uppercase tracking-wider mb-2">Total Estudiantes</div>
                <div class="text-4xl font-black text-white">{{ $students->count() }}</div>
            </div>
            <div class="glass-card p-6 rounded-3xl animate-fade-in-up animate-delay-1">
                <div class="text-slate-400 text-sm font-bold uppercase tracking-wider mb-2">Lecciones Promedio</div>
                <div class="text-4xl font-black text-draco-emerald-light">
                    {{ $students->count() > 0 ? round($students->avg('completed_lessons')) : 0 }}
                </div>
            </div>
            <div class="glass-card p-6 rounded-3xl animate-fade-in-up animate-delay-2">
                <div class="text-slate-400 text-sm font-bold uppercase tracking-wider mb-2">Avance Global</div>
                <div class="text-4xl font-black text-sky-400">
                    {{ $students->count() > 0 ? round($students->avg('progress_percentage')) : 0 }}%
                </div>
            </div>
        </div>

        <!-- Tabla Estudiantes -->
        <h2 class="text-xl font-bold text-white mb-4 flex items-center gap-2">
            <svg class="w-6 h-6 text-draco-emerald" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z"/></svg>
            Progreso de Estudiantes
</h2>
        <div class="glass-card rounded-3xl shadow-xl overflow-hidden animate-fade-in-up animate-delay-3">
            <div class="overflow-x-auto">
                <table class="w-full text-left text-slate-300">
                    <thead class="bg-slate-900/60 text-slate-400 uppercase text-xs font-extrabold tracking-wider border-b border-slate-700/60">
                        <tr>
                            <th class="px-6 py-5 whitespace-nowrap">Estudiante</th>
                            <th class="px-6 py-5 whitespace-nowrap">Semestre</th>
                            <th class="px-6 py-5 whitespace-nowrap">Lecciones (Comp/Total)</th>
                            <th class="px-6 py-5 whitespace-nowrap">Pendientes</th>
                            <th class="px-6 py-5 whitespace-nowrap">Avance</th>
                        </tr>
                    </thead>
                    <tbody class="divide-y divide-slate-700/30">
                        @forelse($students as $student)
                        <tr class="hover:bg-slate-700/20 transition-colors group">
                            <td class="px-6 py-5 whitespace-nowrap">
                                <div class="flex items-center gap-4">
                                    <div class="flex-shrink-0 h-11 w-11 bg-gradient-to-br from-slate-600 to-slate-700 rounded-2xl flex items-center justify-center text-lg font-bold text-white border border-slate-600/50 group-hover:border-draco-emerald/30 transition-colors">
                                        {{ strtoupper(substr($student->name, 0, 1)) }}
                                    </div>
                                    <div>
                                        <div class="text-sm font-bold text-white">{{ $student->name }}</div>
                                        <div class="text-xs text-slate-400">{{ $student->email }}</div>
                                    </div>
                                </div>
                            </td>
                            <td class="px-6 py-5 whitespace-nowrap">
                                @if($student->semester)
                                    <span class="bg-slate-700/30 text-slate-300 border border-slate-600/50 px-3 py-1.5 rounded-xl text-xs font-extrabold tracking-widest">{{ $student->semester }}</span>
                                @else
                                    <span class="text-slate-500 text-sm">-</span>
                                @endif
                            </td>
                            <td class="px-6 py-5 whitespace-nowrap">
                                <div class="text-sm font-bold text-draco-emerald-light">{{ $student->completed_lessons }} <span class="text-slate-500 font-normal">/ {{ $student->total_lessons }}</span></div>
                            </td>
                            <td class="px-6 py-5 whitespace-nowrap">
                                <span class="text-amber-400 font-bold">{{ $student->pending_lessons }}</span>
                            </td>
                            <td class="px-6 py-5 whitespace-nowrap">
                                <div class="flex items-center gap-3">
                                    <div class="w-full bg-slate-700/50 rounded-full h-2.5 max-w-[100px]">
                                        <div class="bg-draco-emerald h-2.5 rounded-full" style="width: {{ $student->progress_percentage }}%"></div>
                                    </div>
                                    <span class="text-sm font-bold text-white">{{ $student->progress_percentage }}%</span>
                                </div>
                            </td>
                        </tr>
                        @empty
                        <tr>
                            <td colspan="5" class="px-6 py-12 text-center text-slate-400 font-medium">
                                <div class="text-4xl mb-3">📭</div>
                                No hay estudiantes registrados.
                            </td>
                        </tr>
                        @endforelse
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>
@endsection
