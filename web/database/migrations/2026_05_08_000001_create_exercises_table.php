<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('exercises', function (Blueprint $table) {
            $table->id();
            $table->foreignId('lesson_id')->constrained()->cascadeOnDelete();
            $table->string('type', 20);
            $table->text('question');
            $table->jsonb('data');
            $table->string('language', 20)->default('javascript');
            $table->text('hint')->nullable();
            $table->text('explanation')->nullable();
            $table->smallInteger('xp_reward')->default(0);
            $table->string('difficulty', 20)->default('beginner');
            $table->boolean('is_active')->default(true);
            $table->integer('sort_order')->default(0);
            $table->timestamps();

            $table->index(['lesson_id', 'language', 'is_active'], 'idx_exercises_lesson_lang_active');
            $table->index(['lesson_id', 'difficulty'], 'idx_exercises_lesson_difficulty');
        });

        // text[] is a PostgreSQL-native type; Blueprint does not support array defaults directly
        DB::statement("ALTER TABLE exercises ADD COLUMN tags text[] NOT NULL DEFAULT '{}'");
    }

    public function down(): void
    {
        Schema::dropIfExists('exercises');
    }
};
