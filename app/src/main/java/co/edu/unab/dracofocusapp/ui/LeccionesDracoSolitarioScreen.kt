package co.edu.unab.dracofocusapp.ui

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
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.ui.graphics.Brush
import co.edu.unab.dracofocusapp.R
import co.edu.unab.dracofocusapp.ui.components.ModernTopBar


@Composable
fun LeccionesDracoSolitarioScreen(
    navController: NavController,
    onBack: () -> Unit
) {

    val gradientBackground = Brush.verticalGradient(
        listOf(Color(0xFF0B132B), Color(0xFF1C2541))
    )
    // Estados
    var leccionesCompletadas by remember { mutableStateOf(0) } // de 0 a 3
    val leccionesFaltantes = (3 - leccionesCompletadas).coerceAtLeast(0)


    Scaffold(
        modifier = Modifier.statusBarsPadding(),
        topBar = {
            ModernTopBar(
                title = "Lecciones DracoSolitario",
                showBackButton = true,
                onBackClick = {
                    navController.navigate("menu_lecciones") {
                        popUpTo("lecciones_solitario") { inclusive = true }
                    }
                }
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
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = "Avanza realizando ejercicios y completando los módulos para ganar XP mientras aprendes.",
                    color = Color(0xFFB3B3B3),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Fundamentos de Programación",
                    color = Color(0xFF12D2CA),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                //Progreso de lecciones
                Text(
                    text = "${leccionesCompletadas}/3 completadas",
                    color = Color(0xFFB3B3B3),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                //Lista de lecciones
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    LessonCard(
                        titulo = "Decisiones de Fuego",
                        subtitulo = "Condicionales",
                        xp = "+ 90 XP",
                        navController = navController
                    ) {
                        leccionesCompletadas++
                    }

                    LessonCard(
                        titulo = "Vuelo Infinito",
                        subtitulo = "Bucles",
                        xp = "+ 150 XP",
                        navController = navController
                    ) {
                        leccionesCompletadas++
                    }

                    LessonCard(
                        titulo = "El Libro de las Tareas",
                        subtitulo = "Arreglos",
                        xp = "+ 120 XP",
                        navController = navController
                    ) {
                        leccionesCompletadas++
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                //Recuadro del sobre de piezas
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
                            painter = painterResource(id = R.drawable.img_sobre),
                            contentDescription = "Sobre de piezas",
                            modifier = Modifier
                                .size(70.dp)
                                .align(Alignment.CenterHorizontally)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "¡Te faltan solo $leccionesFaltantes lecciones para desbloquear un sobre de piezas misteriosas y seguir completando su galería!",
                            color = Color(0xFF12D2CA),
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
@Composable
fun LessonCard(
    titulo: String,
    subtitulo: String,
    xp: String,
    navController: NavController,
    onComplete: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF2C4A7A), RoundedCornerShape(15.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_lec),
                contentDescription = "Ícono de lección",
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(10.dp))
            )

            Column(
                modifier = Modifier.weight(1f).padding(horizontal = 10.dp)
            ) {
                Text(
                    text = titulo,
                    color = Color(0xFFF2F2F2),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitulo,
                    color = Color(0xFFCBC8C8),
                    fontSize = 13.sp
                )
            }

            // XP + botón Play
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .background(Color(0xFF6A5CC4), RoundedCornerShape(50.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(xp, color = Color.White, fontSize = 13.sp)
                }

                IconButton(
                    onClick = {
                        when (titulo) {
                            "Decisiones de Fuego" -> navController.navigate("leccion_decisiones_fuego")
                            "Vuelo Infinito" -> navController.navigate("leccion_vuelo_infinito")
                            "El Libro de las Tareas" -> navController.navigate("leccion_libro_tareas")

                        }
                    }
                )
                    {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Iniciar lección",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    }
}


