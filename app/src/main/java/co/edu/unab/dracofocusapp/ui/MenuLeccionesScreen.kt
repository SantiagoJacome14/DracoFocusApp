package co.edu.unab.dracofocusapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun MenuLeccionesScreen(navController: NavController) {

    val gradientBackground = Brush.verticalGradient(
        listOf(Color(0xFF0B132B), Color(0xFF1C2541))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBackground)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Elige tu modo de estudio",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { navController.navigate("lecciones_solitario") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF22DDF2),
                    contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                Text("Modo DracoSolitario", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate("lecciones_grupales") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF22DDF2),
                    contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                Text("Modo en Grupo", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(28.dp))

            TextButton(
                onClick = {
                    navController.navigate("draco") {
                        popUpTo("menu_lecciones") { inclusive = true }
                    }
                }
            ) {
                Text("Volver", color = Color.LightGray, fontSize = 16.sp)
            }

        }
    }
}
