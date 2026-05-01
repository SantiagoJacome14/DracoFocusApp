<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Achievement extends Model
{
    protected $fillable = ['title', 'description', 'icon', 'condition'];

    public function users()
    {
        return $this->hasMany(UserAchievement::class);
    }
}
