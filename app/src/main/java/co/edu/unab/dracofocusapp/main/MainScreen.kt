package co.edu.unab.dracofocusapp.main

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
import androidx.lifecycle.viewmodel.compose.viewModel
import co.edu.unab.dracofocusapp.auth.AuthViewModel


@Composable
fun MainScreen(
    // para navegar de vuelta a la auth
    onNavigateToAuth: () -> Unit = {},
    // para poder llamar a la función signOut
    authViewModel: AuthViewModel = viewModel()
) { //fondo transparente
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF2E1A47), // morado oscuro
            Color(0xFF3A256A), // violeta medio
            Color(0xFF4A3C8C)  // violeta claro
        )
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradientBrush)
            .padding(horizontal = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {

            // Texto temporal
            Text(
                text = "¡Bienvenido a DracoFocus!",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))
            //Texto temporal
            Text(
                text = "Has iniciado sesión correctamente.",
                color = Color(0xFFE0E0E0),
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(48.dp))

            // Botón para Cerrar Sesión
            Button(
                onClick = {
                    // llama a la funcionpara cerrar la sesión en Firebase
                    authViewModel.signOut()

                    // ejecuta la navegacion para volver a la pantalla de Login o Registro
                    onNavigateToAuth()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8A4FFF),
                    contentColor = Color.White
                ),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .height(50.dp)
                    .width(200.dp)
            ) {
                Text(
                    text = "Cerrar Sesión",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
// Commit estético y documentado: mejoras de diseño y validaciones
