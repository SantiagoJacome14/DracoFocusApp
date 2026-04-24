package co.edu.unab.dracofocusapp.ui.Lecciones

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
            .padding(horizontal = 24.dp, vertical = 24.dp)
            .padding(bottom = 70.dp),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Elige tu modo de estudio",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- BOTÓN MODO SOLITARIO ---
            Button(
                onClick = { navController.navigate("lecciones_solitario") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF22DDF2),
                    contentColor = Color.Black
                )
            ) {
                Text("Modo Solitario", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- BOTÓN MODO GRUPAL ---
            Button(
                onClick = { navController.navigate("lecciones_grupales") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF22DDF2),
                    contentColor = Color.Black
                )
            ) {
                Text("Modo en Grupo", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- VOLVER al Home ---
            TextButton(
                onClick = { navController.navigate("draco") {
                    popUpTo("draco") { inclusive = false }
                }}
            ) {
                Text("Volver", color = Color.LightGray, fontSize = 16.sp)
            }
        }
    }
}
