<!DOCTYPE html>
<html lang="es" class="dark">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Crear Cuenta — Draco</title>
    <meta name="description" content="Crea tu cuenta en Draco y comienza a aprender a programar de forma gamificada.">
    @vite(['resources/css/app.css', 'resources/js/app.js'])
    <link href="https://fonts.googleapis.com/css2?family=Outfit:wght@300;400;500;600;700;800;900&display=swap" rel="stylesheet">
    <style>
        body { font-family: 'Outfit', sans-serif; }

        .auth-bg {
            background: #0f172a;
            position: relative;
            overflow: hidden;
        }
        .auth-bg::before {
            content: '';
            position: absolute;
            top: -40%;
            left: -20%;
            width: 80%;
            height: 80%;
            background: radial-gradient(circle, rgba(16, 185, 129, 0.08) 0%, transparent 70%);
            pointer-events: none;
        }
        .auth-bg::after {
            content: '';
            position: absolute;
            bottom: -30%;
            right: -15%;
            width: 60%;
            height: 60%;
            background: radial-gradient(circle, rgba(251, 191, 36, 0.05) 0%, transparent 70%);
            pointer-events: none;
        }

        .auth-card {
            background: linear-gradient(135deg, rgba(30, 41, 59, 0.9) 0%, rgba(15, 23, 42, 0.95) 100%);
            backdrop-filter: blur(40px);
            border: 1px solid rgba(51, 65, 85, 0.5);
            box-shadow:
                0 0 0 1px rgba(51, 65, 85, 0.2),
                0 25px 50px -12px rgba(0, 0, 0, 0.5),
                0 0 60px rgba(16, 185, 129, 0.05);
        }

        .input-field {
            background: rgba(15, 23, 42, 0.6);
            border: 1px solid rgba(51, 65, 85, 0.5);
            color: #e2e8f0;
            transition: all 0.3s ease;
        }
        .input-field:focus {
            border-color: #10b981;
            box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.15), 0 0 20px rgba(16, 185, 129, 0.1);
            outline: none;
        }
        .input-field::placeholder {
            color: #475569;
        }

        .btn-primary {
            background: linear-gradient(135deg, #10b981 0%, #059669 100%);
            box-shadow: 0 6px 0 0 #047857, 0 8px 20px rgba(16, 185, 129, 0.3);
            transition: all 0.15s ease;
        }
        .btn-primary:hover {
            filter: brightness(1.1);
            transform: translateY(-1px);
            box-shadow: 0 7px 0 0 #047857, 0 10px 25px rgba(16, 185, 129, 0.35);
        }
        .btn-primary:active {
            transform: translateY(4px);
            box-shadow: 0 2px 0 0 #047857, 0 4px 10px rgba(16, 185, 129, 0.2);
        }

        .floating-particle {
            position: absolute;
            border-radius: 50%;
            animation: float 6s ease-in-out infinite;
            opacity: 0.15;
        }

        @keyframes float {
            0%, 100% { transform: translateY(0px) rotate(0deg); }
            33% { transform: translateY(-15px) rotate(5deg); }
            66% { transform: translateY(10px) rotate(-3deg); }
        }

        @keyframes fadeInUp {
            from { opacity: 0; transform: translateY(30px); }
            to { opacity: 1; transform: translateY(0); }
        }
        .animate-fade-in-up {
            animation: fadeInUp 0.6s ease-out forwards;
        }

        @keyframes pulseGlow {
            0%, 100% { box-shadow: 0 0 20px rgba(16, 185, 129, 0.2); }
            50% { box-shadow: 0 0 40px rgba(16, 185, 129, 0.4); }
        }
        .pulse-glow {
            animation: pulseGlow 3s ease-in-out infinite;
        }

        .role-badge {
            background: linear-gradient(135deg, rgba(16, 185, 129, 0.15), rgba(16, 185, 129, 0.05));
            border: 1px solid rgba(16, 185, 129, 0.3);
        }
    </style>
</head>
<body class="auth-bg min-h-screen flex items-center justify-center p-4 text-white antialiased">

    <!-- Floating Particles -->
    <div class="floating-particle w-3 h-3 bg-emerald-400" style="top:15%; left:10%; animation-delay: 0s;"></div>
    <div class="floating-particle w-2 h-2 bg-amber-400" style="top:25%; right:15%; animation-delay: 1s;"></div>
    <div class="floating-particle w-4 h-4 bg-emerald-500" style="bottom:20%; left:20%; animation-delay: 2s;"></div>
    <div class="floating-particle w-2 h-2 bg-emerald-300" style="bottom:30%; right:25%; animation-delay: 3s;"></div>
    <div class="floating-particle w-3 h-3 bg-amber-300" style="top:60%; left:5%; animation-delay: 4s;"></div>

    <div class="w-full max-w-md animate-fade-in-up">
        <!-- Logo -->
        <div class="text-center mb-8">
            <div class="inline-flex items-center justify-center w-20 h-20 bg-gradient-to-br from-emerald-500 to-emerald-700 rounded-3xl shadow-2xl pulse-glow mb-4 overflow-hidden">
                <video autoplay loop muted playsinline class="w-full h-full object-cover">
                    <source src="{{ asset('DracoInicial.mp4') }}" type="video/mp4">
                </video>
            </div>
            <h1 class="text-3xl font-extrabold tracking-tight">
                <span class="bg-gradient-to-r from-emerald-400 to-emerald-300 bg-clip-text text-transparent">Draco</span>
            </h1>
            <p class="text-slate-400 text-sm mt-1 font-medium">Aprende a programar jugando</p>
        </div>

        <!-- Register Card -->
        <div class="auth-card rounded-3xl p-8">
            <div class="flex items-center justify-between mb-1">
                <h2 class="text-xl font-extrabold text-white">Crear Cuenta</h2>
                <div class="role-badge rounded-xl px-3 py-1.5 flex items-center gap-1.5">
                    <svg class="w-3.5 h-3.5 text-emerald-400" fill="currentColor" viewBox="0 0 24 24"><path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/></svg>
                    <span class="text-xs font-bold text-emerald-400 uppercase tracking-widest">Estudiante</span>
                </div>
            </div>
            <p class="text-slate-400 text-sm mb-6">Completa el formulario para unirte a Draco</p>

            @if($errors->any())
            <div class="bg-red-500/10 border border-red-500/30 rounded-2xl p-4 mb-6">
                @foreach($errors->all() as $error)
                <p class="text-red-400 text-sm font-semibold flex items-center gap-2 mb-1 last:mb-0">
                    <span>⚠️</span> {{ $error }}
                </p>
                @endforeach
            </div>
            @endif

            <form method="POST" action="{{ route('register') }}" id="register-form">
                @csrf

                <!-- Name -->
                <div class="mb-4">
                    <label for="name" class="block text-xs font-bold text-slate-400 uppercase tracking-widest mb-2">Nombre Completo</label>
                    <input type="text"
                           id="name"
                           name="name"
                           value="{{ old('name') }}"
                           required
                           autofocus
                           class="input-field w-full px-4 py-3.5 rounded-xl text-sm font-medium"
                           placeholder="Ej: Juan Pérez">
                </div>

                <!-- Email -->
                <div class="mb-4">
                    <label for="email" class="block text-xs font-bold text-slate-400 uppercase tracking-widest mb-2">Correo Electrónico</label>
                    <input type="email"
                           id="email"
                           name="email"
                           value="{{ old('email') }}"
                           required
                           class="input-field w-full px-4 py-3.5 rounded-xl text-sm font-medium"
                           placeholder="tu@correo.com">
                </div>

                <!-- Password -->
                <div class="mb-4">
                    <label for="password" class="block text-xs font-bold text-slate-400 uppercase tracking-widest mb-2">Contraseña</label>
                    <input type="password"
                           id="password"
                           name="password"
                           required
                           class="input-field w-full px-4 py-3.5 rounded-xl text-sm font-medium"
                           placeholder="Mínimo 6 caracteres">
                </div>

                <!-- Confirm Password -->
                <div class="mb-6">
                    <label for="password_confirmation" class="block text-xs font-bold text-slate-400 uppercase tracking-widest mb-2">Confirmar Contraseña</label>
                    <input type="password"
                           id="password_confirmation"
                           name="password_confirmation"
                           required
                           class="input-field w-full px-4 py-3.5 rounded-xl text-sm font-medium"
                           placeholder="Repite tu contraseña">
                </div>

                <!-- Info badge -->
                <div class="bg-emerald-500/5 border border-emerald-500/20 rounded-2xl p-3 mb-6 flex items-center gap-3">
                    <span class="text-xl">🎓</span>
                    <p class="text-xs text-slate-400 font-medium leading-relaxed">
                        Tu cuenta será creada con el rol de <strong class="text-emerald-400">Estudiante</strong>. ¡Comienza a aprender y gana XP!
                    </p>
                </div>

                <!-- Submit -->
                <button type="submit"
                        id="register-submit"
                        class="btn-primary w-full py-4 rounded-2xl text-lg font-extrabold text-white uppercase tracking-widest">
                    Crear Mi Cuenta
                </button>
            </form>

            <!-- Divider -->
            <div class="flex items-center gap-4 my-6">
                <div class="flex-1 h-px bg-slate-700"></div>
                <span class="text-xs text-slate-500 font-bold uppercase tracking-widest">o</span>
                <div class="flex-1 h-px bg-slate-700"></div>
            </div>

            <!-- Login Link -->
            <div class="text-center">
                <p class="text-sm text-slate-400">
                    ¿Ya tienes una cuenta?
                    <a href="{{ route('login') }}" id="login-link" class="text-emerald-400 font-bold hover:text-emerald-300 transition underline underline-offset-4">
                        Iniciar Sesión
                    </a>
                </p>
            </div>
        </div>

        <!-- Footer -->
        <p class="text-center text-xs text-slate-600 mt-6 font-medium">
            🐉 Draco — Plataforma de aprendizaje gamificada
        </p>
    </div>
</body>
</html>
