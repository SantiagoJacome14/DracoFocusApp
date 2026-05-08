@extends('layouts.app')
@section('title', 'Admin — Crear Profesor')

@section('content')
<div class="min-h-screen">
    <!-- Header -->
    <div class="px-8 py-6 border-b border-slate-700/50 bg-gradient-to-r from-slate-800/80 via-slate-900/50 to-slate-800/80" style="backdrop-filter: blur(20px);">
        <div class="max-w-7xl mx-auto flex items-center justify-between">
            <div>
                <div class="flex items-center gap-3 mb-2">
                    <span class="bg-draco-gold/15 border border-draco-gold/30 text-draco-gold px-3 py-1 rounded-xl text-xs font-extrabold uppercase tracking-widest">⚡ Panel Admin</span>
                </div>
                <h1 class="text-3xl font-extrabold text-white tracking-tight">Crear Profesor</h1>
                <p class="text-slate-400 mt-1 text-sm">Registra un nuevo profesor en el sistema.</p>
            </div>
            <div class="flex gap-3 items-center">
                <a href="{{ route('admin.teachers.index') }}" class="px-5 py-2.5 glass-card rounded-xl hover:bg-slate-700 hover:text-white transition font-bold text-sm text-slate-300 flex items-center gap-2 group">
                    <svg class="w-4 h-4 group-hover:-translate-x-1 transition-transform" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M15 19l-7-7 7-7"/></svg>
                    Volver
                </a>
            </div>
        </div>
    </div>

    <div class="max-w-3xl mx-auto px-8 py-8">
        <div class="glass-card rounded-3xl shadow-xl overflow-hidden animate-fade-in-up p-8">
            <form action="{{ route('admin.teachers.store') }}" method="POST" class="space-y-6">
                @csrf
                
                <div>
                    <label for="name" class="block text-sm font-bold text-slate-300 mb-2">Nombre completo</label>
                    <input type="text" name="name" id="name" value="{{ old('name') }}" required
                           class="w-full bg-slate-900/50 border border-slate-700/50 rounded-xl px-4 py-3 text-white placeholder-slate-500 focus:outline-none focus:ring-2 focus:ring-draco-emerald/50 focus:border-draco-emerald/50 transition-all">
                    @error('name')
                        <p class="text-red-400 text-xs font-bold mt-2">{{ $message }}</p>
                    @enderror
                </div>

                <div>
                    <label for="email" class="block text-sm font-bold text-slate-300 mb-2">Correo electrónico</label>
                    <input type="email" name="email" id="email" value="{{ old('email') }}" required
                           class="w-full bg-slate-900/50 border border-slate-700/50 rounded-xl px-4 py-3 text-white placeholder-slate-500 focus:outline-none focus:ring-2 focus:ring-draco-emerald/50 focus:border-draco-emerald/50 transition-all">
                    @error('email')
                        <p class="text-red-400 text-xs font-bold mt-2">{{ $message }}</p>
                    @enderror
                </div>

                <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div>
                        <label for="password" class="block text-sm font-bold text-slate-300 mb-2">Contraseña</label>
                        <input type="password" name="password" id="password" required
                               class="w-full bg-slate-900/50 border border-slate-700/50 rounded-xl px-4 py-3 text-white placeholder-slate-500 focus:outline-none focus:ring-2 focus:ring-draco-emerald/50 focus:border-draco-emerald/50 transition-all">
                        @error('password')
                            <p class="text-red-400 text-xs font-bold mt-2">{{ $message }}</p>
                        @enderror
                    </div>

                    <div>
                        <label for="password_confirmation" class="block text-sm font-bold text-slate-300 mb-2">Confirmar contraseña</label>
                        <input type="password" name="password_confirmation" id="password_confirmation" required
                               class="w-full bg-slate-900/50 border border-slate-700/50 rounded-xl px-4 py-3 text-white placeholder-slate-500 focus:outline-none focus:ring-2 focus:ring-draco-emerald/50 focus:border-draco-emerald/50 transition-all">
                    </div>
                </div>

                <div class="pt-6 border-t border-slate-700/50 flex justify-end gap-4">
                    <a href="{{ route('admin.teachers.index') }}" class="px-6 py-3 glass-card rounded-xl hover:bg-slate-700 hover:text-white transition font-bold text-sm text-slate-300">
                        Cancelar
                    </a>
                    <button type="submit" class="bg-draco-emerald hover:bg-draco-emerald-light text-slate-900 px-6 py-3 rounded-xl font-bold text-sm transition-colors shadow-lg shadow-draco-emerald/20 flex items-center gap-2">
                        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"/></svg>
                        Guardar profesor
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>
@endsection
