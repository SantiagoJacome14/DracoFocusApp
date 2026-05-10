<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class UserMuseumReward extends Model
{
    protected $fillable = ['user_id', 'lesson_slug', 'piece_catalog_id'];

    public function user()
    {
        return $this->belongsTo(User::class);
    }
}
