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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.blur
import co.edu.unab.dracofocusapp.R

@Composable
fun MuseoDracArte(navController: NavController) {
    // Estados
    var piezasCompletas by remember { mutableStateOf(0) }
    var pinturasCompletas by remember { mutableStateOf(0) }

    // 24 piezas en total. Inicialmente todas falsas (no desbloqueadas)
    val piezas = remember { mutableStateListOf<Boolean>().apply { repeat(24) { add(false) } } }

    val progreso = piezasCompletas * 4.2f // cada pieza vale 4.2%
    val progresoClamped = progreso.coerceAtMost(100f)

    Scaffold(
       // bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFcbc8c8))
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.9f)
                    .clip(RoundedCornerShape(20.dp))
                    .border(2.dp, Color.Black, RoundedCornerShape(20.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_fondo),
                    contentDescription = "Fondo del museo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .blur(5.dp)
                )

                //Contenido principal
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Museo Dracarte",
                        color = Color.White,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Barra de progreso
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Progreso", color = Color.White)
                        Text("${pinturasCompletas}/4", color = Color.White)
                    }

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

                    Spacer(modifier = Modifier.height(16.dp))

                    // Cuatro obras
                    Column(
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxHeight(1f)
                    ) {
                        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                            Image(
                                painter = painterResource(id = R.drawable.pintura1_0),
                                contentDescription = "Obra incompleta 1",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.weight(2f)

                            )
                            Spacer(modifier=Modifier.width(8.dp))
                            Image(
                                painter = painterResource(id = R.drawable.pintura2_0),
                                contentDescription = "Obra incompleta 2",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.weight(2f)
                            )

                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                            Image(
                                painter = painterResource(id = R.drawable.pintura3_0),
                                contentDescription = "Obra incompleta 3",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.weight(2f)
                            )
                            Spacer(modifier=Modifier.width(8.dp))
                            Image(
                                painter = painterResource(id = R.drawable.pintura4_0),
                                contentDescription = "Obra incompleta 4",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.weight(2f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                }
            }
        }
    }
}
