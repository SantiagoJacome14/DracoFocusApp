@extends('layouts.app')

@section('title', 'Configuración | Draco')

@section('content')
<div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
    <div class="mb-8">
        <h1 class="text-3xl font-extrabold text-white">⚙️ Configuración</h1>
        <p class="text-slate-400 mt-1">Administra tu cuenta y preferencias de aprendizaje.</p>
    </div>

    <div class="bg-slate-800 rounded-3xl shadow-xl border border-slate-700 overflow-hidden flex flex-col md:flex-row">
        
        <!-- Sidebar de Navegación -->
        <div class="w-full md:w-64 bg-slate-800/50 border-r border-slate-700 p-6 flex flex-col gap-2">
            <button class="w-full text-left px-4 py-3 rounded-xl font-bold bg-draco-emerald/20 text-draco-emerald border border-draco-emerald/30 transition-colors">
                👤 Cuenta
            </button>
            <button class="w-full text-left px-4 py-3 rounded-xl font-bold text-slate-400 hover:bg-slate-700 hover:text-white transition-colors">
                🔔 Notificaciones
            </button>
            <button class="w-full text-left px-4 py-3 rounded-xl font-bold text-slate-400 hover:bg-slate-700 hover:text-white transition-colors">
                🛡️ Privacidad
            </button>
        </div>

        <!-- Formulario de Configuración -->
        <div class="flex-1 p-6 md:p-8">
            <h2 class="text-xl font-bold text-white border-b border-slate-700 pb-4 mb-6">Detalles de la Cuenta</h2>
            
            <form action="#" method="POST" class="space-y-6">
                @csrf
                @method('PUT')
                
                <div>
                    <label class="block text-sm font-bold text-slate-300 mb-2">Nombre de Usuario</label>
                    <input type="text" value="{{ auth()->user()->name ?? 'Estudiante' }}" class="w-full bg-slate-900 border border-slate-700 rounded-xl px-4 py-3 text-white focus:outline-none focus:ring-2 focus:ring-draco-emerald transition-all" placeholder="Tu nombre...">
                </div>

                <div>
                    <label class="block text-sm font-bold text-slate-300 mb-2">Correo Electrónico</label>
                    <input type="email" value="{{ auth()->user()->email ?? 'tu@correo.com' }}" disabled class="w-full bg-slate-900/50 border border-slate-800 rounded-xl px-4 py-3 text-slate-500 cursor-not-allowed" placeholder="tu@correo.com">
                    <p class="text-xs text-slate-500 mt-2">Registrado a través de Google SSO.</p>
                </div>

                <div class="flex items-center gap-4 pt-4">
                    <button type="submit" class="bg-draco-emerald hover:bg-emerald-600 text-white font-bold py-3 px-8 rounded-xl shadow-[0_4px_0_0_#047857] active:shadow-[0_0px_0_0_#047857] active:translate-y-1 transition-all">
                        Guardar Cambios
                    </button>
                </div>
            </form>
        </div>
        
    </div>
</div>
@endsection
