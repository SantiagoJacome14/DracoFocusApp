package co.edu.unab.dracofocusapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import co.edu.unab.dracofocusapp.navigation.AppNavigation
import co.edu.unab.dracofocusapp.theme.AppColorScheme
import co.edu.unab.dracofocusapp.ui.LeccionDecisionesDeFuegoScreen
import co.edu.unab.dracofocusapp.ui.LeccionesGrupalesScreen
import co.edu.unab.dracofocusapp.ui.LeccionesDracoSolitarioScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = AppColorScheme) {
                Surface {
                    val navController = rememberNavController()

                    LeccionesGrupalesScreen(
                        navController = navController,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}



