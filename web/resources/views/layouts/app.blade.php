<!DOCTYPE html>
<html lang="es" class="dark">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="csrf-token" content="{{ csrf_token() }}">
    <title>Draco - @yield('title', 'Aprende a programar')</title>
    @vite(['resources/css/app.css', 'resources/js/app.js'])
    <!-- Alpine.js -->
    <script defer src="https://cdn.jsdelivr.net/npm/alpinejs@3.x.x/dist/cdn.min.js"></script>
    <link href="https://fonts.googleapis.com/css2?family=Outfit:wght@300;400;500;600;700;800;900&display=swap" rel="stylesheet">
    <style>
        body { font-family: 'Outfit', sans-serif; }

        /* ── Sidebar ── */
        .draco-sidebar {
            width: 260px;
            min-height: 100vh;
            background: linear-gradient(180deg, #0f172a 0%, #1a1f35 100%);
            border-right: 1px solid rgba(51, 65, 85, 0.6);
            position: fixed;
            left: 0;
            top: 0;
            z-index: 40;
            display: flex;
            flex-direction: column;
            transition: transform 0.3s ease;
        }
        .draco-sidebar .sidebar-logo {
            padding: 1.25rem 1rem 1rem 1rem;
            border-bottom: none;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .draco-sidebar .draco-video-frame {
            width: 115px;
            height: 115px;
            overflow: hidden;
            border-radius: 24px;
            background: radial-gradient(circle, rgba(16, 185, 129, 0.12), transparent 65%);
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .draco-sidebar .draco-video-frame video {
            width: 118px;
            height: 118px;
            object-fit: contain;
            border: none;
            outline: none;
            display: block;
            background: transparent;
            transform: scale(1.06) translateY(2px);
        }
        .draco-sidebar .sidebar-logo .logo-icon {
            width: 44px;
            height: 44px;
            background: linear-gradient(135deg, #10b981 0%, #059669 100%);
            border-radius: 14px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 1.5rem;
            box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
        }
        .draco-sidebar .sidebar-logo h1 {
            font-size: 1.5rem;
            font-weight: 800;
            background: linear-gradient(135deg, #10b981, #34d399);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            letter-spacing: -0.025em;
        }
        .draco-sidebar nav {
            flex: 1;
            padding: 1.5rem 0.75rem;
        }
        .draco-sidebar .nav-section-title {
            font-size: 0.65rem;
            font-weight: 700;
            text-transform: uppercase;
            letter-spacing: 0.1em;
            color: #475569;
            padding: 0 0.75rem;
            margin-bottom: 0.5rem;
        }
        .draco-sidebar .nav-link {
            display: flex;
            align-items: center;
            gap: 0.75rem;
            padding: 0.75rem 1rem;
            border-radius: 12px;
            color: #94a3b8;
            font-weight: 600;
            font-size: 0.9rem;
            transition: all 0.2s ease;
            margin-bottom: 4px;
            text-decoration: none;
            position: relative;
        }
        .draco-sidebar .nav-link:hover {
            background: rgba(16, 185, 129, 0.08);
            color: #e2e8f0;
        }
        .draco-sidebar .nav-link.active {
            background: linear-gradient(135deg, rgba(16, 185, 129, 0.15), rgba(16, 185, 129, 0.05));
            color: #34d399;
            box-shadow: inset 3px 0 0 #10b981;
        }
        .draco-sidebar .nav-link .nav-icon {
            width: 22px;
            height: 22px;
            flex-shrink: 0;
        }
        .draco-sidebar .sidebar-footer {
            padding: 1rem 1.25rem;
            border-top: 1px solid rgba(51, 65, 85, 0.4);
        }

        /* ── Main Content ── */
        .draco-main {
            margin-left: 260px;
            min-height: 100vh;
            background: #0f172a;
        }

        /* ── Ambient glow effect ── */
        .glow-emerald {
            box-shadow: 0 0 30px rgba(16, 185, 129, 0.15), 0 0 60px rgba(16, 185, 129, 0.05);
        }
        .glow-gold {
            box-shadow: 0 0 20px rgba(251, 191, 36, 0.2);
        }

        /* ── Card glass effect ── */
        .glass-card {
            background: linear-gradient(135deg, rgba(30, 41, 59, 0.8) 0%, rgba(30, 41, 59, 0.4) 100%);
            backdrop-filter: blur(20px);
            border: 1px solid rgba(51, 65, 85, 0.6);
        }

        /* ── Animations ── */
        @keyframes fadeInUp {
            from { opacity: 0; transform: translateY(20px); }
            to { opacity: 1; transform: translateY(0); }
        }
        .animate-fade-in-up {
            animation: fadeInUp 0.5s ease-out forwards;
        }
        .animate-delay-1 { animation-delay: 0.1s; }
        .animate-delay-2 { animation-delay: 0.2s; }
        .animate-delay-3 { animation-delay: 0.3s; }
        .animate-delay-4 { animation-delay: 0.4s; }

        @keyframes pulseGlow {
            0%, 100% { box-shadow: 0 0 20px rgba(16, 185, 129, 0.2); }
            50% { box-shadow: 0 0 40px rgba(16, 185, 129, 0.4); }
        }
        .pulse-glow {
            animation: pulseGlow 2s ease-in-out infinite;
        }

        /* ── Scrollbar styling ── */
        ::-webkit-scrollbar { width: 6px; }
        ::-webkit-scrollbar-track { background: #0f172a; }
        ::-webkit-scrollbar-thumb { background: #334155; border-radius: 3px; }
        ::-webkit-scrollbar-thumb:hover { background: #475569; }

        /* ── Stripe pattern for progress bars ── */
        .bg-stripe-pattern {
            background-image: repeating-linear-gradient(
                45deg,
                transparent,
                transparent 6px,
                rgba(255,255,255,0.05) 6px,
                rgba(255,255,255,0.05) 12px
            );
        }

        /* ── Gamified Sidebar Enhancements ── */
        .level-card {
            background: linear-gradient(135deg, rgba(16, 185, 129, 0.15) 0%, rgba(139, 92, 246, 0.15) 100%);
            border: 1px solid rgba(139, 92, 246, 0.3);
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2), inset 0 0 15px rgba(139, 92, 246, 0.1);
        }
        
        .nav-link:hover {
            background: rgba(139, 92, 246, 0.08);
            color: #c4b5fd;
            transform: translateX(4px);
        }

        .nav-link.active {
            background: linear-gradient(135deg, rgba(16, 185, 129, 0.15), rgba(139, 92, 246, 0.15));
            color: #34d399;
            box-shadow: inset 3px 0 0 #10b981;
            border-right: 1px solid rgba(139, 92, 246, 0.2);
        }
    </style>
</head>
<body class="bg-slate-900 text-white antialiased min-h-screen">

    <!-- ── Sidebar Navigation ── -->
    <aside class="draco-sidebar">
        <div class="sidebar-logo">
            <div class="draco-video-frame">
                <video autoplay loop muted playsinline>
                    <source src="{{ asset('DracoInicial.mp4') }}" type="video/mp4">
                </video>
            </div>
        </div>

        <!-- Gamified Level & XP Card -->
        <div class="level-card p-4 mx-3 my-4 rounded-xl text-center text-white glass-card">
            <div class="text-xs font-bold uppercase text-slate-300 mb-1 tracking-wider">Nivel <span class="text-white text-lg ml-1">{{ auth()->user()->level ?? 1 }}</span></div>
            <div class="flex items-center justify-between text-xs font-semibold text-slate-300 mb-2">
                <span>XP</span>
                <span>{{ auth()->user()->xp ?? 0 }} / {{ auth()->user()->next_level_xp ?? 1000 }}</span>
            </div>
            <div class="w-full h-2 bg-slate-700/50 rounded-full overflow-hidden border border-slate-600/50 relative">
                <div class="h-full bg-gradient-to-r from-emerald-400 to-emerald-600 bg-stripe-pattern absolute left-0 top-0" style="width: {{ min((auth()->user()->xp ?? 0) / (auth()->user()->next_level_xp ?? 1000) * 100, 100) }}%"></div>
            </div>
        </div>

        <nav>
            <div class="nav-section-title" style="margin-top: 0.5rem;">Principal</div>

            <a href="{{ route('dashboard') }}" class="nav-link {{ request()->routeIs('dashboard') ? 'active' : '' }}">
                <svg class="nav-icon" fill="currentColor" viewBox="0 0 24 24"><path d="M12 2l10 9h-3v11h-4v-7h-6v7h-4v-11h-3z"/></svg>
                Dashboard
            </a>

            <a href="#" class="nav-link {{ request()->routeIs('lessons.*') ? 'active' : '' }}">
                <svg class="nav-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" /></svg>
                Lecciones
            </a>

            <a href="#" class="nav-link {{ request()->routeIs('progress.*') ? 'active' : '' }}">
                <svg class="nav-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6" /></svg>
                Avances
            </a>

            <a href="{{ route('profile') }}" class="nav-link {{ request()->routeIs('profile') ? 'active' : '' }}">
                <svg class="nav-icon" fill="currentColor" viewBox="0 0 24 24"><path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/></svg>
                Mi Perfil
            </a>

            <a href="#" class="nav-link {{ request()->routeIs('settings.*') ? 'active' : '' }}">
                <svg class="nav-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z" /><path stroke-linecap="round" stroke-linejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" /></svg>
                Configuración
            </a>

            @if(auth()->user() && auth()->user()->isAdmin())
            <div class="nav-section-title" style="margin-top: 1.5rem;">Administración</div>
            <a href="{{ route('admin.users') }}" class="nav-link {{ request()->routeIs('admin.*') ? 'active' : '' }}">
                <svg class="nav-icon" fill="currentColor" viewBox="0 0 24 24"><path d="M12 1l3.09 6.26L22 8.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01z"/></svg>
                Panel Admin
            </a>
            @endif

            @if(auth()->user() && auth()->user()->isTeacher())
            <div class="nav-section-title" style="margin-top: 1.5rem;">Docencia</div>
            <a href="{{ route('teacher.dashboard') }}" class="nav-link {{ request()->routeIs('teacher.*') ? 'active' : '' }}">
                <svg class="nav-icon" fill="currentColor" viewBox="0 0 24 24"><path d="M5 13.18v4L12 21l7-3.82v-4L12 17l-7-3.82zM12 3L1 9l11 6 9-4.91V17h2V9L12 3z"/></svg>
                Panel Profesor
            </a>
            @endif
        </nav>

        <div class="sidebar-footer">
            <!-- User Info -->
            <div class="flex items-center gap-3 px-2 py-2 mb-3">
                <div class="w-9 h-9 bg-gradient-to-br from-emerald-500 to-emerald-700 rounded-xl flex items-center justify-center text-sm font-black text-white flex-shrink-0">
                    {{ strtoupper(substr(auth()->user()->name, 0, 1)) }}
                </div>
                <div class="flex-1 min-w-0">
                    <p class="text-sm font-bold text-slate-200 truncate">{{ auth()->user()->name }}</p>
                    <p class="text-xs text-slate-500 font-medium truncate">
                        @if(auth()->user()->isAdmin()) Admin
                        @elseif(auth()->user()->isTeacher()) Profesor
                        @else Estudiante @endif
                    </p>
                </div>
            </div>

            <!-- Logout Button -->
            <form method="POST" action="{{ route('logout') }}">
                @csrf
                <button type="submit" class="w-full flex items-center gap-2 px-3 py-2.5 rounded-xl text-sm font-semibold text-slate-400 hover:text-red-400 hover:bg-red-500/10 transition-all">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2">
                        <path stroke-linecap="round" stroke-linejoin="round" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"/>
                    </svg>
                    Cerrar Sesión
                </button>
            </form>
        </div>
    </aside>

    <!-- ── Main Content ── -->
    <main class="draco-main relative">
        @if(session()->has('impersonated_by'))
        <div class="bg-amber-500/20 border-b border-amber-500/40 px-6 py-2 flex items-center justify-between text-amber-200 text-sm font-semibold sticky top-0 z-50 backdrop-blur-md">
            <div class="flex items-center gap-2">
                <span>🕵️‍♂️</span> Estás navegando como <strong>{{ auth()->user()->name }}</strong>.
            </div>
            <form action="{{ route('impersonate.leave') }}" method="POST">
                @csrf
                <button type="submit" class="bg-amber-500/20 hover:bg-amber-500/40 text-amber-100 px-3 py-1 rounded-lg transition border border-amber-500/30 text-xs font-bold uppercase tracking-widest">
                    Volver a mi cuenta Admin
                </button>
            </form>
        </div>
        @endif

        @yield('content')
    </main>

    <!-- Toast Notification using Alpine -->
    @if(session('success'))
        <div x-data="{ show: true }" x-init="setTimeout(() => show = false, 3000)" x-show="show" 
             x-transition.opacity.duration.500ms
             class="fixed bottom-6 right-6 bg-gradient-to-r from-draco-emerald to-draco-emerald-light text-slate-900 px-6 py-3 rounded-2xl font-bold shadow-2xl z-50 flex items-center gap-2">
            <span>✅</span> {{ session('success') }}
        </div>
    @endif
</body>
</html>
