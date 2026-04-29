package co.edu.unab.dracofocusapp.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import co.edu.unab.dracofocusapp.auth.AuthScreen
import co.edu.unab.dracofocusapp.auth.ForgotPasswordScreen
import co.edu.unab.dracofocusapp.auth.RegisterScreen
import co.edu.unab.dracofocusapp.main.MainScreen
import co.edu.unab.dracofocusapp.ui.Lecciones.FeedbackScreen
import co.edu.unab.dracofocusapp.ui.Lecciones.Grupales.IngresarCodigoGrupoScreen
import co.edu.unab.dracofocusapp.ui.Lecciones.Grupales.LeccionesGrupalesScreen
import co.edu.unab.dracofocusapp.ui.Lecciones.MenuLeccionesScreen
import co.edu.unab.dracofocusapp.ui.Lecciones.RespuestaLeccionesScreen
import co.edu.unab.dracofocusapp.ui.Lecciones.Solitario.LeccionDecisionesDeFuegoScreen
import co.edu.unab.dracofocusapp.ui.Lecciones.Solitario.LeccionElLibroDeTareasScreen
import co.edu.unab.dracofocusapp.ui.Lecciones.Solitario.LeccionVueloInfinitoScreen
import co.edu.unab.dracofocusapp.ui.Lecciones.Solitario.LeccionesDracoSolitarioScreen
import co.edu.unab.dracofocusapp.ui.MuseoDracoArteScreen
import co.edu.unab.dracofocusapp.ui.Perfil.MyProfileScreen
import co.edu.unab.dracofocusapp.ui.Pomodoro.CicloCompletadoScreen
import co.edu.unab.dracofocusapp.ui.Pomodoro.DracomodoroScreen
import co.edu.unab.dracofocusapp.ui.SplashScreen
import co.edu.unab.dracofocusapp.viewmodel.FeedbackViewModel

object AppRoutes {
    const val SPLASH = "splash"
    const val AUTH = "auth"
    const val MAIN = "main"
    const val REGISTER = "register"
    const val FORGOT_PASSWORD = "forgot_password"
    const val PROFILE = "profile"
    const val DRACOMODORO = "dracomodoro"
    const val CICLO_COMPLETADO = "ciclo_completado"
    const val LECCION_DECISIONES_DE_FUEGO = "leccion_decisiones_de_fuego"
    const val LECCION_VUELO_INFINITO = "leccion_vuelo_infinito"
    const val LECCION_LIBRO_TAREAS = "leccion_libro_tareas"
    const val MUSEO_DRACARTE = "museo_dracarte"
    const val LECCIONES_SOLO = "lecciones_solitario"
    const val LECCIONES_GRUPALES = "lecciones_grupales"
    const val RESPUESTA_LECCION_S = "respuesta_leccion_s"
    const val MENU_LECCIONES = "menu_lecciones"
    const val FEEDBACK_SCREEN = "feedback_screen"
    const val INGRESAR_CODIGO_GRUPO = "ingresar_codigo_grupo/{leccionId}"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppRoutes.SPLASH
    ) {
        composable(AppRoutes.SPLASH) {
            SplashScreen(navController = navController)
        }

        composable(AppRoutes.AUTH) {
            AuthScreen(
                onNavigateToMain = {
                    navController.navigate(AppRoutes.MAIN) {
                        popUpTo(AppRoutes.AUTH) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToForgotPassword = { navController.navigate(AppRoutes.FORGOT_PASSWORD) },
                onNavigateToRegister = { navController.navigate(AppRoutes.REGISTER) }
            )
        }

        composable(AppRoutes.REGISTER) {
            RegisterScreen(
                onBack = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate(AppRoutes.MAIN) {
                        popUpTo(AppRoutes.AUTH) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(AppRoutes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(onBack = { navController.popBackStack() })
        }

        composable(AppRoutes.MAIN) {
            MainScreen(
                onNavigateToAuth = {
                    navController.navigate(AppRoutes.AUTH) {
                        popUpTo(AppRoutes.MAIN) { inclusive = true }
                    }
                },
                onNavigateToProfile = { navController.navigate(AppRoutes.PROFILE) }
            )
        }

        composable(AppRoutes.DRACOMODORO) {
            DracomodoroScreen(
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppRoutes.CICLO_COMPLETADO) {
            CicloCompletadoScreen(onBack = { navController.popBackStack() })
        }

        composable(AppRoutes.MENU_LECCIONES) {
            MenuLeccionesScreen(navController = navController)
        }

        composable(AppRoutes.LECCION_DECISIONES_DE_FUEGO) {
            LeccionDecisionesDeFuegoScreen(
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppRoutes.LECCION_VUELO_INFINITO) {
            LeccionVueloInfinitoScreen(
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppRoutes.LECCION_LIBRO_TAREAS) {
            LeccionElLibroDeTareasScreen(
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppRoutes.LECCIONES_SOLO) {
            LeccionesDracoSolitarioScreen(
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppRoutes.LECCIONES_GRUPALES) {
            LeccionesGrupalesScreen(
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppRoutes.RESPUESTA_LECCION_S) {
            RespuestaLeccionesScreen(onBack = { navController.popBackStack() })
        }

        composable(AppRoutes.FEEDBACK_SCREEN) {
            val feedbackViewModel: FeedbackViewModel = viewModel()
            FeedbackScreen(
                navController = navController,
                retroalimentacion = feedbackViewModel.retroalimentacion.value
            )
        }

        composable(AppRoutes.MUSEO_DRACARTE) {
            MuseoDracoArteScreen(onBack = { navController.popBackStack() })
        }

        composable(
            route = AppRoutes.INGRESAR_CODIGO_GRUPO,
            arguments = listOf(navArgument("leccionId") { type = NavType.StringType })
        ) { backStackEntry ->
            IngresarCodigoGrupoScreen(
                navController = navController,
                backStackEntry = backStackEntry,
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppRoutes.PROFILE) {
            MyProfileScreen(
                onBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(AppRoutes.AUTH) {
                        popUpTo(AppRoutes.MAIN) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
