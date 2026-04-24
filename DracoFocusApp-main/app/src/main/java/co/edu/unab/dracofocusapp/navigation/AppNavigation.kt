package co.edu.unab.dracofocusapp.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import co.edu.unab.dracofocusapp.auth.AuthScreen
import co.edu.unab.dracofocusapp.auth.ForgotPasswordScreen
import co.edu.unab.dracofocusapp.auth.RegisterScreen
import co.edu.unab.dracofocusapp.main.MainScreen
import co.edu.unab.dracofocusapp.ui.Pomodoro.DracomodoroScreen
import co.edu.unab.dracofocusapp.ui.Lecciones.Solitario.LeccionDecisionesDeFuegoScreen
import co.edu.unab.dracofocusapp.ui.Lecciones.Solitario.LeccionesDracoSolitarioScreen
import co.edu.unab.dracofocusapp.ui.Lecciones.Grupales.LeccionesGrupalesScreen
import co.edu.unab.dracofocusapp.ui.Perfil.MyProfileScreen
import co.edu.unab.dracofocusapp.theme.AppColorScheme
import co.edu.unab.dracofocusapp.ui.Lecciones.RespuestaLeccionesScreen
import co.edu.unab.dracofocusapp.ui.Lecciones.FeedbackScreen
import co.edu.unab.dracofocusapp.ui.Lecciones.Grupales.IngresarCodigoGrupoScreen
import co.edu.unab.dracofocusapp.ui.Lecciones.MenuLeccionesScreen
import co.edu.unab.dracofocusapp.viewmodel.FeedbackViewModel
import co.edu.unab.dracofocusapp.ui.SplashScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    MaterialTheme(colorScheme = AppColorScheme) {
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(500)
                ) + fadeIn(animationSpec = tween(500))
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(500)
                ) + fadeOut(animationSpec = tween(500))
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(500)
                ) + fadeIn(animationSpec = tween(500))
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(500)
                ) + fadeOut(animationSpec = tween(500))
            }
        ) {
            // Splash & Auth
            composable(Screen.Splash.route) {
                SplashScreen(navController = navController)
            }

            composable(Screen.Auth.route) {
                AuthScreen(
                    onNavigateToMain = {
                        navController.navigate(Screen.Main.route) {
                            popUpTo(Screen.Auth.route) { inclusive = true }
                        }
                    },
                    onNavigateToForgotPassword = {
                        navController.navigate(Screen.ForgotPassword.route)
                    },
                    onNavigateToRegister = {
                        navController.navigate(Screen.Register.route)
                    }
                )
            }

            composable(Screen.Register.route) {
                RegisterScreen(
                    onBackToLogin = { navController.popBackStack() },
                    onRegisterSuccess = {
                        navController.navigate(Screen.Main.route) {
                            popUpTo(Screen.Auth.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.ForgotPassword.route) {
                ForgotPasswordScreen(onBackToLogin = { navController.popBackStack() })
            }

            // Flujo Principal
            composable(Screen.Main.route) {
                MainScreen(
                    onNavigateToAuth = {
                        navController.navigate(Screen.Auth.route) {
                            popUpTo(Screen.Main.route) { inclusive = true }
                        }
                    },
                    onNavigateToProfile = {
                        navController.navigate(Screen.Profile.route)
                    }
                )
            }

            composable(Screen.Profile.route) {
                MyProfileScreen(
                    onBackToMain = { navController.popBackStack() },
                    onNavigateToAuth = {
                        navController.navigate(Screen.Auth.route) {
                            popUpTo(Screen.Main.route) { inclusive = true }
                        }
                    }
                )
            }

            // Herramientas y Lecciones
            composable(Screen.Dracomodoro.route) {
                DracomodoroScreen(onBack = { navController.popBackStack() })
            }

            composable(Screen.MenuLecciones.route) {
                MenuLeccionesScreen(navController = navController)
            }

            composable(Screen.LeccionesSolo.route) {
                LeccionesDracoSolitarioScreen(
                    navController = navController,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.LeccionDecisionesDeFuego.route) {
                LeccionDecisionesDeFuegoScreen(
                    navController = navController,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.LeccionesGrupales.route) {
                LeccionesGrupalesScreen(
                    navController = navController,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.RespuestaLeccion.route) {
                RespuestaLeccionesScreen(onBack = { navController.popBackStack() })
            }

            composable(Screen.Feedback.route) {
                val feedbackViewModel: FeedbackViewModel = viewModel()
                FeedbackScreen(
                    navController = navController,
                    retroalimentacion = feedbackViewModel.retroalimentacion.value
                )
            }

            composable(Screen.IngresarCodigo.route) { backStackEntry ->
                IngresarCodigoGrupoScreen(
                    navController = navController,
                    backStackEntry = backStackEntry,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Museo.route) {
                // TODO: Implementar pantalla de Museo
            }
        }
    }
}
