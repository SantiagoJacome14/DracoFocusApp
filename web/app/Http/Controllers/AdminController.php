<?php

namespace App\Http\Controllers;

use App\Models\User;
use Illuminate\Http\Request;

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
}
