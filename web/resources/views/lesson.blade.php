@extends('layouts.app')
@section('title', $lesson->title . ' — Draco')

@section('content')
<div class="min-h-screen flex flex-col"
     x-data="lessonPage(@js($exercises), '{{ $lesson->slug }}', @json($currentExercise ?? 0))">

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

    <div class="max-w-4xl mx-auto px-8 pt-6 pb-2" x-show="state !== 'completed'">
        <p class="text-xs text-slate-500 font-bold uppercase tracking-widest">
            Ejercicio <span x-text="current + 1"></span> de <span x-text="totalExercises"></span>
        </p>
        <h2 class="text-2xl font-extrabold text-white mt-2">{{ $lesson->title }}</h2>
    </div>

    <div class="flex-1 flex items-center justify-center px-8 py-8" x-show="state === 'reading'" x-transition>
        <div class="glass-card rounded-3xl p-10 max-w-2xl w-full text-center animate-fade-in-up">
            <div class="text-6xl mb-6">💡</div>
            <p class="text-xl text-slate-200 font-medium leading-relaxed">
                {{ $lesson->description }}
            </p>
            <p class="text-sm text-slate-400 mt-6">
                Esta lección tiene <strong class="text-draco-gold" x-text="totalExercises"></strong> ejercicios. ¡Completa todos para ganar XP!
            </p>
        </div>
    </div>

    <div class="flex-1 flex items-center justify-center px-8 py-8" x-show="state === 'answering'" style="display:none;" x-transition>
        <div class="glass-card rounded-3xl p-10 max-w-2xl w-full animate-fade-in-up">
            <p class="text-lg text-slate-300 font-semibold mb-8 text-center leading-relaxed" x-text="ex.question"></p>

            <template x-if="ex.type === 'multiple'">
                <div class="grid gap-3">
                    <template x-for="option in ex.options" :key="option">
                        <button type="button"
                                @click="selectOption(option)"
                                :class="inputCode === option
                                    ? 'border-draco-emerald bg-draco-emerald/20 text-draco-emerald-light'
                                    : 'border-slate-700 bg-slate-900 text-slate-200 hover:border-draco-gold'"
                                class="w-full text-left px-5 py-4 rounded-2xl border-2 font-bold transition">
                            <span x-text="option"></span>
                        </button>
                    </template>
                </div>
            </template>

            <template x-if="ex.type === 'fill'">
                <div class="bg-slate-900 p-6 rounded-2xl font-mono text-lg shadow-inner border border-slate-700 flex items-center justify-center flex-wrap gap-2">
                    <input type="text"
                           x-model="inputCode"
                           @keydown.enter="checkAnswer()"
                           class="bg-slate-800 text-draco-emerald-light font-bold w-48 outline-none border-b-2 border-draco-gold focus:border-draco-emerald transition-colors px-3 py-2 rounded-lg text-center"
                           placeholder="Escribe...">
                </div>
            </template>

            <template x-if="ex.type === 'true_false'">
                <div class="grid grid-cols-2 gap-4">
                    <button type="button"
                            @click="selectOption('Verdadero')"
                            :class="inputCode === 'Verdadero'
                                ? 'border-draco-emerald bg-draco-emerald/20 text-draco-emerald-light'
                                : 'border-slate-700 bg-slate-900 text-slate-200 hover:border-draco-gold'"
                            class="px-5 py-4 rounded-2xl border-2 font-black transition">
                        Verdadero
                    </button>

                    <button type="button"
                            @click="selectOption('Falso')"
                            :class="inputCode === 'Falso'
                                ? 'border-draco-emerald bg-draco-emerald/20 text-draco-emerald-light'
                                : 'border-slate-700 bg-slate-900 text-slate-200 hover:border-draco-gold'"
                            class="px-5 py-4 rounded-2xl border-2 font-black transition">
                        Falso
                    </button>
                </div>
            </template>

            <template x-if="ex.type === 'order'">
                <div>
                    <p class="text-sm text-slate-400 font-semibold mb-4 text-center">
                        Selecciona las piezas en el orden correcto:
                    </p>

                    <div class="flex flex-wrap gap-3 justify-center mb-6">
                        <template x-for="item in ex.items" :key="item">
                            <button type="button"
                                    @click="selectOrderItem(item)"
                                    :disabled="selectedOrder.includes(item)"
                                    :class="selectedOrder.includes(item)
                                        ? 'opacity-40 cursor-not-allowed border-slate-700 bg-slate-800'
                                        : 'border-draco-gold bg-slate-900 hover:bg-draco-gold/10'"
                                    class="px-4 py-3 rounded-xl border-2 text-slate-200 font-bold transition">
                                <span x-text="item"></span>
                            </button>
                        </template>
                    </div>

                    <div class="bg-slate-900 border border-slate-700 rounded-2xl p-4 min-h-[70px]">
                        <p class="text-xs text-slate-500 font-bold uppercase mb-3">Tu orden:</p>

                        <div class="flex flex-wrap gap-2">
                            <template x-for="(item, index) in selectedOrder" :key="index">
                                <button type="button"
                                        @click="removeOrderItem(index)"
                                        class="px-3 py-2 rounded-lg bg-draco-emerald/20 text-draco-emerald-light font-bold">
                                    <span x-text="(index + 1) + '. ' + item"></span>
                                </button>
                            </template>
                        </div>
                    </div>
                </div>
            </template>

            <div x-show="fails > 0" x-transition class="mt-6 bg-draco-gold/10 border border-draco-gold/30 rounded-2xl p-4 text-center">
                <p x-show="aiLoading" class="text-sm text-draco-gold font-semibold">
                    Draco esta analizando tu respuesta...
                </p>
                <p x-show="!aiLoading && aiHint" class="text-sm text-draco-gold font-semibold" x-text="aiHint"></p>
                <p x-show="!aiLoading && !aiHint" class="text-sm text-draco-gold font-semibold">
                    Draco detectó un error. Revisa el concepto principal e inténtalo otra vez.
                </p>
            </div>

            <div x-show="fails > 0" x-transition class="mt-4 text-center">
                <p class="text-sm text-red-400 font-semibold">❌ Incorrecto. ¡Inténtalo de nuevo!</p>
            </div>
        </div>
    </div>

    <div class="flex-1 flex items-center justify-center px-8 py-8" x-show="state === 'correct'" style="display:none;" x-transition>
        <div class="bg-draco-emerald/10 border-2 border-draco-emerald/40 rounded-3xl p-10 shadow-xl text-center max-w-2xl w-full animate-fade-in-up">
            <div class="text-6xl mb-4">🎉</div>
            <h3 class="text-3xl font-extrabold text-draco-emerald-light mb-3">¡Correcto!</h3>
            <div class="bg-slate-900 rounded-xl p-4 font-mono text-lg border border-slate-700 inline-block mt-3">
                <span class="text-draco-emerald-light font-bold" x-text="correctAnswerText()"></span>
            </div>
        </div>
    </div>

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

            <form x-ref="completeForm" action="{{ route('lesson.complete', ['slug' => $lesson->slug]) }}" method="POST">
                @csrf
                <input type="hidden" name="score" value="100">
                <input type="hidden" name="completed" value="1">

                <button type="button"
                        @click="completeLesson()"
                        :disabled="syncing"
                        class="w-full bg-draco-emerald py-4 rounded-2xl text-xl font-extrabold text-white shadow-[0_6px_0_0_#059669] active:shadow-[0_0px_0_0_#059669] active:translate-y-1.5 transition-all outline-none uppercase tracking-widest hover:brightness-110 disabled:opacity-60 disabled:cursor-not-allowed">
                    <span x-text="syncing ? 'Sincronizando...' : 'Continuar'"></span>
                </button>
            </form>
        </div>
    </div>

    <div class="max-w-4xl mx-auto px-8 pb-8 pt-4" x-show="state !== 'completed'">
        <button x-show="state === 'reading'"
                @click="state = 'answering'"
                class="w-full bg-draco-emerald py-4 rounded-2xl text-xl font-extrabold text-white shadow-[0_6px_0_0_#059669] active:shadow-[0_0px_0_0_#059669] active:translate-y-1.5 transition-all outline-none uppercase tracking-widest hover:brightness-110">
            Comenzar
        </button>

        <button x-show="state === 'answering'"
                style="display:none;"
                @click="checkAnswer()"
                :class="canCheck
                    ? 'bg-draco-emerald shadow-[0_6px_0_0_#059669] active:shadow-[0_0px_0_0_#059669] text-white hover:brightness-110'
                    : 'bg-slate-700 shadow-[0_6px_0_0_#334155] text-slate-400 cursor-not-allowed'"
                :disabled="!canCheck"
                class="w-full py-4 rounded-2xl text-xl font-extrabold transition-all active:translate-y-1.5 outline-none uppercase tracking-widest">
            Comprobar
        </button>

        <button x-show="state === 'correct'"
                style="display:none;"
                @click="nextExercise()"
                class="w-full bg-draco-emerald py-4 rounded-2xl text-xl font-extrabold text-white shadow-[0_6px_0_0_#059669] active:shadow-[0_0px_0_0_#059669] active:translate-y-1.5 transition-all outline-none uppercase tracking-widest hover:brightness-110">
            <span x-text="(current + 1 >= totalExercises) ? 'Finalizar' : 'Siguiente'"></span>
        </button>
    </div>
