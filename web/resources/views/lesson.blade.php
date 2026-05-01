@extends('layouts.app')
@section('title', $lesson->title . ' — Draco')

@section('content')
{{-- JSON-encode the exercises array so Alpine can consume it --}}
@php
    $exercisesJson = json_encode($exercises, JSON_UNESCAPED_UNICODE);
@endphp

<div class="min-h-screen flex flex-col"
     x-data="{
        // ── State ──
        exercises: {{ $exercisesJson }},
        current: 0,
        state: 'reading',       /* reading | answering | correct | completed */
        inputCode: '',
        fails: 0,               /* fail count for current question */
        totalFails: 0,
        lives: 5,
        showHint: false,

        // ── Computed helpers ──
        get totalExercises() { return this.exercises.length },
        get progressPct() { return Math.round((this.current / this.totalExercises) * 100) },
        get ex() { return this.exercises[this.current] },
        get isCorrect() { return this.inputCode.trim().toLowerCase() === this.ex.answer.toLowerCase() },

        // ── Actions ──
        checkAnswer() {
            if (this.isCorrect) {
                this.state = 'correct';
            } else {
                this.fails++;
                this.totalFails++;
                this.lives--;
                if (this.fails >= 2) {
                    this.showHint = true;
                }
                // Shake animation via class toggle
                this.$refs.inputField?.classList.add('animate-shake');
                setTimeout(() => this.$refs.inputField?.classList.remove('animate-shake'), 500);
            }
        },
        nextExercise() {
            if (this.current + 1 >= this.totalExercises) {
                this.state = 'completed';
            } else {
                this.current++;
                this.inputCode = '';
                this.fails = 0;
                this.showHint = false;
                this.state = 'answering';
            }
        }
     }">

    <!-- ── Top Bar: X • Progress • Lives ── -->
    <div class="border-b border-slate-700/50 bg-slate-800/50" style="backdrop-filter: blur(20px);">
        <div class="max-w-4xl mx-auto flex items-center space-x-6 px-8 py-5">
            <a href="{{ route('dashboard') }}" class="text-slate-400 hover:text-white transition p-2 rounded-xl hover:bg-slate-700/50">
                <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="3">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12"/>
                </svg>
            </a>
            <div class="flex-1 bg-slate-800 rounded-full h-4 overflow-hidden border border-slate-700">
                <div class="h-full bg-gradient-to-r from-draco-emerald to-draco-emerald-light rounded-full transition-all duration-500"
                     :style="'width:' + progressPct + '%'"></div>
            </div>
            <div class="flex items-center text-red-500 font-extrabold text-base gap-1.5 bg-red-500/10 px-3 py-1.5 rounded-xl border border-red-500/20">
                <span>❤️</span>
                <span x-text="lives"></span>
            </div>
        </div>
    </div>

    <!-- ── Exercise counter & Lesson title ── -->
    <div class="max-w-4xl mx-auto px-8 pt-6 pb-2" x-show="state !== 'completed'">
        <p class="text-xs text-slate-500 font-bold uppercase tracking-widest">
            Ejercicio <span x-text="current + 1"></span> de <span x-text="totalExercises"></span>
        </p>
        <h2 class="text-2xl font-extrabold text-white mt-2">{{ $lesson->title }}</h2>
    </div>

    <!-- ════════════════════════════════ -->
    <!--  READING STATE (intro card)      -->
    <!-- ════════════════════════════════ -->
    <div class="flex-1 flex items-center justify-center px-8 py-8" x-show="state === 'reading'" x-transition>
        <div class="glass-card rounded-3xl p-10 max-w-2xl w-full text-center animate-fade-in-up">
            <div class="text-6xl mb-6">💡</div>
            <p class="text-xl text-slate-200 font-medium leading-relaxed">
                {{ $lesson->description }}
            </p>
            <p class="text-sm text-slate-400 mt-6">
                Esta lección tiene <strong class="text-draco-gold">8 ejercicios</strong>. ¡Completa todos para ganar XP!
            </p>
        </div>
    </div>

    <!-- ════════════════════════════════ -->
    <!--  ANSWERING STATE                 -->
    <!-- ════════════════════════════════ -->
    <div class="flex-1 flex items-center justify-center px-8 py-8" x-show="state === 'answering'" style="display:none;" x-transition>
        <div class="glass-card rounded-3xl p-10 max-w-2xl w-full animate-fade-in-up">
            <!-- Question -->
            <p class="text-lg text-slate-300 font-semibold mb-8 text-center leading-relaxed" x-text="ex.question"></p>

            <!-- Code block -->
            <div class="bg-slate-900 p-6 rounded-2xl font-mono text-lg shadow-inner border border-slate-700 flex items-center justify-center flex-wrap gap-2">
                <template x-if="ex.code_before">
                    <span class="text-pink-400" x-text="ex.code_before"></span>
                </template>
                <input type="text"
                       x-model="inputCode"
                       x-ref="inputField"
                       @keydown.enter="checkAnswer()"
                       class="bg-slate-800 text-draco-emerald-light font-bold w-36 outline-none border-b-2 border-draco-gold focus:border-draco-emerald transition-colors px-3 py-2 rounded-lg text-center"
                       placeholder="...">
                <template x-if="ex.code_after">
                    <span class="text-slate-300" x-text="ex.code_after"></span>
                </template>
            </div>

            <!-- Hint (only shown after 2 fails) -->
            <div x-show="showHint" x-transition class="mt-6 bg-draco-gold/10 border border-draco-gold/30 rounded-2xl p-4 text-center">
                <p class="text-sm text-draco-gold font-semibold" x-text="ex.hint"></p>
            </div>

            <!-- Fail indicator -->
            <div x-show="fails > 0 && !showHint" x-transition class="mt-6 text-center">
                <p class="text-sm text-red-400 font-semibold">❌ Incorrecto. ¡Inténtalo de nuevo!</p>
            </div>
        </div>
    </div>

    <!-- ════════════════════════════════ -->
    <!--  CORRECT STATE (feedback)        -->
    <!-- ════════════════════════════════ -->
    <div class="flex-1 flex items-center justify-center px-8 py-8" x-show="state === 'correct'" style="display:none;" x-transition>
        <div class="bg-draco-emerald/10 border-2 border-draco-emerald/40 rounded-3xl p-10 shadow-xl text-center max-w-2xl w-full animate-fade-in-up">
            <div class="text-6xl mb-4">🎉</div>
            <h3 class="text-3xl font-extrabold text-draco-emerald-light mb-3">¡Correcto!</h3>
            <div class="bg-slate-900 rounded-xl p-4 font-mono text-lg border border-slate-700 inline-block mt-3">
                <span class="text-pink-400" x-text="ex.code_before"></span><span class="text-draco-emerald-light font-bold" x-text="ex.answer"></span><span class="text-slate-300" x-text="ex.code_after"></span>
            </div>
        </div>
    </div>

    <!-- ════════════════════════════════ -->
    <!--  COMPLETED STATE                 -->
    <!-- ════════════════════════════════ -->
    <div class="flex-1 flex items-center justify-center px-8 py-8" x-show="state === 'completed'" style="display:none;" x-transition>
        <div class="glass-card rounded-3xl p-12 max-w-2xl w-full text-center animate-fade-in-up" style="border-bottom: 4px solid #10b981;">
            <div class="text-7xl mb-6">🏆</div>
            <h2 class="text-4xl font-extrabold text-white mb-3">¡Lección Completada!</h2>
            <p class="text-slate-400 font-semibold mb-8 text-lg">{{ $lesson->title }}</p>

            <div class="flex justify-center gap-10 mb-8">
                <div class="text-center">
                    <div class="text-3xl font-black text-draco-gold">+{{ $lesson->xp_reward }}</div>
                    <div class="text-xs text-slate-400 font-semibold mt-1">XP ganados</div>
                </div>
                <div class="text-center">
                    <div class="text-3xl font-black text-draco-emerald-light" x-text="totalExercises + '/' + totalExercises"></div>
                    <div class="text-xs text-slate-400 font-semibold mt-1">Ejercicios</div>
                </div>
                <div class="text-center">
                    <div class="text-3xl font-black text-red-400" x-text="lives"></div>
                    <div class="text-xs text-slate-400 font-semibold mt-1">Vidas</div>
                </div>
            </div>

            <form action="{{ route('lesson.complete', ['slug' => $lesson->slug]) }}" method="POST">
                @csrf
                <button type="submit" class="w-full bg-draco-emerald py-4 rounded-2xl text-xl font-extrabold text-white shadow-[0_6px_0_0_#059669] active:shadow-[0_0px_0_0_#059669] active:translate-y-1.5 transition-all outline-none uppercase tracking-widest hover:brightness-110">
                    Continuar
                </button>
            </form>
        </div>
    </div>

    <!-- ── Bottom Action Buttons ── -->
    <div class="max-w-4xl mx-auto px-8 pb-8 pt-4" x-show="state !== 'completed'">

        <!-- Start exercises -->
        <button x-show="state === 'reading'"
                @click="state = 'answering'; $nextTick(() => $refs.inputField?.focus())"
                class="w-full bg-draco-emerald py-4 rounded-2xl text-xl font-extrabold text-white shadow-[0_6px_0_0_#059669] active:shadow-[0_0px_0_0_#059669] active:translate-y-1.5 transition-all outline-none uppercase tracking-widest hover:brightness-110">
            Comenzar
        </button>

        <!-- Check answer -->
        <button x-show="state === 'answering'"
                style="display:none;"
                @click="checkAnswer()"
                :class="inputCode.trim().length > 0
                    ? 'bg-draco-emerald shadow-[0_6px_0_0_#059669] active:shadow-[0_0px_0_0_#059669] text-white hover:brightness-110'
                    : 'bg-slate-700 shadow-[0_6px_0_0_#334155] text-slate-400 cursor-not-allowed'"
                :disabled="inputCode.trim().length === 0"
                class="w-full py-4 rounded-2xl text-xl font-extrabold transition-all active:translate-y-1.5 outline-none uppercase tracking-widest">
            Comprobar
        </button>

        <!-- Next exercise -->
        <button x-show="state === 'correct'"
                style="display:none;"
                @click="nextExercise()"
                class="w-full bg-draco-emerald py-4 rounded-2xl text-xl font-extrabold text-white shadow-[0_6px_0_0_#059669] active:shadow-[0_0px_0_0_#059669] active:translate-y-1.5 transition-all outline-none uppercase tracking-widest hover:brightness-110">
            <span x-text="(current + 1 >= totalExercises) ? 'Finalizar' : 'Siguiente'"></span>
        </button>
    </div>

</div>

<style>
    @keyframes shake {
        0%, 100% { transform: translateX(0); }
        20%, 60% { transform: translateX(-6px); }
        40%, 80% { transform: translateX(6px); }
    }
    .animate-shake {
        animation: shake 0.4s ease-in-out;
    }
</style>
@endsection
