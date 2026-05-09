<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Lesson extends Model
{
    protected $fillable = ['title', 'slug', 'description', 'content', 'difficulty', 'type', 'xp_reward', 'order'];
    protected $casts = [
    'content' => 'array',
    'exercises' => 'array',
];

    public function progress()
    {
        return $this->hasMany(UserProgress::class);
    }

    public function questions()
    {
        return $this->hasMany(Question::class);
    }
}
