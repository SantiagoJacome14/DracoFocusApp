<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;

class Exercise extends Model
{
    protected $fillable = [
        'lesson_id',
        'type',
        'question',
        'data',
        'language',
        'hint',
        'explanation',
        'xp_reward',
        'difficulty',
        'is_active',
        'sort_order',
    ];

    protected $casts = [
        'data'      => 'array',
        'is_active' => 'boolean',
        'xp_reward' => 'integer',
    ];

    public function lesson(): BelongsTo
    {
        return $this->belongsTo(Lesson::class);
    }
}
