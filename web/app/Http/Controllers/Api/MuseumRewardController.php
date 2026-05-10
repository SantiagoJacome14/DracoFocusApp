<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\UserMuseumReward;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;

class MuseumRewardController extends Controller
{
    /**
     * All piece IDs that exist in the Android MuseumCatalog.
     * Must stay in sync with MuseumCatalog.kt.
     */
    private const CATALOG_PIECES = [
        'm_dracoegipto',
        'm_dracofiesta',
        'm_dracoluna',
        'm_dracomuseoverona',
        'm_dracoskate',
        'm_dracoversalles',
    ];

    /**
     * GET /api/museum/rewards
     * Returns all museum pieces unlocked for the authenticated user.
     */
    public function index()
    {
        $rewards = UserMuseumReward::where('user_id', Auth::id())->get();

        return response()->json([
            'rewards' => $rewards->map(fn($r) => [
                'lesson_slug'     => $r->lesson_slug,
                'piece_catalog_id' => $r->piece_catalog_id,
                'unlocked_at'     => $r->created_at?->toIso8601String(),
            ])->values(),
        ]);
    }

    /**
     * POST /api/museum/rewards/claim
     * Claims a museum reward for a completed lesson.
     *
     * Idempotent: if the lesson already has a reward for this user,
     * returns the same piece (status: "existing").
     * If all pieces are unlocked, returns status: "collection_complete".
     * Otherwise picks a random available piece (status: "new").
     */
    public function claim(Request $request)
    {
        $request->validate([
            'lesson_slug' => ['required', 'string', 'exists:lessons,slug'],
        ]);

        $userId    = Auth::id();
        $lessonSlug = $request->lesson_slug;

        // Idempotent: return existing reward for this lesson
        $existing = UserMuseumReward::where('user_id', $userId)
            ->where('lesson_slug', $lessonSlug)
            ->first();

        if ($existing) {
            return response()->json([
                'status'           => 'existing',
                'piece_catalog_id' => $existing->piece_catalog_id,
                'unlocked_at'      => $existing->created_at?->toIso8601String(),
            ]);
        }

        // Find which pieces this user has NOT yet unlocked
        $unlockedIds = UserMuseumReward::where('user_id', $userId)
            ->pluck('piece_catalog_id')
            ->toArray();

        $available = array_values(array_diff(self::CATALOG_PIECES, $unlockedIds));

        if (empty($available)) {
            return response()->json(['status' => 'collection_complete']);
        }

        // Pick a random available piece and persist
        $pieceId = $available[array_rand($available)];

        $reward = UserMuseumReward::create([
            'user_id'         => $userId,
            'lesson_slug'     => $lessonSlug,
            'piece_catalog_id' => $pieceId,
        ]);

        return response()->json([
            'status'           => 'new',
            'piece_catalog_id' => $reward->piece_catalog_id,
            'unlocked_at'      => $reward->created_at?->toIso8601String(),
        ]);
    }
}
