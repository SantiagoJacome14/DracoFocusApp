<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\User;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Str;
use Illuminate\Validation\ValidationException;
use Google_Client;

class AuthController extends Controller
{
    /**
     * Handle user registration.
     */
    public function register(Request $request)
    {
        $request->validate([
            'name' => ['required', 'string', 'max:255'],
            'email' => ['required', 'string', 'email', 'max:255', 'unique:users'],
            'password' => ['required', 'string', 'min:6', 'confirmed'],
            'semester' => ['required', 'string', 'max:10'],
        ]);

        $user = User::create([
            'name' => $request->name,
            'email' => $request->email,
            'password' => Hash::make($request->password),
            'semester' => $request->semester,
            'is_admin' => false,
            'daily_goal' => 50,
            'current_streak' => 0,
            'total_xp' => 0,
        ]);

        $token = $user->createToken('auth_token')->plainTextToken;

        return response()->json([
            'access_token' => $token,
            'token_type' => 'Bearer',
            'user' => $user,
        ], 201);
    }

    /**
     * Handle user login.
     */
    public function login(Request $request)
    {
        $request->validate([
            'email' => ['required', 'email'],
            'password' => ['required'],
        ]);

        $user = User::where('email', $request->email)->first();

        if (! $user || ! Hash::check($request->password, $user->password)) {
            throw ValidationException::withMessages([
                'email' => ['Las credenciales proporcionadas son incorrectas.'],
            ]);
        }

        $token = $user->createToken('auth_token')->plainTextToken;

        return response()->json([
            'access_token' => $token,
            'token_type' => 'Bearer',
            'user' => $user,
        ]);
    }

    

    /**
     * Handle login with Google.
     */
    public function loginWithGoogle(Request $request)
    {
        $request->validate([
            'id_token' => ['required', 'string'],
        ]);

        try {
            $client = new \Google_Client(['client_id' => env('GOOGLE_WEB_CLIENT_ID')]);
            $payload = $client->verifyIdToken($request->id_token);

            if ($payload) {
                $googleId = $payload['sub'];
                $email = $payload['email'];
                $name = $payload['name'];
                $picture = $payload['picture'] ?? null;

                $user = User::where('google_id', $googleId)->orWhere('email', $email)->first();

                if (! $user) {
                    $user = User::create([
                        'name' => $name,
                        'email' => $email,
                        'password' => Hash::make(\Illuminate\Support\Str::random(24)),
                        'is_admin' => false,
                        'daily_goal' => 50,
                        'current_streak' => 0,
                        'total_xp' => 0,
                        'google_id' => $googleId,
                        'avatar' => $picture,
                    ]);
                } else {
                    $changed = false;
                    if (! $user->google_id) {
                        $user->google_id = $googleId;
                        $changed = true;
                    }
                    if (! $user->avatar && $picture) {
                        $user->avatar = $picture;
                        $changed = true;
                    }
                    if ($changed) {
                        $user->save();
                    }
                }

                $token = $user->createToken('android-app')->plainTextToken;

                return response()->json([
                    'message' => 'Inicio de sesión con Google exitoso',
                    'user' => $user,
                    'access_token' => $token,
                    'token_type' => 'Bearer'
                ]);
            } else {
                return response()->json(['message' => 'Token de Google inválido'], 401);
            }
        } catch (\Exception $e) {
            return response()->json(['message' => 'Error al autenticar con Google', 'error' => $e->getMessage()], 500);
        }
    }

    /**
     * Log the user out (revoke token).
     */
    public function logout(Request $request)
    {
        $request->user()->currentAccessToken()->delete();

        return response()->json([
            'message' => 'Sesión cerrada correctamente.'
        ]);
    }

    /**
     * Get the authenticated user data.
     */
    public function me(Request $request)
    {
        return response()->json($request->user());
    }
}
