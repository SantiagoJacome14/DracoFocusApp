package co.edu.unab.dracofocusapp.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import co.edu.unab.dracofocusapp.R
import co.edu.unab.dracofocusapp.auth.AuthViewModel
import co.edu.unab.dracofocusapp.ui.Perfil.MyProfileScreen
import androidx.navigation.compose.currentBackStackEntryAsState
import co.edu.unab.dracofocusapp.ui.Avances.ProgressScreen
import co.edu.unab.dracofocusapp.ui.Draco.HomeScreen
import co.edu.unab.dracofocusapp.ui.Lecciones.Grupales.*
import co.edu.unab.dracofocusapp.ui.Lecciones.Solitario.*
import co.edu.unab.dracofocusapp.ui.Lecciones.MenuLeccionesScreen
import co.edu.unab.dracofocusapp.ui.Pomodoro.DracomodoroScreen
import co.edu.unab.dracofocusapp.ui.Pomodoro.CicloCompletadoScreen
import co.edu.unab.dracofocusapp.ui.MuseoDracoArteScreen
import co.edu.unab.dracofocusapp.navigation.AppRoutes
import co.edu.unab.dracofocusapp.ui.Lecciones.FeedbackScreen
import co.edu.unab.dracofocusapp.ui.Lecciones.LeccionRetoScreen
import co.edu.unab.dracofocusapp.viewmodel.FeedbackViewModel

sealed class BottomNavItem(val route: String, val icon: Int, val label: String) {
    object Lecciones : BottomNavItem("lecciones", R.drawable.ic_book, "Lecciones")
    object Pomodoro : BottomNavItem("pomodoro", R.drawable.ic_timer, "Estudiar")
    object Draco : BottomNavItem("draco", R.drawable.ic_home, "Draco")
    object Avances : BottomNavItem("avances", R.drawable.ic_calendar, "Avances")
    object Perfil : BottomNavItem("perfil", R.drawable.ic_user, "Perfil")
}

@Composable
fun MainScreen(
    onNavigateToAuth: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    authViewModel: AuthViewModel = viewModel()
) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            BottomNavGraph(
                navController = navController,
                onNavigateToAuth = onNavigateToAuth,
                onNavigateToProfile = onNavigateToProfile,
                authViewModel = authViewModel
            )
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val items = listOf(
        BottomNavItem.Lecciones,
        BottomNavItem.Pomodoro,
        BottomNavItem.Draco,
        BottomNavItem.Avances,
        BottomNavItem.Perfil
    )

    NavigationBar(
        containerColor = Color(0xFF0F1A2A),
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = { Text(item.label, fontSize = 10.sp, fontWeight = if(isSelected) FontWeight.Bold else FontWeight.Normal) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF22DDF2),
                    selectedTextColor = Color(0xFF22DDF2),
                    indicatorColor = Color(0xFF1C2541),
                    unselectedIconColor = Color(0xFF8FA3BD),
                    unselectedTextColor = Color(0xFF8FA3BD)
                )
            )
        }
    }
}

@Composable
fun BottomNavGraph(
    navController: NavHostController,
    onNavigateToAuth: () -> Unit,
    onNavigateToProfile: () -> Unit,
    authViewModel: AuthViewModel
) {
    NavHost(navController, startDestination = BottomNavItem.Draco.route) {
        composable(BottomNavItem.Draco.route) { HomeScreen(navController) }
        composable(BottomNavItem.Avances.route) { ProgressScreen(navController = navController) }
        composable(BottomNavItem.Perfil.route) { 
            MyProfileScreen(onBack = { navController.popBackStack() }, onLogout = onNavigateToAuth) 
        }
        composable(BottomNavItem.Pomodoro.route) { 
            DracomodoroScreen(navController = navController, onBack = { navController.popBackStack() }) 
        }
        composable(BottomNavItem.Lecciones.route) { MenuLeccionesScreen(navController = navController) }
        
        // Rutas adicionales
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
        composable(AppRoutes.CICLO_COMPLETADO) { CicloCompletadoScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoutes.MUSEO_DRACARTE) { MuseoDracoArteScreen(onBack = { navController.popBackStack() }) }

        // Lecciones (solitario) + feedback — retos unificados (puzzle / quiz / relleno)
        composable(AppRoutes.LECCION_DECISIONES_DE_FUEGO) {
            LeccionRetoScreen(
                navController = navController,
                lessonId = "decisiones_de_fuego",
                coopRoomId = null,
                onBack = { navController.popBackStack() },
            )
        }
        composable(AppRoutes.LECCION_VUELO_INFINITO) {
            LeccionRetoScreen(
                navController = navController,
                lessonId = "vuelo_infinito",
                coopRoomId = null,
                onBack = { navController.popBackStack() },
            )
        }
        composable(AppRoutes.LECCION_LIBRO_TAREAS) {
            LeccionRetoScreen(
                navController = navController,
                lessonId = "el_libro_de_tareas",
                coopRoomId = null,
                onBack = { navController.popBackStack() },
            )
        }
        composable(
            route = AppRoutes.LECCION_RETO,
            arguments = listOf(navArgument("lessonId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("lessonId") ?: return@composable
            LeccionRetoScreen(
                navController = navController,
                lessonId = id,
                coopRoomId = null,
                onBack = { navController.popBackStack() },
            )
        }
        composable(
            route = AppRoutes.LECCION_RETO_COOP,
            arguments = listOf(
                navArgument("lessonId") { type = NavType.StringType },
                navArgument("roomId") { type = NavType.StringType },
            ),
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("lessonId") ?: return@composable
            val room = backStackEntry.arguments?.getString("roomId") ?: return@composable
            LeccionRetoScreen(
                navController = navController,
                lessonId = id,
                coopRoomId = room,
                onBack = { navController.popBackStack() },
            )
        }
        composable(AppRoutes.FEEDBACK_SCREEN) {
            val feedbackViewModel: FeedbackViewModel = viewModel()
            FeedbackScreen(
                navController = navController,
                retroalimentacion = feedbackViewModel.retroalimentacion.value
            )
        }
    }
}
