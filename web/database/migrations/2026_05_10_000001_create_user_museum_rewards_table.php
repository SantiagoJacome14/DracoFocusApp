<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('user_museum_rewards', function (Blueprint $table) {
            $table->id();
            $table->foreignId('user_id')->constrained()->onDelete('cascade');
            $table->string('lesson_slug')->nullable();
            $table->string('piece_catalog_id');
            $table->timestamps();

            // A lesson grants at most one piece per user
            $table->unique(['user_id', 'lesson_slug']);
            // A piece can only be unlocked once per user
            $table->unique(['user_id', 'piece_catalog_id']);
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('user_museum_rewards');
    }
};
