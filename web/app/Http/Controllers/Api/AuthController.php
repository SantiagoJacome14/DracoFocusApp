<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\User;
use App\Models\UserProgress;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\Http;
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
        try {
            $request->validate([
                'name' => ['required', 'string', 'max:255'],
                'email' => ['required', 'string', 'email', 'max:255', 'unique:users'],
                'password' => ['required', 'string', 'min:6', 'confirmed'],
                'semester' => ['nullable', 'string', 'max:10'],
            ]);

            $user = User::create([
                'name' => $request->name,
                'email' => $request->email,
                'password' => Hash::make($request->password),
                'semester' => $request->semester,
            ]);

            $token = $user->createToken('auth_token')->plainTextToken;

            return response()->json([
                'access_token' => $token,
                'token_type' => 'Bearer',
                'user' => $user,
            ], 201);
        } catch (\Exception $e) {
            return response()->json([
                'error' => true,
                'message' => $e->getMessage()
            ], 500);
        }
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
                        'role' => 'estudiante',
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
     * Get the authenticated user data, including today's XP progress.
     */
    public function me(Request $request)
    {
        $user = $request->user();

        $dailyXp = (int) UserProgress::where('user_id', $user->id)
            ->where('completed', true)
            ->whereDate('completed_at', now()->toDateString())
            ->with('lesson:id,xp_reward')
            ->get()
            ->sum(fn($p) => $p->lesson?->xp_reward ?? 0);

        return response()->json(array_merge($user->toArray(), ['daily_progress_xp' => $dailyXp]));
    }

    /**
     * Update the authenticated user's editable profile fields.
     */
    public function updateProfile(Request $request)
    {
        $request->validate([
            'bio' => ['nullable', 'string', 'max:280'],
            'specialty' => ['nullable', 'string', 'max:100'],
            'location' => ['nullable', 'string', 'max:100'],
            'github_url' => ['nullable', 'string', 'max:255'],
            'linkedin_url' => ['nullable', 'string', 'max:255'],
            'website_url' => ['nullable', 'string', 'max:255'],
        ]);

        $user = $request->user();
        $user->update($request->only([
            'bio', 'specialty', 'location', 'github_url', 'linkedin_url', 'website_url',
        ]));

        return response()->json($user->fresh());
    }

    /**
     * Upload a new profile photo to Cloudinary and save its URL as avatar.
     */
    public function uploadAvatar(Request $request)
    {
        $request->validate([
            'photo' => ['required', 'image', 'max:5120'],
        ]);

        $cloudName = env('CLOUDINARY_CLOUD_NAME');
        $apiKey = env('CLOUDINARY_API_KEY');
        $apiSecret = env('CLOUDINARY_API_SECRET');

        if (!$cloudName || !$apiKey || !$apiSecret) {
            return response()->json(['message' => 'Cloudinary no está configurado en el servidor.'], 500);
        }

        $timestamp = time();
        $folder = 'dracofocus/avatars';
        $paramsToSign = "folder={$folder}&timestamp={$timestamp}";
        $signature = sha1($paramsToSign . $apiSecret);

        $photo = $request->file('photo');

        $response = Http::attach(
            'file',
            file_get_contents($photo->getRealPath()),
            $photo->getClientOriginalName()
        )->post("https://api.cloudinary.com/v1_1/{$cloudName}/image/upload", [
            'api_key' => $apiKey,
            'timestamp' => $timestamp,
            'signature' => $signature,
            'folder' => $folder,
        ]);

        if (!$response->successful()) {
            return response()->json([
                'message' => 'Error al subir la imagen a Cloudinary',
                'error' => $response->json('error.message') ?? $response->body(),
            ], 502);
        }

        $user = $request->user();
        $user->avatar = $response->json('secure_url');
        $user->save();

        return response()->json($user->fresh());
    }
}
