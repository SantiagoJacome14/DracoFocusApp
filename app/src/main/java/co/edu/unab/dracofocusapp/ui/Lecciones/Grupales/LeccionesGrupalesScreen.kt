package co.edu.unab.dracofocusapp.ui.Lecciones.Grupales

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.material3.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import co.edu.unab.dracofocusapp.R
import androidx.compose.foundation.Image
import co.edu.unab.dracofocusapp.auth.ModernTopBar
import androidx.compose.ui.res.painterResource


@Composable
fun ModernLessonCard(
    icon: Int? = null,
    titulo: String,
    subtitulo: String,
    isCompleted: Boolean,
    onStart: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF22DDF2), RoundedCornerShape(16.dp))
            .background(Color(0xFF0F1A2A), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {

            icon?.let {
                Image(
                    painter = painterResource(id = it),
                    contentDescription = titulo,
                    modifier = Modifier.size(48.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f).padding(horizontal = 10.dp)
            ) {
                Text(titulo, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(subtitulo, color = Color(0xFFB3B3B3), fontSize = 13.sp)
            }

            // SOLO PLAY, hasta que este conectado
            if (!isCompleted) {
                IconButton(
                    onClick = onStart
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_play),
                        contentDescription = "Iniciar lección",
                        tint = Color(0xFF22DDF2),
                        modifier = Modifier.size(28.dp)
                    )
                }
            } else {
                //  HECHO cuando la lección esté terminada
                Button(
                    onClick = {},
                    enabled = false,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Gray,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Hecho", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeccionesGrupalesScreen(
            navController: NavController,
            onBack: () -> Unit = {}
        ) {
    val gradientBackground = Brush.verticalGradient(
        listOf(Color(0xFF0B132B), Color(0xFF1C2541))
    )

    var lecciones = remember { mutableStateListOf(false, false, false) } // 3 lecciones
    val leccionesCompletadas = lecciones.count { it }
    val leccionesFaltantes = (3 - leccionesCompletadas).coerceAtLeast(0)

    Scaffold(
        modifier = Modifier.statusBarsPadding(),
        topBar = {
            ModernTopBar(
                title = "Lecciones Grupales",
                showBackButton = true,
                onBackClick = onBack
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBackground)
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Descripción general
                Text(
                    text = "Avanza completando misiones y gana XP.",
                    color = Color(0xFFB0BEC5),
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center
                )

                // Título de curso
                Text(
                    text = "Fundamentos de Programación",
                    color = Color(0xFF22DDF2),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                // Estado de progreso
                Text(
                    text = "${leccionesCompletadas}/3 lecciones completadas",
                    color = Color.White,
                    fontSize = 14.sp
                )

                // Tarjeta de progreso general
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFF22DDF2), RoundedCornerShape(16.dp))
                        .background(Color(0xFF0F1A2A), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        LinearProgressIndicator(
                            progress = leccionesCompletadas / 3f,
                            color = Color(0xFF22DDF2),
                            trackColor = Color(0xFF1C2541),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(50))
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (leccionesFaltantes == 0)
                                "¡Curso completado! 🎉"
                            else
                                "$leccionesFaltantes lecciones restantes",
                            color = Color(0xFFB0BEC5),
                            fontSize = 13.sp
                        )
                    }
                }

                // Tarjetas de lecciones
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {

                    ModernLessonCard(
                        icon = R.drawable.ic_tesoro,
                        titulo = "Guardianes del Tesoro",
                        subtitulo = "Variables y condicionales",
                        isCompleted = lecciones[0],
                        onStart = {
                            navController.navigate("leccion_tesoro")
                        }
                    )

                    ModernLessonCard(
                        icon = R.drawable.ic_navegacion,
                        titulo = "Misión de Vuelo",
                        subtitulo = "Ciclos, acumuladores",
                        isCompleted = lecciones[1],
                        onStart = {
                            navController.navigate("leccion_vuelo")
                        }
                    )

                    ModernLessonCard(
                        icon = R.drawable.ic_analizar,
                        titulo = "Reto de los Acertijos",
                        subtitulo = "Funciones y lógica",
                        isCompleted = lecciones[2],
                        onStart = {
                            navController.navigate("leccion_acertijos")
                        }
                    )
                }
            }
        }
    }
}
