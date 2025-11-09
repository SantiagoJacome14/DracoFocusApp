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
import androidx.navigation.compose.*
import co.edu.unab.dracofocusapp.R
import co.edu.unab.dracofocusapp.auth.AuthViewModel
import co.edu.unab.dracofocusapp.ui.MyProfileScreen
import androidx.navigation.compose.currentBackStackEntryAsState
import co.edu.unab.dracofocusapp.ui.ProgressScreen
import co.edu.unab.dracofocusapp.ui.HomeScreen
import co.edu.unab.dracofocusapp.ui.LeccionDecisionesDeFuegoScreen
import co.edu.unab.dracofocusapp.ui.LeccionElLibroDeTareasScreen
import co.edu.unab.dracofocusapp.ui.LeccionVueloInfinitoScreen
import co.edu.unab.dracofocusapp.ui.LeccionesDracoSolitarioScreen
import co.edu.unab.dracofocusapp.ui.MenuLeccionesScreen


// ---------------------- RUTAS DEL MENÚ INFERIOR ----------------------
sealed class BottomNavItem(val route: String, val icon: Int, val label: String) {
    object Lecciones : BottomNavItem("lecciones", R.drawable.ic_book, "Lecciones")
    object Pomodoro : BottomNavItem("pomodoro", R.drawable.ic_timer, "Pomodoro")
    object Draco : BottomNavItem("draco", R.drawable.ic_home, "Draco")
    object Avances : BottomNavItem("avances", R.drawable.ic_calendar, "Avances")
    object Perfil : BottomNavItem("perfil", R.drawable.ic_user, "Perfil")


}

// ---------------------- PANTALLA PRINCIPAL CON NAVBAR ----------------------
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

// ---------------------- BARRA DE NAVEGACIÓN INFERIOR ----------------------
data class NavItem(val route: String, val icon: Int, val label: String)

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        NavItem(BottomNavItem.Lecciones.route, R.drawable.ic_book, "Lecciones"),
        NavItem(BottomNavItem.Pomodoro.route, R.drawable.ic_timer, "Estudiar"),
        NavItem(BottomNavItem.Draco.route, R.drawable.ic_home, "Draco"),
        NavItem(BottomNavItem.Avances.route, R.drawable.ic_calendar, "Avances"),
        NavItem(BottomNavItem.Perfil.route, R.drawable.ic_user, "Perfil")
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color(0xFF0F1A2A), // Fondo consistente con el app
        tonalElevation = 0.dp
    ) {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(item.label, fontSize = 12.sp)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = Color.White,
                    indicatorColor = Color(0xFF22DDF2), // Resaltado neón
                    unselectedIconColor = Color(0xFF8FA3BD),
                    unselectedTextColor = Color(0xFF8FA3BD)
                )
            )
        }
    }
}

// ---------------------- GESTIÓN DE PANTALLAS ----------------------
@Composable
fun BottomNavGraph(
    navController: NavHostController,
    onNavigateToAuth: () -> Unit,
    onNavigateToProfile: () -> Unit,
    authViewModel: AuthViewModel
) {
    NavHost(navController, startDestination = BottomNavItem.Draco.route) {

        // ---------- Pestañas del menú inferior ----------
        composable(BottomNavItem.Draco.route) {
            HomeScreen(
                onNavigateToStudy = { },
                onNavigateToLessons = { navController.navigate("menu_lecciones") },
                onNavigateToMuseum = { },
                onNavigateToProgress = { navController.navigate(BottomNavItem.Avances.route) }
            )
        }

        composable(BottomNavItem.Avances.route) { ProgressScreen(navController) }
        composable(BottomNavItem.Perfil.route) { MyProfileScreen() }

        // ---------- Pantalla de elección de modo ----------
        composable("menu_lecciones") {
            MenuLeccionesScreen(navController = navController)
        }
        composable("lecciones_solitario"){LeccionesDracoSolitarioScreen(
            navController = navController,
            onBack = { navController.popBackStack() }
        )}

        composable("leccion_decisiones_fuego") {
            LeccionDecisionesDeFuegoScreen(
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }

        composable("leccion_vuelo_infinito") {
            LeccionVueloInfinitoScreen(
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }

        composable("leccion_libro_tareas") {
            LeccionElLibroDeTareasScreen(
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }


    }
}

// ---------------------- PANTALLA “DRACO” (Bienvenida) ----------------------
@Composable
fun DracoWelcomeScreen(
    onNavigateToAuth: () -> Unit,
    onNavigateToProfile: () -> Unit,
    authViewModel: AuthViewModel
) {
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1F1C2C),
            Color(0xFF3A1C71),
            Color(0xFF602A8A)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradientBrush)
            .padding(horizontal = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "¡Acabas de entrar a DracoFocus!",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Tu viaje para mejorar tu concentración comienza ahora.",
                color = Color(0xFFE3E3E3),
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = onNavigateToProfile,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF9D4EDD),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .height(50.dp)
                    .width(200.dp)
            ) {
                Text("Ver mi perfil 👤", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))

        }
    }
}

