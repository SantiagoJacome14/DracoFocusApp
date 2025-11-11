package co.edu.unab.dracofocusapp.ui.Lecciones.Solitario

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.material3.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import co.edu.unab.dracofocusapp.R
import co.edu.unab.dracofocusapp.ui.Lecciones.Grupales.ModernLessonCard
import co.edu.unab.dracofocusapp.auth.ModernTopBar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeccionesDracoSolitarioScreen(
    navController: NavController,
    onBack: () -> Unit
) {
    val gradientBackground = Brush.verticalGradient(
        listOf(Color(0xFF0B132B), Color(0xFF1C2541))
    )

    // Estados de las lecciones
    var lecciones = remember { mutableStateListOf(false, false, false) }
    val leccionesCompletadas = lecciones.count { it }
    val leccionesFaltantes = (3 - leccionesCompletadas).coerceAtLeast(0)

    Scaffold(
        modifier = Modifier.statusBarsPadding(),
        topBar = {
            ModernTopBar(
                title = "Lecciones Draco Solitario",
                showBackButton = true,
                onBackClick = { onBack() }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBackground)
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                Text(
                    "Avanza completando misiones y gana XP.",
                    color = Color(0xFFB3B3B3),
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Fundamentos de Programación",
                    color = Color(0xFF22DDF2),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                // Barra de progreso
                Text(
                    "${leccionesCompletadas}/3 completadas",
                    color = Color.White,
                    fontSize = 14.sp
                )

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
                            if (leccionesFaltantes == 0)
                                "¡Curso completado! 🎉"
                            else
                                "$leccionesFaltantes lecciones restantes",
                            color = Color(0xFFB3B3B3),
                            fontSize = 13.sp
                        )
                    }
                }

                // TARJETAS DE LECCIONES
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {

                    ModernLessonCard(
                        icon = R.drawable.ic_decisiones_fuego,
                        titulo = "Decisiones de Fuego",
                        subtitulo = "Condicionales",
                        isCompleted = lecciones[0],
                        onStart = {
                            navController.navigate("leccion_decisiones_fuego") {
                                launchSingleTop = true
                            }
                        }
                    )

                    ModernLessonCard(
                        icon = R.drawable.ic_vuelo_infinito,
                        titulo = "Vuelo Infinito",
                        subtitulo = "Bucles",
                        isCompleted = lecciones[1],
                        onStart = {
                            navController.navigate("leccion_vuelo_infinito") {
                                launchSingleTop = true
                            }
                        }
                    )

                    ModernLessonCard(
                        icon = R.drawable.ic_libro_tareas,
                        titulo = "El Libro de las Tareas",
                        subtitulo = "Arreglos",
                        isCompleted = lecciones[2],
                        onStart = {
                            navController.navigate("leccion_libro_tareas") {
                                launchSingleTop = true
                            }
                        }
                    )




                    // Caja de recompensa
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0F2B5D), RoundedCornerShape(15.dp))
                            .border(3.dp, Color(0xFF57F5ED), RoundedCornerShape(15.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Image(
                                painter = painterResource(R.drawable.img_sobre),
                                contentDescription = "Sobre misterioso",
                                modifier = Modifier.size(70.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                "¡Te faltan solo $leccionesFaltantes para un sobre misterioso!",
                                color = Color(0xFF22DDF2),
                                textAlign = TextAlign.Center,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}