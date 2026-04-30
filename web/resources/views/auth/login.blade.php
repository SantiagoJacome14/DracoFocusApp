<!DOCTYPE html>
<html lang="es" class="dark">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Iniciar Sesión — Draco</title>
    <meta name="description" content="Inicia sesión en Draco, tu plataforma gamificada para aprender a programar.">
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

        <!-- Login Card -->
        <div class="auth-card rounded-3xl p-8">
            <h2 class="text-xl font-extrabold text-white mb-1">Iniciar Sesión</h2>
            <p class="text-slate-400 text-sm mb-6">Ingresa tus credenciales para continuar</p>

            @if($errors->any())
            <div class="bg-red-500/10 border border-red-500/30 rounded-2xl p-4 mb-6">
                @foreach($errors->all() as $error)
                <p class="text-red-400 text-sm font-semibold flex items-center gap-2">
                    <span>⚠️</span> {{ $error }}
                </p>
                @endforeach
            </div>
            @endif

            <form method="POST" action="{{ route('login') }}" id="login-form">
                @csrf

                <!-- Email -->
                <div class="mb-5">
                    <label for="email" class="block text-xs font-bold text-slate-400 uppercase tracking-widest mb-2">Correo Electrónico</label>
                    <input type="email"
                           id="email"
                           name="email"
                           value="{{ old('email') }}"
                           required
                           autofocus
                           class="input-field w-full px-4 py-3.5 rounded-xl text-sm font-medium"
                           placeholder="tu@correo.com">
                </div>

                <!-- Password -->
                <div class="mb-5">
                    <label for="password" class="block text-xs font-bold text-slate-400 uppercase tracking-widest mb-2">Contraseña</label>
                    <input type="password"
                           id="password"
                           name="password"
                           required
                           class="input-field w-full px-4 py-3.5 rounded-xl text-sm font-medium"
                           placeholder="••••••••">
                </div>

                <!-- Remember Me -->
                <div class="flex items-center justify-between mb-6">
                    <label class="flex items-center gap-2 cursor-pointer group">
                        <input type="checkbox" name="remember" id="remember"
                               class="w-4 h-4 rounded bg-slate-800 border-slate-600 text-emerald-500 focus:ring-emerald-500/30 focus:ring-offset-0 cursor-pointer">
                        <span class="text-sm text-slate-400 font-medium group-hover:text-slate-300 transition">Recordarme</span>
                    </label>
                </div>

                <!-- Submit -->
                <button type="submit"
                        id="login-submit"
                        class="btn-primary w-full py-4 rounded-2xl text-lg font-extrabold text-white uppercase tracking-widest">
                    Iniciar Sesión
                </button>
            </form>

            <!-- Divider -->
            <div class="flex items-center gap-4 my-6">
                <div class="flex-1 h-px bg-slate-700"></div>
                <span class="text-xs text-slate-500 font-bold uppercase tracking-widest">o</span>
                <div class="flex-1 h-px bg-slate-700"></div>
            </div>

            <!-- Register Link -->
            <div class="text-center">
                <p class="text-sm text-slate-400">
                    ¿No tienes una cuenta?
                    <a href="{{ route('register') }}" id="register-link" class="text-emerald-400 font-bold hover:text-emerald-300 transition underline underline-offset-4">
                        Crear Cuenta
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
