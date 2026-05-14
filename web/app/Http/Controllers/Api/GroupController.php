<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\GroupMember;
use App\Models\GroupProgress;
use App\Models\GroupSession;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;

class GroupController extends Controller
{
    /**
     * POST /api/groups
     * Creates a new group session. Creator is automatically the leader.
     */
    public function create(Request $request)
    {
        $request->validate([
            'title'       => ['required', 'string', 'max:100'],
            'lesson_slug' => ['nullable', 'string', 'exists:lessons,slug'],
        ]);

        $code = $this->generateUniqueCode();

        $session = GroupSession::create([
            'code'        => $code,
            'title'       => $request->title,
            'lesson_slug' => $request->lesson_slug,
            'created_by'  => Auth::id(),
            'status'      => 'waiting',
        ]);

        GroupMember::create([
            'group_session_id' => $session->id,
            'user_id'          => Auth::id(),
            'role'             => 'leader',
            'joined_at'        => now(),
        ]);

        return response()->json([
            'code'        => $session->code,
            'title'       => $session->title,
            'lesson_slug' => $session->lesson_slug,
            'status'      => $session->status,
            'my_role'     => 'leader',
            'members'     => [[
                'user_id'   => Auth::id(),
                'name'      => Auth::user()->name,
                'role'      => 'leader',
                'joined_at' => now()->toIso8601String(),
            ]],
        ], 201);
    }

    /**
     * POST /api/groups/join
     * Joins an existing group by code. Assigns role based on join order.
     */
    public function join(Request $request)
    {
        $request->validate([
            'code' => ['required', 'string'],
        ]);

        $session = GroupSession::where('code', strtoupper(trim($request->code)))->first();

        if (! $session) {
            return response()->json(['error' => 'Código de grupo no encontrado.'], 404);
        }

        if ($session->status === 'completed') {
            return response()->json(['error' => 'Este grupo ya terminó su actividad.'], 422);
        }

        $userId = Auth::id();

        // Already a member — return current state
        $existing = GroupMember::where('group_session_id', $session->id)
            ->where('user_id', $userId)->first();

        if (! $existing) {
            $memberCount = GroupMember::where('group_session_id', $session->id)->count();
            $role = match (true) {
                $memberCount === 0 => 'leader',
                $memberCount === 1 => 'programmer',
                $memberCount === 2 => 'analyst',
                default            => 'student',
            };

            GroupMember::create([
                'group_session_id' => $session->id,
                'user_id'          => $userId,
                'role'             => $role,
                'joined_at'        => now(),
            ]);
        }

        return response()->json($this->sessionPayload($session, $userId));
    }

    /**
     * GET /api/groups/{code}
     * Returns group info + member list. User must be authenticated.
     */
    public function show(string $code)
    {
        $session = GroupSession::where('code', strtoupper($code))
            ->with('members.user:id,name,email')
            ->first();

        if (! $session) {
            return response()->json(['error' => 'Grupo no encontrado.'], 404);
        }

        return response()->json($this->sessionPayload($session, Auth::id()));
    }

    /**
     * GET /api/groups/{code}/members
     * Returns the member list of a group (lightweight).
     */
    public function members(string $code)
    {
        $session = GroupSession::where('code', strtoupper($code))
            ->with('members.user:id,name,email')
            ->first();

        if (! $session) {
            return response()->json(['error' => 'Grupo no encontrado.'], 404);
        }

        return response()->json([
            'members' => $this->formatMembers($session->members),
        ]);
    }

    /**
     * POST /api/groups/{code}/start
     * Sets status to 'active'. Only the leader can call this.
     */
    public function start(string $code)
    {
        $session = GroupSession::where('code', strtoupper($code))->first();

        if (! $session) {
            return response()->json(['error' => 'Grupo no encontrado.'], 404);
        }

        $member = GroupMember::where('group_session_id', $session->id)
            ->where('user_id', Auth::id())->first();

        if (! $member || $member->role !== 'leader') {
            return response()->json(['error' => 'Solo el líder puede iniciar la actividad.'], 403);
        }

        $session->update(['status' => 'active']);

        return response()->json(['code' => $session->code, 'status' => 'active']);
    }

