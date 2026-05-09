@extends('layouts.app')
@section('title', 'Admin — Gestión de Profesores')

@section('content')
<div class="min-h-screen">
    <!-- Header -->
    <div class="px-8 py-6 border-b border-slate-700/50 bg-gradient-to-r from-slate-800/80 via-slate-900/50 to-slate-800/80" style="backdrop-filter: blur(20px);">
        <div class="max-w-7xl mx-auto flex items-center justify-between">
            <div>
                <div class="flex items-center gap-3 mb-2">
                    <span class="bg-draco-gold/15 border border-draco-gold/30 text-draco-gold px-3 py-1 rounded-xl text-xs font-extrabold uppercase tracking-widest">⚡ Panel Admin</span>
                </div>
                <h1 class="text-3xl font-extrabold text-white tracking-tight">Gestión de Profesores</h1>
                <p class="text-slate-400 mt-1 text-sm">Administra los profesores de la plataforma.</p>
            </div>
            <div class="flex gap-3 items-center">
                <a href="{{ route('dashboard') }}" class="px-5 py-2.5 glass-card rounded-xl hover:bg-slate-700 hover:text-white transition font-bold text-sm text-slate-300 flex items-center gap-2 group">
                    <svg class="w-4 h-4 group-hover:-translate-x-1 transition-transform" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M15 19l-7-7 7-7"/></svg>
                    Volver
                </a>
                <a href="{{ route('admin.teachers.create') }}" class="bg-draco-emerald hover:bg-draco-emerald-light text-slate-900 px-5 py-2.5 rounded-xl font-bold text-sm transition-colors shadow-lg shadow-draco-emerald/20 flex items-center gap-2">
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"/></svg>
                    Crear Profesor
                </a>
            </div>
        </div>
    </div>

    <div class="max-w-7xl mx-auto px-8 py-8">
        <!-- Alert -->
        @if(session('success'))
        <div class="bg-draco-emerald/15 border border-draco-emerald/40 text-draco-emerald-light px-5 py-3 rounded-2xl mb-6 font-medium flex items-center gap-2 animate-fade-in-up" role="alert">
            <span>✅</span> {{ session('success') }}
        </div>
        @endif

        <!-- Table Card -->
        <div class="glass-card rounded-3xl shadow-xl overflow-hidden animate-fade-in-up">
            <div class="overflow-x-auto">
                <table class="w-full text-left text-slate-300">
                    <thead class="bg-slate-900/60 text-slate-400 uppercase text-xs font-extrabold tracking-wider border-b border-slate-700/60">
                        <tr>
                            <th class="px-6 py-5 whitespace-nowrap">Profesor</th>
                            <th class="px-6 py-5 whitespace-nowrap">Fecha de registro</th>
                            <th class="px-6 py-5 text-right whitespace-nowrap">Acciones</th>
                        </tr>
                    </thead>
                    <tbody class="divide-y divide-slate-700/30">
                        @forelse($teachers as $teacher)
                        <tr class="hover:bg-slate-700/20 transition-colors group">
                            <td class="px-6 py-5 whitespace-nowrap">
                                <div class="flex items-center gap-4">
                                    <div class="flex-shrink-0 h-11 w-11 bg-gradient-to-br from-slate-600 to-slate-700 rounded-2xl flex items-center justify-center text-lg font-bold text-white border border-slate-600/50 group-hover:border-draco-emerald/30 transition-colors">
                                        {{ strtoupper(substr($teacher->name, 0, 1)) }}
                                    </div>
                                    <div>
                                        <div class="text-sm font-bold text-white">{{ $teacher->name }}</div>
                                        <div class="text-xs text-slate-400">{{ $teacher->email }}</div>
                                    </div>
                                </div>
                            </td>
                            <td class="px-6 py-5 whitespace-nowrap text-xs text-slate-400 font-medium">
                                🗓️ {{ $teacher->created_at->format('d M Y') }}
                            </td>
                            <td class="px-6 py-5 whitespace-nowrap text-right text-sm font-medium flex justify-end gap-2">
                                <a href="{{ route('admin.teachers.edit', $teacher) }}" title="Editar" class="text-amber-400 hover:text-amber-300 bg-amber-400/10 hover:bg-amber-400/20 px-3 py-2 rounded-xl transition-colors font-bold text-sm border border-amber-400/20 hover:border-amber-400/30 flex items-center gap-1">
                                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"/></svg>
                                    Editar
                                </a>
                                <form action="{{ route('admin.teachers.destroy', $teacher) }}" method="POST" class="inline-block" onsubmit="return confirm('¿Seguro que deseas eliminar este profesor?');">
                                    @csrf
                                    @method('DELETE')
                                    <button type="submit" title="Eliminar" class="text-red-400 hover:text-red-300 bg-red-400/10 hover:bg-red-400/20 px-3 py-2 rounded-xl transition-colors font-bold text-sm border border-red-400/20 hover:border-red-400/30 flex items-center gap-1">
                                        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/></svg>
                                        Eliminar
                                    </button>
                                </form>
                            </td>
                        </tr>
                        @empty
                        <tr>
                            <td colspan="3" class="px-6 py-12 text-center text-slate-400 font-medium">
                                <div class="text-4xl mb-3">📭</div>
                                No hay profesores registrados.
                            </td>
                        </tr>
                        @endforelse
                    </tbody>
                </table>
            </div>
        </div>
        
        <!-- Pagination -->
        <div class="mt-6">
            {{ $teachers->links() }}
        </div>
    </div>
</div>
@endsection
