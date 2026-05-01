@extends('layouts.app')
@section('title', 'Admin — Gestión de Usuarios')

@section('content')
<div class="min-h-screen">
    <!-- Header -->
    <div class="px-8 py-6 border-b border-slate-700/50 bg-gradient-to-r from-slate-800/80 via-slate-900/50 to-slate-800/80" style="backdrop-filter: blur(20px);">
        <div class="max-w-7xl mx-auto flex items-center justify-between">
            <div>
                <div class="flex items-center gap-3 mb-2">
                    <span class="bg-draco-gold/15 border border-draco-gold/30 text-draco-gold px-3 py-1 rounded-xl text-xs font-extrabold uppercase tracking-widest">⚡ Panel Admin</span>
                </div>
                <h1 class="text-3xl font-extrabold text-white tracking-tight">Gestión de Usuarios</h1>
                <p class="text-slate-400 mt-1 text-sm">Administra los aprendices de Draco.</p>
            </div>
            <div class="flex gap-3 items-center">
                <a href="{{ route('dashboard') }}" class="px-5 py-2.5 glass-card rounded-xl hover:bg-slate-700 hover:text-white transition font-bold text-sm text-slate-300 flex items-center gap-2 group">
                    <svg class="w-4 h-4 group-hover:-translate-x-1 transition-transform" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M15 19l-7-7 7-7"/></svg>
                    Volver
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
                            <th class="px-6 py-5 whitespace-nowrap">Usuario</th>
                            <th class="px-6 py-5 whitespace-nowrap">Rol</th>
                            <th class="px-6 py-5 whitespace-nowrap">Estadísticas</th>
                            <th class="px-6 py-5 whitespace-nowrap">Lecciones</th>
                            <th class="px-6 py-5 whitespace-nowrap">Registro</th>
                            <th class="px-6 py-5 text-right whitespace-nowrap">Acciones</th>
                        </tr>
                    </thead>
                    <tbody class="divide-y divide-slate-700/30">
                        @forelse($users as $user)
                        <tr class="hover:bg-slate-700/20 transition-colors group">
                            <td class="px-6 py-5 whitespace-nowrap">
                                <div class="flex items-center gap-4">
                                    <div class="flex-shrink-0 h-11 w-11 bg-gradient-to-br from-slate-600 to-slate-700 rounded-2xl flex items-center justify-center text-lg font-bold text-white border border-slate-600/50 group-hover:border-draco-emerald/30 transition-colors">
                                        {{ strtoupper(substr($user->name, 0, 1)) }}
                                    </div>
                                    <div>
                                        <div class="text-sm font-bold text-white">{{ $user->name }}</div>
                                        <div class="text-xs text-slate-400">{{ $user->email }}</div>
                                    </div>
                                </div>
                            </td>
                            <td class="px-6 py-5 whitespace-nowrap">
                                @if($user->is_admin)
                                    <span class="bg-draco-gold/15 text-draco-gold border border-draco-gold/30 px-3 py-1.5 rounded-xl text-xs font-extrabold uppercase tracking-widest">Admin</span>
                                @else
                                    <span class="bg-slate-700/30 text-slate-300 border border-slate-600/50 px-3 py-1.5 rounded-xl text-xs font-extrabold uppercase tracking-widest">Estudiante</span>
                                @endif
                            </td>
                            <td class="px-6 py-5 whitespace-nowrap">
                                <div class="flex flex-col gap-1.5">
                                    <span class="text-xs font-bold text-draco-gold">⭐ {{ $user->total_xp }} <span class="text-slate-500 font-normal">XP</span></span>
                                    <span class="text-xs font-bold text-orange-400">🔥 {{ $user->current_streak }} <span class="text-slate-500 font-normal">Días</span></span>
                                    <span class="text-xs font-bold text-sky-400">🎯 {{ $user->daily_goal }} <span class="text-slate-500 font-normal">/día</span></span>
                                </div>
                            </td>
                            <td class="px-6 py-5 whitespace-nowrap">
                                <div class="text-sm font-bold text-draco-emerald-light">🎓 {{ $user->progress->count() }} <span class="text-slate-500 font-normal text-xs">completadas</span></div>
                            </td>
                            <td class="px-6 py-5 whitespace-nowrap text-xs text-slate-400 font-medium">
                                🗓️ {{ $user->created_at->format('d M Y') }}
                            </td>
                            <td class="px-6 py-5 whitespace-nowrap text-right text-sm font-medium flex justify-end gap-2">
                                @if(auth()->id() !== $user->id)
                                <form action="{{ route('admin.users.impersonate', $user) }}" method="POST" class="inline-block">
                                    @csrf
                                    <button type="submit" title="Entrar como {{ $user->name }}" class="text-sky-400 hover:text-sky-300 bg-sky-400/10 hover:bg-sky-400/20 px-3 py-2 rounded-xl transition-colors font-bold text-sm border border-sky-400/20 hover:border-sky-400/30 flex items-center gap-1">
                                        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 16l-4-4m0 0l4-4m-4 4h14m-5 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h7a3 3 0 013 3v1"/></svg>
                                        Entrar
                                    </button>
                                </form>
                                @endif
                                <form action="{{ route('admin.users.destroy', $user) }}" method="POST" class="inline-block" onsubmit="return confirm('¿Seguro que deseas eliminar este usuario?');">
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
                            <td colspan="6" class="px-6 py-12 text-center text-slate-400 font-medium">
                                <div class="text-4xl mb-3">📭</div>
                                No hay usuarios registrados en la plataforma.
                            </td>
                        </tr>
                        @endforelse
                    </tbody>
                </table>
            </div>
        </div>
        
        <!-- Pagination -->
        <div class="mt-6">
            {{ $users->links() }}
        </div>
    </div>
</div>
@endsection
