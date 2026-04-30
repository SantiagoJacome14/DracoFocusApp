package co.edu.unab.dracofocusapp.ui.Museo

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.material3.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import co.edu.unab.dracofocusapp.R
import co.edu.unab.dracofocusapp.auth.ModernTopBar
import androidx.compose.foundation.verticalScroll

// Esta función representa la pantalla "Museo DracArte" dentro de la aplicación
@Composable
fun MuseoDracArteScreen(navController: NavController) {

    // Fondo con un degradado de colores oscuros
    val gradientBackground = Brush.verticalGradient(
        listOf(Color(0xFF0B132B), Color(0xFF1C2541))
    )

    // Variables que almacenan el número de piezas y pinturas completadas
    var piezasCompletas by remember { mutableStateOf(0) }
    var pinturasCompletas by remember { mutableStateOf(0) }

    // Lista que representa las piezas del museo, inicialmente todas sin completar (false)
    val piezas = remember { mutableStateListOf<Boolean>().apply { repeat(24) { add(false) } } }

    // Cálculo del progreso general del jugador en porcentaje
    val progreso = piezasCompletas * 4.2f
    val progresoClamped = progreso.coerceAtMost(100f) // Limita el valor máximo a 100%

    // Estructura base de la pantalla con barra superior y contenido principal
    Scaffold(
        modifier = Modifier.statusBarsPadding(),
        topBar = {
            // Barra superior con título y botón de retroceso
            ModernTopBar(
                title = "Museo Dracarte",
                showBackButton = true,
                onBackClick = { navController.popBackStack() } // Vuelve a la pantalla anterior
            )
        },
    ) { innerPadding ->

        // Contenedor principal de la pantalla
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBackground)
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {

            // Caja principal que contiene el marco del museo
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .clip(RoundedCornerShape(20.dp)) // Bordes redondeados
                    .border(2.dp, Color.Black, RoundedCornerShape(20.dp)) // Borde negro
                    .background(Color(0xFF101C33)) // Fondo azul oscuro
                    .padding(16.dp)
            ) {

                // Imagen de fondo difuminada para dar ambiente al museo
                Image(
                    painter = painterResource(id = R.drawable.img_fondo),
                    contentDescription = "Fondo del museo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .matchParentSize()
                        .blur(5.dp) // Efecto de desenfoque
                )

                // Columna que organiza todos los elementos visuales de forma vertical
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()) // Permite desplazarse si hay mucho contenido
                        .padding(16.dp)
                ) {
                    Spacer(modifier = Modifier.height(20.dp))

                    // Sección que muestra el progreso general
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Progreso", color = Color.White)
                        Text("${pinturasCompletas}/4", color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Barra visual que representa el progreso total del usuario
                    LinearProgressIndicator(
                        progress = progresoClamped / 100f,
                        color = Color(0xFFF44336), // Rojo brillante para el progreso
                        trackColor = Color.White,  // Fondo de la barra
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(50.dp))
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${progresoClamped.toInt()}%", // Porcentaje mostrado en texto
                        color = Color.White,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Sección donde se muestran las obras del museo
                    Column(
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        // Primera fila con dos imágenes de pinturas incompletas
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.pintura1_0),
                                contentDescription = "Obra incompleta 1",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(10.dp))
                            )
                            Image(
                                painter = painterResource(id = R.drawable.pintura2_0),
                                contentDescription = "Obra incompleta 2",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(10.dp))
                            )
                        }

                        // Segunda fila con otras dos pinturas incompletas
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.pintura3_0),
                                contentDescription = "Obra incompleta 3",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(10.dp))
                            )
                            Image(
                                painter = painterResource(id = R.drawable.pintura4_0),
                                contentDescription = "Obra incompleta 4",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(10.dp))
                            )
                        }
                    }

                    // Espacio final para evitar que el contenido quede cortado en pantallas pequeñas
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}
