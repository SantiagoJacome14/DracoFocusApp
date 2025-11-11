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
@Composable
fun MuseoDracArteScreen(navController: NavController) {
    val gradientBackground = Brush.verticalGradient(
        listOf(Color(0xFF0B132B), Color(0xFF1C2541))
    )

    var piezasCompletas by remember { mutableStateOf(0) }
    var pinturasCompletas by remember { mutableStateOf(0) }

    val piezas = remember { mutableStateListOf<Boolean>().apply { repeat(24) { add(false) } } }

    val progreso = piezasCompletas * 4.2f
    val progresoClamped = progreso.coerceAtMost(100f)

    Scaffold(
        modifier = Modifier.statusBarsPadding(),
        topBar = {
            ModernTopBar(
                title = "Museo Dracarte",
                showBackButton = true
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBackground)
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .clip(RoundedCornerShape(20.dp))
                    .border(2.dp, Color.Black, RoundedCornerShape(20.dp))
                    .background(Color(0xFF101C33))
                    .padding(16.dp)
            ) {
                // Fondo difuminado
                Image(
                    painter = painterResource(id = R.drawable.img_fondo),
                    contentDescription = "Fondo del museo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .matchParentSize()
                        .blur(5.dp)
                )

                // ✅ Scroll vertical para todo el contenido
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()) // ← ¡Clave!
                        .padding(16.dp)
                ) {
                    Spacer(modifier = Modifier.height(20.dp))

                    // Barra de progreso
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Progreso", color = Color.White)
                        Text("${pinturasCompletas}/4", color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = progresoClamped / 100f,
                        color = Color(0xFFF44336),
                        trackColor = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(50.dp))
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${progresoClamped.toInt()}%",
                        color = Color.White,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Cuatro obras del museo
                    Column(
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
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

                    Spacer(modifier = Modifier.height(40.dp)) // margen final para evitar corte
                }
            }
        }
    }
}

