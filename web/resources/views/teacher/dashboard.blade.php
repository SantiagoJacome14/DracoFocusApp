@extends('layouts.app')
@section('title', 'Dashboard Profesor — Draco')

@section('content')
<div class="min-h-screen">
    <div class="px-8 py-6 border-b border-slate-700/50 bg-gradient-to-r from-slate-800/80 via-slate-900/50 to-slate-800/80" style="backdrop-filter: blur(20px);">
        <div class="max-w-7xl mx-auto">
            <div class="flex items-center gap-3 mb-2">
                <span class="bg-sky-400/15 border border-sky-400/30 text-sky-400 px-3 py-1 rounded-xl text-xs font-extrabold uppercase tracking-widest">👨‍🏫 Panel Profesor</span>
            </div>
            <h1 class="text-3xl font-extrabold text-white tracking-tight">Bienvenido, {{ auth()->user()->name }}</h1>
            <p class="text-slate-400 mt-1">Aquí podrás gestionar tus grupos y ver el progreso de tus estudiantes.</p>
        </div>
    </div>

    <div class="max-w-7xl mx-auto px-8 py-8">
        <div class="glass-card rounded-3xl p-12 text-center border-dashed border-2 border-slate-700/50">
            <div class="text-6xl mb-6">🛠️</div>
            <h2 class="text-2xl font-bold text-white mb-2">Módulo en construcción</h2>
            <p class="text-slate-400 max-w-md mx-auto">Estamos trabajando para traerte las mejores herramientas de gestión académica. ¡Vuelve pronto!</p>
        </div>
    </div>
</div>
@endsection
