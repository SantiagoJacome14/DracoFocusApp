@extends('layouts.app')

@section('title', 'Configuración - Draco')

@section('content')
<div class="p-6 lg:p-10 animate-fade-in-up" x-data="{ tab: 'perfil' }">
    <div class="mb-10">
        <h1 class="text-4xl font-black text-white mb-2 tracking-tight">Tu <span class="text-transparent bg-clip-text bg-gradient-to-r from-teal-400 to-emerald-500">Configuración</span></h1>
        <p class="text-slate-400">Personaliza tu experiencia de aprendizaje y gestiona tu cuenta.</p>
    </div>

    <!-- Pestañas -->
    <div class="flex gap-4 mb-8 border-b border-slate-700/50">
        <button @click="tab = 'perfil'" :class="tab === 'perfil' ? 'text-teal-400 border-b-2 border-teal-400' : 'text-slate-500'" class="pb-4 px-2 font-bold transition-all">Perfil</button>
        <button @click="tab = 'cuenta'" :class="tab === 'cuenta' ? 'text-teal-400 border-b-2 border-teal-400' : 'text-slate-500'" class="pb-4 px-2 font-bold transition-all">Cuenta</button>
        <button @click="tab = 'notificaciones'" :class="tab === 'notificaciones' ? 'text-teal-400 border-b-2 border-teal-400' : 'text-slate-500'" class="pb-4 px-2 font-bold transition-all">Notificaciones</button>
    </div>

    <div class="row g-6">
        <!-- Columna Izquierda: Foto y Resumen -->
        <div class="col-12 col-lg-4">
            <div class="bg-slate-800/30 border border-slate-700/50 rounded-2xl p-8 text-center sticky top-24">
                <form action="{{ route('profile.update-photo') }}" method="POST" enctype="multipart/form-data" id="photoForm">
                    @csrf
                    <div class="relative inline-block group mb-6">
                        <div class="w-32 h-32 rounded-full overflow-hidden border-4 border-slate-700 p-1 bg-slate-900 shadow-xl flex items-center justify-center">
                            @if($user->avatar)
                                <img src="{{ $user->avatar }}" 
                                     alt="Profile" 
                                     id="profilePreview"
                                     class="w-full h-full object-cover rounded-full">
                            @else
                                <div class="w-full h-full rounded-full bg-gradient-to-br from-draco-emerald to-emerald-700 flex items-center justify-center text-white font-black text-4xl">
                                    {{ strtoupper(substr($user->name, 0, 1)) }}
                                </div>
                            @endif
                        </div>
                        <label for="photoInput" class="absolute inset-0 flex items-center justify-center bg-black/60 opacity-0 group-hover:opacity-100 transition-opacity rounded-full cursor-pointer">
                            <svg class="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 9a2 2 0 012-2h.93a2 2 0 001.664-.89l.812-1.22A2 2 0 0110.07 4h3.86a2 2 0 011.664.89l.812 1.22A2 2 0 0018.07 7H19a2 2 0 012 2v9a2 2 0 01-2 2H5a2 2 0 01-2-2V9z" />
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 13a3 3 0 11-6 0 3 3 0 016 0z" />
                            </svg>
                        </label>
                        <input type="file" name="photo" id="photoInput" class="hidden" accept="image/*" onchange="document.getElementById('photoForm').submit()">
                    </div>
                </form>

                <h3 class="text-xl font-bold text-white mb-1">{{ $user->name }}</h3>
                <p class="text-teal-400 font-medium mb-4">{{ $user->title ?? 'Estudiante de Draco' }}</p>
                
                <div class="grid grid-cols-2 gap-4 pt-6 border-t border-slate-700/50">
                    <div class="text-center">
                        <p class="text-xs text-slate-500 uppercase tracking-widest mb-1">Nivel</p>
                        <p class="text-lg font-black text-white">{{ floor($user->total_xp / 1000) + 1 }}</p>
                    </div>
                    <div class="text-center">
                        <p class="text-xs text-slate-500 uppercase tracking-widest mb-1">XP Total</p>
                        <p class="text-lg font-black text-white">{{ number_format($user->total_xp) }}</p>
                    </div>
                </div>
            </div>
        </div>

        <!-- Columna Derecha: Formularios -->
        <div class="col-12 col-lg-8">
            <div x-show="tab === 'perfil'" class="bg-slate-800/30 border border-slate-700/50 rounded-2xl p-8">
                <form action="{{ route('profile.update') }}" method="POST">
                    @csrf
                    @method('PATCH')
                    
                    <div class="mb-8">
                        <h4 class="text-lg font-bold text-white mb-6">Información Personal</h4>
                        <div class="row g-4">
                            <div class="col-12 col-md-6">
                                <label class="block text-slate-400 text-sm font-medium mb-2">Nombre Completo</label>
                                <input type="text" value="{{ $user->name }}" readonly 
                                       class="w-full bg-slate-900/50 border border-slate-700/50 rounded-xl px-4 py-3 text-slate-500 cursor-not-allowed opacity-60 focus:outline-none shadow-inner">
                                <p class="mt-1 text-xs text-slate-600 italic">El nombre no es editable.</p>
                            </div>
                            <div class="col-12 col-md-6">
                                <label class="block text-slate-400 text-sm font-medium mb-2">Título / Rango</label>
                                <input type="text" value="{{ $user->title ?? 'Estudiante Novato' }}" readonly 
                                       class="w-full bg-slate-900/50 border border-slate-700/50 rounded-xl px-4 py-3 text-slate-500 cursor-not-allowed opacity-60 focus:outline-none shadow-inner">
                                <p class="mt-1 text-xs text-slate-600 italic">Tu título cambia al subir de nivel.</p>
                            </div>
                            <div class="col-12">
                                <label class="block text-slate-400 text-sm font-medium mb-2">Biografía</label>
                                <textarea name="bio" rows="4" 
                                          class="w-full bg-slate-900 border border-slate-700/50 rounded-xl px-4 py-3 text-white focus:ring-2 focus:ring-teal-400 focus:border-transparent transition-all placeholder:text-slate-600"
                                          placeholder="Cuéntanos un poco sobre ti...">{{ $user->bio }}</textarea>
                            </div>
                        </div>
                    </div>

                    <div>
                        <h4 class="text-lg font-bold text-white mb-6">Preferencias Académicas</h4>
                        <div class="row g-4">
                            <div class="col-12 col-md-6">
                                <label class="block text-slate-400 text-sm font-medium mb-2">Meta Diaria de XP</label>
                                <select name="daily_goal" class="w-full bg-slate-900 border border-slate-700/50 rounded-xl px-4 py-3 text-white focus:ring-2 focus:ring-teal-400 outline-none">
                                    <option value="50" {{ $user->daily_goal == 50 ? 'selected' : '' }}>Relajado (50 XP)</option>
                                    <option value="100" {{ $user->daily_goal == 100 ? 'selected' : '' }}>Normal (100 XP)</option>
                                    <option value="200" {{ $user->daily_goal == 200 ? 'selected' : '' }}>Intenso (200 XP)</option>
                                    <option value="500" {{ $user->daily_goal == 500 ? 'selected' : '' }}>Leyenda (500 XP)</option>
                                </select>
                            </div>
                            <div class="col-12 col-md-6">
                                <label class="block text-slate-400 text-sm font-medium mb-2">Lenguaje Favorito</label>
                                <select name="favorite_language" class="w-full bg-slate-900 border border-slate-700/50 rounded-xl px-4 py-3 text-white focus:ring-2 focus:ring-teal-400 outline-none">
                                    <option value="python">Python</option>
                                    <option value="javascript">JavaScript</option>
                                    <option value="kotlin">Kotlin</option>
                                    <option value="php">PHP (Laravel)</option>
                                </select>
                            </div>
                        </div>
                    </div>

                    <div class="mt-10 flex justify-end">
                        <button type="submit" class="bg-gradient-to-r from-teal-500 to-emerald-500 text-white font-bold px-8 py-3 rounded-xl hover:scale-105 active:scale-95 transition-all shadow-lg shadow-teal-500/20">
                            Guardar Cambios
                        </button>
                    </div>
                </form>
            </div>

            <!-- Otros paneles de pestañas (simplificados) -->
            <div x-show="tab === 'cuenta'" class="bg-slate-800/30 border border-slate-700/50 rounded-2xl p-8">
                <h4 class="text-lg font-bold text-white mb-6">Seguridad de la Cuenta</h4>
                <div class="space-y-6">
                    <div>
                        <label class="block text-slate-400 text-sm font-medium mb-2">Correo Electrónico</label>
                        <input type="email" value="{{ $user->email }}" disabled class="w-full bg-slate-900/50 border border-slate-700/50 rounded-xl px-4 py-3 text-slate-500 opacity-60">
                    </div>
                    <button class="text-teal-400 font-bold hover:underline">Cambiar contraseña</button>
                    <div class="pt-6 border-t border-slate-700/50">
                        <button class="text-red-400 font-bold hover:text-red-300">Eliminar cuenta permanentemente</button>
                    </div>
                </div>
            </div>
        </div>
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
