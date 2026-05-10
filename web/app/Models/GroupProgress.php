<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class GroupProgress extends Model
{
    protected $fillable = [
        'group_session_id', 'user_id', 'lesson_slug',
        'current_exercise', 'completed', 'score', 'xp_earned', 'completed_at',
    ];

    protected $casts = [
        'completed'    => 'boolean',
        'completed_at' => 'datetime',
    ];

    public function user()
    {
        return $this->belongsTo(User::class);
    }

    public function session()
    {
        return $this->belongsTo(GroupSession::class, 'group_session_id');
    }
}
