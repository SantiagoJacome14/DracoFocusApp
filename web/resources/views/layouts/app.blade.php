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
            padding: 1.75rem 1.5rem;
            border-bottom: 1px solid rgba(51, 65, 85, 0.4);
            display: flex;
            align-items: center;
            gap: 0.75rem;
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

        /* ── Main Content — desktop ── */
        .draco-main {
            margin-left: 260px;
            min-height: 100vh;
            background: #0f172a;
        }

        /* ── Mobile: hide sidebar, remove margin ── */
        @media (max-width: 767px) {
            .draco-sidebar {
                transform: translateX(-100%);
            }
            .draco-sidebar.open {
                transform: translateX(0);
            }
            .draco-main {
                margin-left: 0;
            }
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
    </style>
</head>
<body class="bg-slate-900 text-white antialiased min-h-screen"
      x-data="{ sidebarOpen: false }"
      @keydown.escape.window="sidebarOpen = false">

    <!-- ── Mobile overlay ── -->
    <div x-show="sidebarOpen"
         x-transition.opacity.duration.200ms
         @click="sidebarOpen = false"
         class="fixed inset-0 bg-slate-900/70 backdrop-blur-sm z-30 md:hidden"
         style="display: none;"></div>

    <!-- ── Sidebar Navigation ── -->
    <aside class="draco-sidebar" :class="{ 'open': sidebarOpen }">
        <div class="sidebar-logo">
            <div class="logo-icon">🐉</div>
            <h1>Draco</h1>
            <!-- Close button (mobile only) -->
            <button @click="sidebarOpen = false"
                    class="ml-auto text-slate-500 hover:text-white transition md:hidden"
                    aria-label="Cerrar menú">
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12"/>
                </svg>
            </button>
        </div>

        <nav>
            <div class="nav-section-title" style="margin-top: 0.5rem;">Principal</div>

            <a href="{{ route('dashboard') }}" class="nav-link {{ request()->routeIs('dashboard') ? 'active' : '' }}">
                <svg class="nav-icon" fill="currentColor" viewBox="0 0 24 24"><path d="M12 2l10 9h-3v11h-4v-7h-6v7h-4v-11h-3z"/></svg>
                Dashboard
            </a>

            <a href="{{ route('profile') }}" class="nav-link {{ request()->routeIs('profile') ? 'active' : '' }}">
                <svg class="nav-icon" fill="currentColor" viewBox="0 0 24 24"><path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/></svg>
                Mi Perfil
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

        <!-- Mobile top bar with hamburger -->
        <div class="md:hidden flex items-center justify-between px-4 py-3 bg-slate-900/95 border-b border-slate-700/50 sticky top-0 z-20 backdrop-blur-md">
            <div class="flex items-center gap-2">
                <div class="w-8 h-8 bg-gradient-to-br from-emerald-500 to-emerald-700 rounded-xl flex items-center justify-center text-sm">🐉</div>
                <span class="text-base font-extrabold bg-gradient-to-r from-emerald-400 to-emerald-300 bg-clip-text text-transparent">Draco</span>
            </div>
            <button @click="sidebarOpen = true"
                    class="p-2 rounded-xl text-slate-400 hover:text-white hover:bg-slate-700/60 transition"
                    aria-label="Abrir menú">
                <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M4 6h16M4 12h16M4 18h16"/>
                </svg>
            </button>
        </div>

        @if(session()->has('impersonated_by'))
        <div class="bg-amber-500/20 border-b border-amber-500/40 px-4 sm:px-6 py-2 flex items-center justify-between text-amber-200 text-sm font-semibold sticky top-0 z-50 backdrop-blur-md">
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