    /**
     * POST /api/groups/{code}/progress
     * Saves individual progress within a group session.
     */
    public function saveProgress(Request $request, string $code)
    {
        $request->validate([
            'lesson_slug'      => ['required', 'string'],
            'current_exercise' => ['nullable', 'integer', 'min:0'],
            'completed'        => ['nullable', 'boolean'],
            'score'            => ['nullable', 'integer', 'min:0', 'max:100'],
        ]);

        $session = GroupSession::where('code', strtoupper($code))->first();

        if (! $session) {
            return response()->json(['error' => 'Grupo no encontrado.'], 404);
        }

        $progress = GroupProgress::updateOrCreate(
            [
                'group_session_id' => $session->id,
                'user_id'          => Auth::id(),
                'lesson_slug'      => $request->lesson_slug,
            ],
            [
                'current_exercise' => $request->current_exercise ?? 0,
                'completed'        => $request->completed ?? false,
                'score'            => $request->score,
                'completed_at'     => $request->completed ? now() : null,
            ]
        );

        return response()->json(['saved' => true, 'progress_id' => $progress->id]);
    }

    /**
     * POST /api/groups/{code}/complete
     * Marks the session as completed and optionally awards XP to all members.
     */
    public function complete(string $code)
    {
        $session = GroupSession::where('code', strtoupper($code))
            ->with('members')
            ->first();

        if (! $session) {
            return response()->json(['error' => 'Grupo no encontrado.'], 404);
        }

        if ($session->status === 'completed') {
            return response()->json(['status' => 'completed', 'xp_earned' => 0]);
        }

        $session->update(['status' => 'completed']);

        return response()->json(['status' => 'completed']);
    }

    /**
     * POST /api/groups/{code}/role
     * Allows the authenticated user to choose their role (analyst | programmer).
     *
     * Rules:
     * - Group must exist.
     * - User must be a member.
     * - Group must not be completed.
     * - Role must be "analyst" or "programmer".
     * - No other member in the group may already hold that role.
     */
    public function setRole(Request $request, string $code)
    {
        $request->validate([
            'role' => ['required', 'string', 'in:analyst,programmer'],
        ]);

        $session = GroupSession::where('code', strtoupper($code))->first();

        if (! $session) {
            return response()->json(['error' => 'Grupo no encontrado.'], 404);
        }

        if ($session->status === 'completed') {
            return response()->json(['error' => 'Este grupo ya terminó su actividad.'], 422);
        }

        $userId = Auth::id();

        // User must already be a member
        $myMember = GroupMember::where('group_session_id', $session->id)
            ->where('user_id', $userId)
            ->first();

        if (! $myMember) {
            return response()->json(['error' => 'No eres miembro de este grupo.'], 403);
        }

        $desiredRole = $request->role; // "analyst" or "programmer"

        // Check if another member (not me) already holds this role
        $roleTaken = GroupMember::where('group_session_id', $session->id)
            ->where('user_id', '!=', $userId)
            ->where('role', $desiredRole)
            ->exists();

        if ($roleTaken) {
            $label = $desiredRole === 'analyst' ? 'Analista' : 'Programador';
            return response()->json([
                'error' => "El rol de {$label} ya fue tomado por otro estudiante.",
            ], 422);
        }

        // Save the role
        $myMember->update(['role' => $desiredRole]);

        return response()->json($this->sessionPayload($session->fresh(), $userId));
    }

    // ─── helpers ───────────────────────────────────────────────────────────────

    private function sessionPayload(GroupSession $session, int $userId): array
    {
        $session->loadMissing('members.user:id,name,email');
        $myMember = $session->members->firstWhere('user_id', $userId);

        return [
            'code'        => $session->code,
            'title'       => $session->title,
            'lesson_slug' => $session->lesson_slug,
            'status'      => $session->status,
            'my_role'     => $myMember?->role,
            'members'     => $this->formatMembers($session->members),
        ];
    }

    private function formatMembers($members): array
    {
        return $members->map(function ($m) {
            $joinedAt = null;

            if ($m->joined_at) {
                try {
                    $joinedAt = \Carbon\Carbon::parse($m->joined_at)->toIso8601String();
                } catch (\Throwable $e) {
                    $joinedAt = null;
                }
            }

            return [
                'user_id'   => $m->user_id,
                'name'      => $m->user?->name ?? 'Usuario',
                'role'      => $m->role,
                'joined_at' => $joinedAt ?? now()->toIso8601String(),
            ];
        })->values()->toArray();
    }

    private function generateUniqueCode(): string
    {
        $chars = 'ABCDEFGHJKLMNPQRSTUVWXYZ23456789';
        do {
            $code = '';
            for ($i = 0; $i < 6; $i++) {
                $code .= $chars[random_int(0, strlen($chars) - 1)];
            }
        } while (GroupSession::where('code', $code)->exists());

        return $code;
    }
}
