<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class GroupSession extends Model
{
    protected $fillable = ['code', 'title', 'lesson_slug', 'created_by', 'status'];

    public function creator()
    {
        return $this->belongsTo(User::class, 'created_by');
    }

    public function members()
    {
        return $this->hasMany(GroupMember::class);
    }

    public function groupProgress()
    {
        return $this->hasMany(GroupProgress::class);
    }
}
