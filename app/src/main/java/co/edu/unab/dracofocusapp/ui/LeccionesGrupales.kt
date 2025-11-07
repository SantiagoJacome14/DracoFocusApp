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
import co.edu.unab.dracofocusapp.R


@Composable
fun LeccionesGrupales(navController: NavController) {
    // Estados
    var leccionesCompletadas by remember { mutableStateOf(0) } // de 0 a 3
    val leccionesFaltantes = (3 - leccionesCompletadas).coerceAtLeast(0)

    Scaffold(
        //bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF3D62A4))
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                //Título e info inicial
                Text(
                    text = "Lecciones Grupales",
                    color = Color(0xFFEBFFFE),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 10.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

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
                        titulo = "Guardianes del Tesoro",
                        subtitulo = "Variables y condicionales",
                        xp = "+ 90 XP",
                        navController = navController
                    ) {
                        leccionesCompletadas++
                    }

                    LessonCard(
                        titulo = "Misión de Vuelo",
                        subtitulo = "Ciclos, listas y acumuladores",
                        xp = "+ 150 XP",
                        navController = navController
                    ) {
                        leccionesCompletadas++
                    }

                    LessonCard(
                        titulo = "El reto de los acertijos",
                        subtitulo = "Funciones y lógica condicional",
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