</div>

<script>
function lessonPage(exercises, lessonSlug, initialCurrent) {
    return {
        exercises: exercises || [],
        lessonSlug: lessonSlug,
        current: Number(initialCurrent || 0),
        state: Number(initialCurrent || 0) > 0 ? 'answering' : 'reading',
        inputCode: '',
        selectedOrder: [],
        fails: 0,
        totalFails: 0,
        lives: 5,
        showHint: false,
        aiHint: '',
        aiLoading: false,
        aiRequestId: 0,
        syncing: false,

        get totalExercises() {
            return this.exercises.length;
        },

        get progressPct() {
            if (!this.totalExercises) return 0;

            if (this.state === 'completed') {
                return 100;
            }

            return Math.round((this.current / this.totalExercises) * 100);
        },

        get ex() {
            return this.exercises[this.current] || {};
        },

        get canCheck() {
            if (!this.ex || !this.ex.type) return false;

            if (this.ex.type === 'multiple') {
                return this.inputCode !== '';
            }

            if (this.ex.type === 'fill') {
                return String(this.inputCode).trim().length > 0;
            }

            if (this.ex.type === 'true_false') {
                return this.inputCode !== '';
            }

            if (this.ex.type === 'order') {
                return this.selectedOrder.length === (this.ex.correct_answer || []).length;
            }

            return false;
        },

        normalize(value) {
            return String(value ?? '').trim().toLowerCase();
        },

        normalizeTrueFalse(value) {
            const normalized = this.normalize(value);

            if (normalized === 'verdadero') return 'true';
            if (normalized === 'falso') return 'false';

            return normalized;
        },

        get isCorrect() {
            if (!this.ex || !this.ex.type) return false;

            if (this.ex.type === 'multiple') {
                return this.inputCode === this.ex.correct_answer;
            }

            if (this.ex.type === 'fill') {
                return this.normalize(this.inputCode) === this.normalize(this.ex.correct_answer);
            }

            if (this.ex.type === 'true_false') {
                return this.normalizeTrueFalse(this.inputCode) === this.normalizeTrueFalse(this.ex.correct_answer);
            }

            if (this.ex.type === 'order') {
                return JSON.stringify(this.selectedOrder) === JSON.stringify(this.ex.correct_answer);
            }

            return false;
        },

        selectOption(value) {
            this.inputCode = value;
            this.clearAiHint();
        },

        selectOrderItem(item) {
            if (!this.selectedOrder.includes(item)) {
                this.selectedOrder.push(item);
                this.clearAiHint();
            }
        },

        removeOrderItem(index) {
            this.selectedOrder.splice(index, 1);
            this.clearAiHint();
        },

        checkAnswer() {
            if (!this.canCheck) return;

            if (this.isCorrect) {
                this.state = 'correct';
            } else {
                this.fails++;
                this.totalFails++;
                this.lives = Math.max(0, this.lives - 1);
                this.showHint = true;
                this.getAIFeedback();
            }
        },

        nextExercise() {
            if (this.current + 1 >= this.totalExercises) {
                this.state = 'completed';
                return;
            }

            this.current++;
            if (this.current < this.exercises.length - 1) {
                this.saveProgress();
            }
            this.inputCode = '';
            this.selectedOrder = [];
            this.fails = 0;
            this.clearAiHint();
            this.state = 'answering';
        },

        saveProgress() {
            fetch(`/lesson/${encodeURIComponent(this.lessonSlug)}/progress`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json',
                    'X-CSRF-TOKEN': document.querySelector('meta[name="csrf-token"]').content
                },
                body: JSON.stringify({
                    current_exercise: this.current
                })
            }).catch(() => {});
        },

        correctAnswerText() {
            if (!this.ex) return '';

            if (Array.isArray(this.ex.correct_answer)) {
                return this.ex.correct_answer.join(' ');
            }

            return String(this.ex.correct_answer ?? '');
        },

        getUserAnswerText() {
            if (this.ex?.type === 'order') {
                return this.selectedOrder.join(' ');
            }

            return String(this.inputCode ?? '');
        },

        getCorrectAnswerText() {
            return this.correctAnswerText();
        },

        clearAiHint() {
            this.aiRequestId++;
            this.showHint = false;
            this.aiHint = '';
            this.aiLoading = false;
        },

        async getAIFeedback() {
            const requestId = ++this.aiRequestId;
            this.aiLoading = true;
            this.aiHint = '';

            try {
                const response = await fetch('/lesson/ai-feedback', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Accept': 'application/json',
                        'X-CSRF-TOKEN': document.querySelector('meta[name="csrf-token"]').content
                    },
                    body: JSON.stringify({
                        lesson_slug: this.lessonSlug,
                        question: this.ex.question,
                        user_answer: this.getUserAnswerText(),
                        correct_answer: this.getCorrectAnswerText(),
                        type: this.ex.type
                    })
                });

                const data = await response.json();
                if (requestId !== this.aiRequestId) return;
                this.aiHint = data.feedback || 'Draco detectó un error. Revisa el concepto principal e inténtalo otra vez.';
            } catch (e) {
                if (requestId !== this.aiRequestId) return;
                this.aiHint = 'Draco detectó un error. Revisa el concepto principal e inténtalo otra vez.';
            } finally {
                if (requestId === this.aiRequestId) {
                    this.aiLoading = false;
                }
            }
        },

        completeLesson() {
            if (this.syncing) return;

            this.syncing = true;
            this.$refs.completeForm.submit();
        }
    };
}
</script>
@endsection
