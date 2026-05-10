<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('group_progress', function (Blueprint $table) {
            $table->id();
            $table->foreignId('group_session_id')->constrained()->onDelete('cascade');
            $table->foreignId('user_id')->constrained()->onDelete('cascade');
            $table->string('lesson_slug');
            $table->integer('current_exercise')->default(0);
            $table->boolean('completed')->default(false);
            $table->integer('score')->nullable();
            $table->integer('xp_earned')->default(0);
            $table->timestamp('completed_at')->nullable();
            $table->timestamps();

            $table->unique(['group_session_id', 'user_id', 'lesson_slug']);
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('group_progress');
    }
};
