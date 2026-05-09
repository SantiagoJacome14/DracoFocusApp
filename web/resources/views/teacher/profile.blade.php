@extends('layouts.app')
@section('title', 'Mi Perfil — Profesor')

@section('content')
<div class="min-h-screen">
    <div class="px-8 py-6 border-b border-slate-700/50 bg-gradient-to-r from-slate-800/80 via-slate-900/50 to-slate-800/80" style="backdrop-filter: blur(20px);">
        <div class="max-w-5xl mx-auto flex items-center justify-between">
            <div>
                <div class="flex items-center gap-3 mb-2">
                    <span class="bg-draco-gold/15 border border-draco-gold/30 text-draco-gold px-3 py-1 rounded-xl text-xs font-extrabold uppercase tracking-widest">👨‍🏫 Profesor</span>
                </div>
                <h1 class="text-3xl font-extrabold text-white tracking-tight">Mi Perfil</h1>
                <p class="text-slate-400 mt-1 text-sm">Tu identidad pública en Draco.</p>
            </div>
            <a href="{{ route('teacher.profile.edit') }}" class="bg-draco-emerald hover:bg-draco-emerald-light text-slate-900 px-5 py-2.5 rounded-xl font-bold text-sm transition-colors shadow-lg shadow-draco-emerald/20 flex items-center gap-2">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"/></svg>
                Editar Perfil
            </a>
        </div>
    </div>

    <div class="max-w-5xl mx-auto px-8 py-8">
        @if(session('success'))
        <div class="bg-draco-emerald/15 border border-draco-emerald/40 text-draco-emerald-light px-5 py-3 rounded-2xl mb-6 font-medium flex items-center gap-2 animate-fade-in-up">
            <span>✅</span> {{ session('success') }}
        </div>
        @endif

        <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
            <!-- Left: Identity Card -->
            <div class="lg:col-span-1 space-y-5">
                <div class="glass-card rounded-3xl p-6 animate-fade-in-up text-center">
                    @if($userModel->avatar)
                        <img src="{{ $userModel->avatar }}" alt="Avatar" class="w-24 h-24 rounded-full mx-auto mb-4 border-4 border-draco-emerald/30 object-cover shadow-lg">
                    @else
                        <div class="w-24 h-24 rounded-full mx-auto mb-4 bg-gradient-to-br from-draco-emerald to-emerald-700 flex items-center justify-center text-3xl font-black text-white border-4 border-draco-emerald/30 shadow-lg">
                            {{ strtoupper(substr($userModel->name, 0, 1)) }}
                        </div>
                    @endif
                    <h2 class="text-xl font-extrabold text-white">{{ $userModel->name }}</h2>
                    <p class="text-slate-400 text-sm mt-1">{{ $userModel->email }}</p>
                    <div class="mt-3">
                        <span class="bg-draco-gold/15 border border-draco-gold/30 text-draco-gold px-3 py-1.5 rounded-xl text-xs font-extrabold uppercase tracking-widest">Profesor</span>
                    </div>
                    @if($userModel->bio)
                        <p class="text-slate-300 text-sm mt-4 leading-relaxed text-left">{{ $userModel->bio }}</p>
                    @else
                        <p class="text-slate-500 text-sm mt-4 italic">Sin biografía todavía.</p>
                    @endif
                </div>

                <div class="glass-card rounded-3xl p-6 animate-fade-in-up animate-delay-1 space-y-4">
                    @if($userModel->specialty)
                    <div class="flex items-start gap-3">
                        <span class="text-draco-gold text-lg">🎓</span>
                        <div>
                            <p class="text-xs text-slate-500 font-bold uppercase tracking-wider">Especialidad</p>
                            <p class="text-slate-200 text-sm font-medium mt-0.5">{{ $userModel->specialty }}</p>
                        </div>
                    </div>
                    @endif
                    @if($userModel->location)
                    <div class="flex items-start gap-3">
                        <span class="text-draco-gold text-lg">📍</span>
                        <div>
                            <p class="text-xs text-slate-500 font-bold uppercase tracking-wider">Ubicación</p>
                            <p class="text-slate-200 text-sm font-medium mt-0.5">{{ $userModel->location }}</p>
                        </div>
                    </div>
                    @endif
                    <div class="flex items-start gap-3">
                        <span class="text-slate-400 text-lg">📅</span>
                        <div>
                            <p class="text-xs text-slate-500 font-bold uppercase tracking-wider">Miembro desde</p>
                            <p class="text-slate-200 text-sm font-medium mt-0.5">{{ $userModel->created_at->format('d M Y') }}</p>
                        </div>
                    </div>
                </div>

                @if($userModel->github_url || $userModel->linkedin_url || $userModel->website_url)
                <div class="glass-card rounded-3xl p-6 animate-fade-in-up animate-delay-2 space-y-3">
                    <p class="text-xs text-slate-500 font-bold uppercase tracking-wider mb-3">Links</p>
                    @if($userModel->github_url)
                    <a href="{{ $userModel->github_url }}" target="_blank" rel="noopener noreferrer" class="flex items-center gap-3 text-slate-300 hover:text-white transition-colors group">
                        <span class="w-8 h-8 bg-slate-700/60 rounded-lg flex items-center justify-center group-hover:bg-slate-600 transition-colors flex-shrink-0 text-sm">GH</span>
                        <span class="text-sm font-medium truncate">GitHub</span>
                    </a>
                    @endif
                    @if($userModel->linkedin_url)
                    <a href="{{ $userModel->linkedin_url }}" target="_blank" rel="noopener noreferrer" class="flex items-center gap-3 text-slate-300 hover:text-sky-400 transition-colors group">
                        <span class="w-8 h-8 bg-slate-700/60 rounded-lg flex items-center justify-center group-hover:bg-sky-500/20 transition-colors flex-shrink-0 text-sm">Li</span>
                        <span class="text-sm font-medium truncate">LinkedIn</span>
                    </a>
                    @endif
                    @if($userModel->website_url)
                    <a href="{{ $userModel->website_url }}" target="_blank" rel="noopener noreferrer" class="flex items-center gap-3 text-slate-300 hover:text-draco-emerald-light transition-colors group">
                        <span class="w-8 h-8 bg-slate-700/60 rounded-lg flex items-center justify-center group-hover:bg-draco-emerald/20 transition-colors flex-shrink-0 text-sm">🔗</span>
                        <span class="text-sm font-medium truncate">Sitio web</span>
                    </a>
                    @endif
                </div>
                @endif
            </div>

            <!-- Right: Stats + Recent Questions -->
            <div class="lg:col-span-2 space-y-5">
                <div class="grid grid-cols-3 gap-4 animate-fade-in-up animate-delay-1">
                    <div class="glass-card rounded-2xl p-5 text-center">
                        <div class="text-3xl font-black text-draco-emerald-light">{{ \App\Models\User::where('role', 'student')->count() }}</div>
                        <div class="text-xs text-slate-400 font-bold uppercase tracking-wider mt-1">Estudiantes</div>
                    </div>
                    <div class="glass-card rounded-2xl p-5 text-center">
                        <div class="text-3xl font-black text-sky-400">{{ \App\Models\Question::where('created_by', $userModel->id)->count() }}</div>
                        <div class="text-xs text-slate-400 font-bold uppercase tracking-wider mt-1">Preguntas</div>
                    </div>
                    <div class="glass-card rounded-2xl p-5 text-center">
                        <div class="text-3xl font-black text-draco-gold">{{ \App\Models\Lesson::count() }}</div>
                        <div class="text-xs text-slate-400 font-bold uppercase tracking-wider mt-1">Lecciones</div>
                    </div>
                </div>

                <div class="glass-card rounded-3xl p-6 animate-fade-in-up animate-delay-2">
                    <div class="flex items-center justify-between mb-5">
                        <h3 class="text-white font-extrabold text-lg">📝 Preguntas recientes</h3>
                        <a href="{{ route('teacher.questions.index') }}" class="text-draco-emerald hover:text-draco-emerald-light text-sm font-bold transition-colors">Ver todas →</a>
                    </div>
                    @php $recentQuestions = \App\Models\Question::where('created_by', $userModel->id)->latest()->take(5)->get(); @endphp
                    @forelse($recentQuestions as $q)
                    <div class="flex items-center justify-between py-3 border-b border-slate-700/40 last:border-0">
                        <div class="flex-1 min-w-0">
                            <p class="text-sm font-medium text-slate-200 truncate">{{ Str::limit($q->question_text, 60) }}</p>
                            <div class="flex items-center gap-3 mt-1">
                                <span class="text-xs text-slate-500">{{ $q->question_type === 'multiple_choice' ? 'Múltiple' : 'Escrita' }}</span>
                                <span class="text-xs text-slate-600">•</span>
                                @if($q->difficulty === 'easy') <span class="text-xs text-draco-emerald font-bold">Fácil</span>
                                @elseif($q->difficulty === 'intermediate') <span class="text-xs text-amber-400 font-bold">Intermedio</span>
                                @else <span class="text-xs text-red-400 font-bold">Difícil</span>
                                @endif
                            </div>
                        </div>
                        <a href="{{ route('teacher.questions.edit', $q) }}" class="ml-4 text-amber-400 hover:text-amber-300 transition-colors flex-shrink-0">
                            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"/></svg>
                        </a>
                    </div>
                    @empty
                    <div class="text-center py-8 text-slate-500">
                        <div class="text-3xl mb-2">📭</div>
                        <p class="text-sm">Aún no has creado preguntas.</p>
                        <a href="{{ route('teacher.questions.create') }}" class="mt-3 inline-block text-draco-emerald text-sm font-bold">Crear primera pregunta →</a>
                    </div>
                    @endforelse
                </div>
            </div>
        </div>
    </div>
</div>
@endsection
