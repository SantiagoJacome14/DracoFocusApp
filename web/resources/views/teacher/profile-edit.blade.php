@extends('layouts.app')
@section('title', 'Editar Perfil — Profesor')

@section('content')
<div class="min-h-screen">
    <div class="px-8 py-6 border-b border-slate-700/50 bg-gradient-to-r from-slate-800/80 via-slate-900/50 to-slate-800/80" style="backdrop-filter: blur(20px);">
        <div class="max-w-3xl mx-auto flex items-center justify-between">
            <div>
                <div class="flex items-center gap-3 mb-2">
                    <span class="bg-draco-gold/15 border border-draco-gold/30 text-draco-gold px-3 py-1 rounded-xl text-xs font-extrabold uppercase tracking-widest">👨‍🏫 Profesor</span>
                </div>
                <h1 class="text-3xl font-extrabold text-white tracking-tight">Editar Perfil</h1>
                <p class="text-slate-400 mt-1 text-sm">Actualiza tu información pública.</p>
            </div>
            <a href="{{ route('profile') }}" class="px-5 py-2.5 glass-card rounded-xl hover:bg-slate-700 hover:text-white transition font-bold text-sm text-slate-300 flex items-center gap-2 group">
                <svg class="w-4 h-4 group-hover:-translate-x-1 transition-transform" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M15 19l-7-7 7-7"/></svg>
                Volver
            </a>
        </div>
    </div>

    <div class="max-w-3xl mx-auto px-8 py-8">
        @if ($errors->any())
        <div class="bg-red-400/15 border border-red-400/40 text-red-400 px-5 py-3 rounded-2xl mb-6 font-medium text-sm">
            Por favor corrige los errores del formulario.
        </div>
        @endif

        <div class="glass-card rounded-3xl p-8 animate-fade-in-up">
            <!-- Read-only fields -->
            <div class="mb-8 pb-8 border-b border-slate-700/50">
                <h3 class="text-white font-bold text-base mb-4 flex items-center gap-2">
                    <svg class="w-4 h-4 text-slate-500" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"/></svg>
                    Datos de cuenta (solo lectura)
                </h3>
                <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div>
                        <label class="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-2">Nombre</label>
                        <div class="w-full bg-slate-800/50 border border-slate-700/30 rounded-xl px-4 py-3 text-slate-400 text-sm select-none">{{ $userModel->name }}</div>
                    </div>
                    <div>
                        <label class="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-2">Correo</label>
                        <div class="w-full bg-slate-800/50 border border-slate-700/30 rounded-xl px-4 py-3 text-slate-400 text-sm select-none">{{ $userModel->email }}</div>
                    </div>
                </div>
                <p class="text-xs text-slate-600 mt-3">Estos datos solo pueden ser modificados por un administrador.</p>
            </div>

            <form action="{{ route('teacher.profile.update') }}" method="POST" class="space-y-6">
                @csrf
                @method('PUT')

                <div>
                    <label for="bio" class="block text-sm font-bold text-slate-300 mb-2">Biografía <span class="text-slate-500 font-normal">(Opcional)</span></label>
                    <textarea name="bio" id="bio" rows="4" maxlength="1000" placeholder="Cuéntale a tus estudiantes sobre ti..."
                              class="w-full bg-slate-900/50 border border-slate-700/50 rounded-xl px-4 py-3 text-white placeholder-slate-500 focus:outline-none focus:ring-2 focus:ring-draco-emerald/50 focus:border-draco-emerald/50 transition-all resize-none">{{ old('bio', $userModel->bio) }}</textarea>
                    @error('bio')<p class="text-red-400 text-xs font-bold mt-1">{{ $message }}</p>@enderror
                </div>

                <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div>
                        <label for="specialty" class="block text-sm font-bold text-slate-300 mb-2">Especialidad <span class="text-slate-500 font-normal">(Opcional)</span></label>
                        <input type="text" name="specialty" id="specialty" value="{{ old('specialty', $userModel->specialty) }}" placeholder="Ej: Algoritmos, Backend, IA..."
                               class="w-full bg-slate-900/50 border border-slate-700/50 rounded-xl px-4 py-3 text-white placeholder-slate-500 focus:outline-none focus:ring-2 focus:ring-draco-emerald/50 focus:border-draco-emerald/50 transition-all">
                        @error('specialty')<p class="text-red-400 text-xs font-bold mt-1">{{ $message }}</p>@enderror
                    </div>
                    <div>
                        <label for="location" class="block text-sm font-bold text-slate-300 mb-2">Ubicación <span class="text-slate-500 font-normal">(Opcional)</span></label>
                        <input type="text" name="location" id="location" value="{{ old('location', $userModel->location) }}" placeholder="Ej: Quito, Ecuador"
                               class="w-full bg-slate-900/50 border border-slate-700/50 rounded-xl px-4 py-3 text-white placeholder-slate-500 focus:outline-none focus:ring-2 focus:ring-draco-emerald/50 focus:border-draco-emerald/50 transition-all">
                        @error('location')<p class="text-red-400 text-xs font-bold mt-1">{{ $message }}</p>@enderror
                    </div>
                </div>

                <div class="pt-4 border-t border-slate-700/50">
                    <h3 class="text-white font-bold text-sm mb-4">🔗 Links profesionales</h3>
                    <div class="space-y-4">
                        <div class="flex items-center gap-3">
                            <span class="w-9 h-9 bg-slate-700/60 rounded-lg flex items-center justify-center text-xs font-bold text-slate-400 flex-shrink-0">GH</span>
                            <div class="flex-1">
                                <input type="url" name="github_url" id="github_url" value="{{ old('github_url', $userModel->github_url) }}" placeholder="https://github.com/usuario"
                                       class="w-full bg-slate-900/50 border border-slate-700/50 rounded-xl px-4 py-3 text-white placeholder-slate-500 focus:outline-none focus:ring-2 focus:ring-draco-emerald/50 focus:border-draco-emerald/50 transition-all">
                                @error('github_url')<p class="text-red-400 text-xs font-bold mt-1">{{ $message }}</p>@enderror
                            </div>
                        </div>
                        <div class="flex items-center gap-3">
                            <span class="w-9 h-9 bg-slate-700/60 rounded-lg flex items-center justify-center text-xs font-bold text-sky-400 flex-shrink-0">Li</span>
                            <div class="flex-1">
                                <input type="url" name="linkedin_url" id="linkedin_url" value="{{ old('linkedin_url', $userModel->linkedin_url) }}" placeholder="https://linkedin.com/in/usuario"
                                       class="w-full bg-slate-900/50 border border-slate-700/50 rounded-xl px-4 py-3 text-white placeholder-slate-500 focus:outline-none focus:ring-2 focus:ring-draco-emerald/50 focus:border-draco-emerald/50 transition-all">
                                @error('linkedin_url')<p class="text-red-400 text-xs font-bold mt-1">{{ $message }}</p>@enderror
                            </div>
                        </div>
                        <div class="flex items-center gap-3">
                            <span class="w-9 h-9 bg-slate-700/60 rounded-lg flex items-center justify-center text-lg flex-shrink-0">🔗</span>
                            <div class="flex-1">
                                <input type="url" name="website_url" id="website_url" value="{{ old('website_url', $userModel->website_url) }}" placeholder="https://mipagina.com"
                                       class="w-full bg-slate-900/50 border border-slate-700/50 rounded-xl px-4 py-3 text-white placeholder-slate-500 focus:outline-none focus:ring-2 focus:ring-draco-emerald/50 focus:border-draco-emerald/50 transition-all">
                                @error('website_url')<p class="text-red-400 text-xs font-bold mt-1">{{ $message }}</p>@enderror
                            </div>
                        </div>
                    </div>
                </div>

                <div class="pt-6 border-t border-slate-700/50 flex justify-end gap-4">
                    <a href="{{ route('profile') }}" class="px-6 py-3 glass-card rounded-xl hover:bg-slate-700 hover:text-white transition font-bold text-sm text-slate-300">Cancelar</a>
                    <button type="submit" class="bg-draco-emerald hover:bg-draco-emerald-light text-slate-900 px-6 py-3 rounded-xl font-bold text-sm transition-colors shadow-lg shadow-draco-emerald/20 flex items-center gap-2">
                        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"/></svg>
                        Guardar cambios
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>
@endsection
