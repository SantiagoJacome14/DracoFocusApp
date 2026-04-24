package co.edu.unab.dracofocusapp.ui.Lecciones.Grupales

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavBackStackEntry
import kotlinx.coroutines.delay
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.ui.draw.alpha
import androidx.compose.foundation.shape.RoundedCornerShape
import co.edu.unab.dracofocusapp.auth.ModernTopBar

@Composable
fun IngresarCodigoGrupoScreen(
    navController: NavController,
    backStackEntry: NavBackStackEntry,
    onBack: () -> Unit = {}
) {
    // Recupera el nombre de la lección desde la ruta
    val leccionId = backStackEntry.arguments?.getString("leccionId") ?: "guardianes_tesoro"

    var codigo by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }
    var mostrandoAnimacion by remember { mutableStateOf(false) }
    var mensajeRol by remember { mutableStateOf("") }
    var destinoRol by remember { mutableStateOf("") }

    val gradient = Brush.verticalGradient(
        listOf(Color(0xFF0B132B), Color(0xFF1C2541))
    )

    val alphaAnim by animateFloatAsState(
        targetValue = if (mostrandoAnimacion) 1f else 0f,
        animationSpec = tween(durationMillis = 800)
    )

    Scaffold(
        topBar = {
            ModernTopBar(
                title = "Conexión en Grupo",
                showBackButton = true,
                onBackClick = onBack
            )
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(innerPadding)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {

            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                Text(
                    "Ingresa el código de tu compañero para comenzar la lección",
                    color = Color(0xFFB3B3B3),
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = codigo,
                    onValueChange = {
                        codigo = it
                        error = false
                    },
                    label = { Text("Código del grupo") },
                    isError = error,
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color(0xFF22DDF2),
                        unfocusedIndicatorColor = Color.Gray,
                        cursorColor = Color(0xFF22DDF2)
                    )
                )

                if (error) {
                    Text(
                        "Código inválido, inténtalo de nuevo.",
                        color = Color(0xFFFF6B6B),
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        val limpio = codigo.trim()
                        if (limpio.length >= 4 && limpio.last().isDigit()) {
                            val ultimo = limpio.last().digitToInt()

                            mostrandoAnimacion = true

                            // Determinar el mensaje y destino
                            if (ultimo % 2 == 0) {
                                mensajeRol = "Draco te ha asignado el rol de Analista..."
                                destinoRol = when (leccionId) {
                                    "guardianes_tesoro" -> "leccion_guardianes_analista"
                                    "mision_vuelo" -> "leccion_vuelo_analista"
                                    "reto_acertijos" -> "leccion_acertijos_analista"
                                    else -> "lecciones_grupales"
                                }
                            } else {
                                mensajeRol = "Draco te ha asignado el rol de Programador..."
                                destinoRol = when (leccionId) {
                                    "guardianes_tesoro" -> "leccion_guardianes_programador"
                                    "mision_vuelo" -> "leccion_vuelo_programador"
                                    "reto_acertijos" -> "leccion_acertijos_programador"
                                    else -> "lecciones_grupales"
                                }
                            }
                        } else {
                            error = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF22DDF2),
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.fillMaxWidth(0.7f)
                ) {
                    Text("Validar Código", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // 🔹 Animación tipo overlay
            if (mostrandoAnimacion) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xAA000000))
                        .alpha(alphaAnim),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Color(0xFF22DDF2))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = mensajeRol,
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                LaunchedEffect(Unit) {
                    delay(2200)
                    mostrandoAnimacion = false
                    navController.navigate(destinoRol)
                }
            }
        }
    }
}
