@extends('layouts.app')
@section('title', 'Teacher — Crear Pregunta')

@section('content')
<div class="min-h-screen">
    <div class="px-8 py-6 border-b border-slate-700/50 bg-gradient-to-r from-slate-800/80 via-slate-900/50 to-slate-800/80" style="backdrop-filter: blur(20px);">
        <div class="max-w-7xl mx-auto flex items-center justify-between">
            <div>
                <div class="flex items-center gap-3 mb-2">
                    <span class="bg-sky-400/15 border border-sky-400/30 text-sky-400 px-3 py-1 rounded-xl text-xs font-extrabold uppercase tracking-widest">📝 Panel de Profesor</span>
                </div>
                <h1 class="text-3xl font-extrabold text-white tracking-tight">Crear Nueva Pregunta</h1>
                <p class="text-slate-400 mt-1 text-sm">Añade un nuevo reto a la plataforma.</p>
            </div>
            <div class="flex gap-3 items-center">
                <a href="{{ route('teacher.questions.index') }}" class="px-5 py-2.5 glass-card rounded-xl hover:bg-slate-700 hover:text-white transition font-bold text-sm text-slate-300 flex items-center gap-2 group">
                    <svg class="w-4 h-4 group-hover:-translate-x-1 transition-transform" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M15 19l-7-7 7-7"/></svg>
                    Volver
                </a>
            </div>
        </div>
    </div>

    <div class="max-w-4xl mx-auto px-8 py-8" x-data="{ qType: '{{ old('question_type', 'multiple_choice') }}' }">
        @if ($errors->any())
        <div class="bg-red-400/15 border border-red-400/40 text-red-400 px-5 py-3 rounded-2xl mb-6 font-medium text-sm">
            Hay algunos errores en el formulario, por favor revísalos.
        </div>
        @endif

        <div class="glass-card rounded-3xl shadow-xl overflow-hidden animate-fade-in-up p-8">
            <form action="{{ route('teacher.questions.store') }}" method="POST" class="space-y-6">
                @csrf
                
                <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div>
                        <label for="lesson_id" class="block text-sm font-bold text-slate-300 mb-2">Asociar a Lección (Opcional)</label>
                        <select name="lesson_id" id="lesson_id" class="w-full bg-slate-900/50 border border-slate-700/50 rounded-xl px-4 py-3 text-white focus:outline-none focus:ring-2 focus:ring-draco-emerald/50 focus:border-draco-emerald/50 transition-all appearance-none">
                            <option value="">-- General (Sin lección) --</option>
                            @foreach($lessons as $lesson)
                                <option value="{{ $lesson->id }}" {{ old('lesson_id') == $lesson->id ? 'selected' : '' }}>{{ $lesson->title }}</option>
                            @endforeach
                        </select>
                        @error('lesson_id')<p class="text-red-400 text-xs font-bold mt-2">{{ $message }}</p>@enderror
                    </div>

                    <div>
                        <label for="difficulty" class="block text-sm font-bold text-slate-300 mb-2">Dificultad *</label>
                        <select name="difficulty" id="difficulty" required class="w-full bg-slate-900/50 border border-slate-700/50 rounded-xl px-4 py-3 text-white focus:outline-none focus:ring-2 focus:ring-draco-emerald/50 focus:border-draco-emerald/50 transition-all appearance-none">
                            <option value="easy" {{ old('difficulty') == 'easy' ? 'selected' : '' }}>Fácil</option>
                            <option value="intermediate" {{ old('difficulty') == 'intermediate' ? 'selected' : '' }}>Intermedio</option>
                            <option value="hard" {{ old('difficulty') == 'hard' ? 'selected' : '' }}>Difícil</option>
                        </select>
                        @error('difficulty')<p class="text-red-400 text-xs font-bold mt-2">{{ $message }}</p>@enderror
                    </div>

                    <div>
                        <label for="lesson_type" class="block text-sm font-bold text-slate-300 mb-2">Tipo de Lección *</label>
                        <select name="lesson_type" id="lesson_type" required class="w-full bg-slate-900/50 border border-slate-700/50 rounded-xl px-4 py-3 text-white focus:outline-none focus:ring-2 focus:ring-draco-emerald/50 focus:border-draco-emerald/50 transition-all appearance-none">
                            <option value="individual" {{ old('lesson_type') == 'individual' ? 'selected' : '' }}>Lección Individual</option>
                            <option value="group" {{ old('lesson_type') == 'group' ? 'selected' : '' }}>Lección Grupal</option>
                        </select>
                        @error('lesson_type')<p class="text-red-400 text-xs font-bold mt-2">{{ $message }}</p>@enderror
                    </div>

                    <div>
                        <label for="question_type" class="block text-sm font-bold text-slate-300 mb-2">Formato de Pregunta *</label>
                        <select name="question_type" id="question_type" x-model="qType" required class="w-full bg-slate-900/50 border border-slate-700/50 rounded-xl px-4 py-3 text-white focus:outline-none focus:ring-2 focus:ring-draco-emerald/50 focus:border-draco-emerald/50 transition-all appearance-none">
                            <option value="multiple_choice">Selección Múltiple</option>
                            <option value="text">Respuesta Escrita</option>
                        </select>
                        @error('question_type')<p class="text-red-400 text-xs font-bold mt-2">{{ $message }}</p>@enderror
                    </div>
                </div>

                <div>
                    <label for="question_text" class="block text-sm font-bold text-slate-300 mb-2">Enunciado de la Pregunta *</label>
                    <textarea name="question_text" id="question_text" rows="4" required placeholder="Escribe aquí la pregunta..."
                              class="w-full bg-slate-900/50 border border-slate-700/50 rounded-xl px-4 py-3 text-white placeholder-slate-500 focus:outline-none focus:ring-2 focus:ring-draco-emerald/50 focus:border-draco-emerald/50 transition-all">{{ old('question_text') }}</textarea>
                    @error('question_text')<p class="text-red-400 text-xs font-bold mt-2">{{ $message }}</p>@enderror
                </div>

                <!-- Opciones de Selección Múltiple -->
                <div x-show="qType === 'multiple_choice'" x-transition class="bg-slate-800/40 border border-slate-700/50 rounded-2xl p-6">
                    <h3 class="text-white font-bold mb-4">Opciones de Respuesta</h3>
                    
                    <div class="space-y-4">
                        @foreach(['A', 'B', 'C', 'D'] as $letter)
                        <div>
                            <label class="block text-sm font-bold text-slate-400 mb-1">Opción {{ $letter }}</label>
                            <input type="text" name="options[{{ $letter }}]" value="{{ old('options.'.$letter) }}" 
                                   class="w-full bg-slate-900/80 border border-slate-700/50 rounded-lg px-4 py-2.5 text-white focus:outline-none focus:ring-2 focus:ring-sky-500/50 transition-all">
                            @error('options.'.$letter)<p class="text-red-400 text-xs font-bold mt-1">{{ $message }}</p>@enderror
                        </div>
                        @endforeach
                    </div>

                    <div class="mt-6 border-t border-slate-700/50 pt-4">
                        <label for="correct_answer_mc" class="block text-sm font-bold text-draco-emerald-light mb-2">Respuesta Correcta *</label>
                        <select name="correct_answer" id="correct_answer_mc" x-bind:disabled="qType !== 'multiple_choice'" class="w-full bg-draco-emerald/10 border border-draco-emerald/30 rounded-xl px-4 py-3 text-draco-emerald-light focus:outline-none focus:ring-2 focus:ring-draco-emerald/50 transition-all appearance-none">
                            <option value="">Selecciona la correcta...</option>
                            <option value="A" {{ old('correct_answer') == 'A' ? 'selected' : '' }}>Opción A</option>
                            <option value="B" {{ old('correct_answer') == 'B' ? 'selected' : '' }}>Opción B</option>
                            <option value="C" {{ old('correct_answer') == 'C' ? 'selected' : '' }}>Opción C</option>
                            <option value="D" {{ old('correct_answer') == 'D' ? 'selected' : '' }}>Opción D</option>
                        </select>
                        @if($errors->has('correct_answer') && old('question_type') === 'multiple_choice')
                            <p class="text-red-400 text-xs font-bold mt-2">{{ $errors->first('correct_answer') }}</p>
                        @endif
                    </div>
                </div>

                <!-- Respuesta Escrita -->
                <div x-show="qType === 'text'" x-transition class="bg-slate-800/40 border border-slate-700/50 rounded-2xl p-6">
                    <h3 class="text-white font-bold mb-4">Respuesta Esperada</h3>
                    <p class="text-xs text-slate-400 mb-4">Escribe la respuesta exacta que debe ingresar el estudiante.</p>
                    
                    <div>
                        <label for="correct_answer_text" class="block text-sm font-bold text-draco-emerald-light mb-2">Respuesta Correcta *</label>
                        <input type="text" name="correct_answer" id="correct_answer_text" x-bind:disabled="qType !== 'text'" value="{{ old('question_type') === 'text' ? old('correct_answer') : '' }}"
                               class="w-full bg-draco-emerald/10 border border-draco-emerald/30 rounded-xl px-4 py-3 text-draco-emerald-light focus:outline-none focus:ring-2 focus:ring-draco-emerald/50 transition-all">
                        @if($errors->has('correct_answer') && old('question_type') === 'text')
                            <p class="text-red-400 text-xs font-bold mt-2">{{ $errors->first('correct_answer') }}</p>
                        @endif
                    </div>
                </div>

                <div class="pt-6 border-t border-slate-700/50 flex justify-end gap-4">
                    <a href="{{ route('teacher.questions.index') }}" class="px-6 py-3 glass-card rounded-xl hover:bg-slate-700 hover:text-white transition font-bold text-sm text-slate-300">
                        Cancelar
                    </a>
                    <button type="submit" class="bg-draco-emerald hover:bg-draco-emerald-light text-slate-900 px-6 py-3 rounded-xl font-bold text-sm transition-colors shadow-lg shadow-draco-emerald/20 flex items-center gap-2">
                        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"/></svg>
                        Guardar Pregunta
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>
@endsection
