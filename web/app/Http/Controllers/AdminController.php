<?php

namespace App\Http\Controllers;

use App\Models\User;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;
use Illuminate\Validation\Rules\Password;

class AdminController extends Controller
{
    public function users()
    {
        $users = User::with('progress')->paginate(10);
        return view('admin.users.index', compact('users'));
    }

    public function impersonate(User $user)
    {
        // Store the original admin id so we can switch back if needed (optional advanced feature)
        session()->put('impersonated_by', auth()->id());
        
        // Log in as the selected user
        auth()->login($user);

        return redirect()->route('dashboard')->with('success', "Has iniciado sesión como {$user->name}.");
    }

    public function leaveImpersonation()
    {
        if (session()->has('impersonated_by')) {
            $adminId = session()->pull('impersonated_by');
            auth()->loginUsingId($adminId);
            return redirect()->route('admin.users')->with('success', 'Has vuelto a tu cuenta de Administrador.');
        }

        return redirect()->route('dashboard');
    }

    public function destroyUser(User $user)
    {
        $user->delete();
        return redirect()->route('admin.users')->with('success', 'Usuario eliminado correctamente.');
    }

    public function teachers()
{
    $teachers = User::where('role', 'teacher')
        ->latest()
        ->paginate(10);

    return view('admin.teachers.index', compact('teachers'));
}

    public function createTeacher()
    {
        return view('admin.teachers.create');
    }

    public function storeTeacher(Request $request)
    {
        $request->validate([
            'name' => ['required', 'string', 'max:255'],
            'email' => ['required', 'email', 'max:255', 'unique:users,email'],
            'password' => ['required', 'confirmed', Password::defaults()],
        ]);

        User::create([
            'name' => $request->name,
            'email' => $request->email,
            'password' => Hash::make($request->password),
            'role' => 'teacher',
            'is_admin' => false,
            'daily_goal' => 50,
            'current_streak' => 0,
            'total_xp' => 0,
        ]);

        return redirect()
            ->route('admin.teachers.index')
            ->with('success', 'Profesor creado correctamente.');
    }

    public function editTeacher(User $user)
    {
        if ($user->role !== 'teacher') {
            abort(404);
        }

        return view('admin.teachers.edit', compact('user'));
    }

    public function updateTeacher(Request $request, User $user)
    {
        if ($user->role !== 'teacher') {
            abort(404);
        }

        $request->validate([
            'name' => ['required', 'string', 'max:255'],
            'email' => ['required', 'email', 'max:255', 'unique:users,email,' . $user->id],
            'password' => ['nullable', 'confirmed', Password::defaults()],
        ]);

        $user->name = $request->name;
        $user->email = $request->email;

        if ($request->filled('password')) {
            $user->password = Hash::make($request->password);
        }

        $user->role = 'teacher';
        $user->is_admin = false;
        $user->save();

        return redirect()
            ->route('admin.teachers.index')
            ->with('success', 'Profesor actualizado correctamente.');
    }

    public function destroyTeacher(User $user)
    {
        if ($user->role !== 'teacher') {
            abort(404);
        }

        $user->delete();

return redirect()
    ->route('admin.teachers.index')
    ->with('success', 'Profesor creado correctamente.');
    }
}
