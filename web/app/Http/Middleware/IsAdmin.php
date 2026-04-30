<?php

namespace App\Http\Middleware;

use Closure;
use Illuminate\Http\Request;
use Symfony\Component\HttpFoundation\Response;

class IsAdmin
{
    /**
     * Handle an incoming request.
     * Only users with is_admin = true can access admin routes.
     *
     * @param  Closure(Request): (Response)  $next
     */
    public function handle(Request $request, Closure $next): Response
    {
        // Check authenticated user's is_admin flag
        if (!auth()->check() || !auth()->user()->is_admin) {
            return redirect()->route('dashboard')
                ->with('error', 'No tienes permisos para acceder a esa área.');
        }

        return $next($request);
    }
}
