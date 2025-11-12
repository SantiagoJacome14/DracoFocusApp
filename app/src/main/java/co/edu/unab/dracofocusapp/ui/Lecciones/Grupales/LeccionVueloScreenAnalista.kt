package co.edu.unab.dracofocusapp.ui.Lecciones.Grupales

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import co.edu.unab.dracofocusapp.auth.ModernTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeccionVueloScreenAnalista(
    navController: NavController,
    onComplete: () -> Unit = {},
    onBack: () -> Unit = { navController.popBackStack() }
) {
    val gradientBackground = Brush.verticalGradient(
        listOf(Color(0xFF0B132B), Color(0xFF1C2541))
    )

    Scaffold(
        topBar = {
            ModernTopBar(
                title = "Misión de Vuelo",
                showBackButton = true,
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { inner ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBackground)
                .padding(inner)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
                Text("Aprenderás ciclos, listas y contadores.", color = Color.White, fontSize = 18.sp)

                Button(
                    onClick = {
                        onComplete()
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF22DDF2),
                        contentColor = Color.Black
                    )
                ) {
                    Text("Marcar como completada")
                }
            }
        }
    }
}
