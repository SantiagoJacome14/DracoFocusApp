<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::table('users', function (Blueprint $table) {
            $table->text('bio')->nullable()->after('avatar');
            $table->string('specialty')->nullable()->after('bio');
            $table->string('location')->nullable()->after('specialty');
            $table->string('github_url')->nullable()->after('location');
            $table->string('linkedin_url')->nullable()->after('github_url');
            $table->string('website_url')->nullable()->after('linkedin_url');
        });
    }

    public function down(): void
    {
        Schema::table('users', function (Blueprint $table) {
            $table->dropColumn(['bio', 'specialty', 'location', 'github_url', 'linkedin_url', 'website_url']);
        });
    }
};
