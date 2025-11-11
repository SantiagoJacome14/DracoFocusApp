package co.edu.unab.dracofocusapp.ui.Pomodoro

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import kotlinx.coroutines.delay
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.ui.graphics.Brush
import co.edu.unab.dracofocusapp.R
import androidx.compose.ui.platform.LocalContext

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween



@Composable
fun DracomodoroScreen(
    onBack: () -> Unit = {},
    navController: NavController? = null  // navegar a pantalla ciclo completado dracomodoro
) {
    // Estados

    var workMinutes by remember { mutableStateOf(25) }
    var restMinutes by remember { mutableStateOf(5) }
    var secondsLeft by remember { mutableStateOf(workMinutes * 60) }
    var isRunning by remember { mutableStateOf(false) }
    var isWorkMode by remember { mutableStateOf(true) }
    var completedCycles by remember { mutableStateOf(0) }


    val context = LocalContext.current

    // Animación del tamaño del círculo
    val circleSize by animateDpAsState(
        targetValue = if (isWorkMode) 220.dp else 250.dp,
        animationSpec = tween(durationMillis = 800)
    )

    // Temporizador
    LaunchedEffect(isRunning, secondsLeft, isWorkMode) {
        if (isRunning && secondsLeft > 0) {
            delay(1000L)
            secondsLeft--
        }
        else if (isRunning && secondsLeft == 0) {

            // Cuando termina de modo descanso hace el ciclo completo terminado
            if (!isWorkMode) {
                completedCycles++
                navController?.navigate("ciclo_completado")
            }

            // Cambia modo
            isWorkMode = !isWorkMode

            // Nuevo tiempo
            secondsLeft = if (isWorkMode) workMinutes * 60 else restMinutes * 60
        }
    }
    val minutes = secondsLeft / 60
    val seconds = secondsLeft % 60
    val timeDisplay = String.format("%02d:%02d", minutes, seconds)

    //cambia el fondo segun el modo que este, trabajo o descanso
    val gradientBackground = Brush.verticalGradient(
        if (isWorkMode)
            listOf(Color(0xFF0B132B), Color(0xFF1C2541)) // TRABAJO
        else
            listOf(Color(0xFF003022), Color(0xFF005C41)) // DESCANSO
    )
    // Color dinámico del borde
    val borderColor = if (isWorkMode) Color(0xFF22DDF2) else Color(0xFF58FF99)

    // Contenido
    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBackground)
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxSize()
            ) {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 10.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.dragon_dracofocus1),
                        contentDescription = "Draco",
                        modifier = Modifier.size(130.dp)
                    )
                    Text("Dracomodoro", color = Color(0xFF22DDF2), fontSize = 45.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = if (isWorkMode) "Modo Trabajo" else "Modo Descanso",
                        color = Color(0xFFa2faf6),
                        fontSize = 22.sp
                    )
                }

                // Temporizador con animacion
                Box(
                    modifier = Modifier
                        .size(circleSize)
                        .border(10.dp, borderColor, shape = CircleShape)
                        .padding(25.dp)
                        .border(5.dp, borderColor, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(timeDisplay, color = Color.White, fontSize = 48.sp, fontWeight = FontWeight.Bold)
                }

                // Botones
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Button(
                        onClick = { isRunning = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8A78FF)),
                        modifier = Modifier.border(2.dp, borderColor, shape = RoundedCornerShape(30.dp))
                    ) { Text("+ Iniciar", color = Color.White) }

                    Button(
                        onClick = { isRunning = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8A78FF)),
                        modifier = Modifier.border(2.dp, borderColor, shape = RoundedCornerShape(30.dp))
                    ) { Text("❚❚ Pausar", color = Color.White) }

                    Button(
                        onClick = {
                            isRunning = false
                            secondsLeft = if (isWorkMode) workMinutes * 60 else restMinutes * 60
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8A78FF)),
                        modifier = Modifier.border(2.dp, borderColor, shape = RoundedCornerShape(30.dp))
                    ) { Text("↻ Reiniciar", color = Color.White) }
                }
                // Trabajo y descanso tiempos
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.padding(20.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(2f)
                            .height(65.dp)
                            .background(Color(0xFF6a5cc4)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Trabajo", color = Color(0xFFb6b6b6), fontWeight = FontWeight.Bold)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = {
                                    if (workMinutes > 1) {
                                        workMinutes--
                                        if (isWorkMode) secondsLeft = workMinutes * 60
                                    }
                                }) {
                                    Icon(Icons.Filled.Remove, contentDescription = "Disminuir", tint = Color.White)
                                }
                                Text("$workMinutes min", color = Color.White)
                                IconButton(onClick = {
                                    workMinutes++
                                    if (isWorkMode) secondsLeft = workMinutes * 60
                                }) {
                                    Icon(Icons.Filled.Add, contentDescription = "Aumentar", tint = Color.White)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    Box(
                        modifier = Modifier
                            .weight(2f)
                            .height(65.dp)
                            .background(Color(0xFF6a5cc4))
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Descanso", color = Color(0xFFb6b6b6), fontWeight = FontWeight.Bold)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = {
                                    if (restMinutes > 1) {
                                        restMinutes--
                                        if (!isWorkMode) secondsLeft = restMinutes * 60
                                    }
                                }) {
                                    Icon(Icons.Filled.Remove, contentDescription = "Disminuir", tint = Color.White)
                                }
                                Text("$restMinutes min", color = Color.White)
                                IconButton(onClick = {
                                    restMinutes++
                                    if (!isWorkMode) secondsLeft = restMinutes * 60
                                }) {
                                    Icon(Icons.Filled.Add, contentDescription = "Aumentar", tint = Color.White)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(50.dp))
                }
            }
        }
    }
}

