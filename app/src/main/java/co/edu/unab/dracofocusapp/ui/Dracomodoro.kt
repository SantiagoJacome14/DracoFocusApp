package co.edu.unab.dracofocusapp.ui

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
import co.edu.unab.dracofocusapp.main.BottomNavigationBar

@Composable
fun DracomodoroScreen(navController: NavController) {
    // Estados
    var workMinutes by remember { mutableStateOf(25) }
    var restMinutes by remember { mutableStateOf(5) }
    var secondsLeft by remember { mutableStateOf(workMinutes * 60) }
    var isRunning by remember { mutableStateOf(false) }
    var isWorkMode by remember { mutableStateOf(true) }

    // Temporizador
    LaunchedEffect(isRunning, secondsLeft, isWorkMode) {
        if (isRunning && secondsLeft > 0) {
            delay(1000L)
            secondsLeft--
        } else if (isRunning && secondsLeft == 0) {
            // Cuando se termina el tiempo, cambia entre trabajo y descanso
            isWorkMode = !isWorkMode
            secondsLeft = if (isWorkMode) workMinutes * 60 else restMinutes * 60
        }
    }

    val minutes = secondsLeft / 60
    val seconds = secondsLeft % 60
    val timeDisplay = String.format("%02d:%02d", minutes, seconds)
    val gradientBackground = Brush.verticalGradient(
        listOf(Color(0xFF0B132B), Color(0xFF1C2541))
    )

    // Contenido
    Scaffold(
        //bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
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
                    Text(
                        text = "Dracomodoro",
                        color = Color(0xFF22DDF2),
                        fontSize = 45.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isWorkMode) "Modo Trabajo" else "Modo Descanso",
                        color = Color(0xFFa2faf6),
                        fontSize = 22.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .border(10.dp, Color(0xFF22DDF2), shape = CircleShape)
                        .padding(25.dp)
                        .border(5.dp, Color(0xFF22DDF2), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = timeDisplay,
                        color = Color.White,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Botones
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Button(
                        onClick = {
                            isRunning = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF8A78FF)
                        ),
                        modifier = Modifier
                            .border(
                                width = 2.dp,
                                color = Color(0xFF22DDF2),
                                shape = RoundedCornerShape(30.dp)
                            )
                    )
                    {
                        Text(text = "+ Iniciar", color = Color.White)
                    }

                    Button(
                        onClick = {
                            isRunning = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF8A78FF)
                        ),
                        modifier = Modifier
                            .border(
                                width = 2.dp,
                                color = Color(0xFF22DDF2),
                                shape = RoundedCornerShape(30.dp)
                            )
                    ) {
                        Text(text = "❚❚ Pausar", color = Color.White)
                    }

                    Button(
                        onClick = {
                            isRunning = false
                            secondsLeft = if (isWorkMode) workMinutes * 60 else restMinutes * 60
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF8A78FF)
                        ),
                        modifier = Modifier
                            .border(
                                width = 2.dp,
                                color = Color(0xFF22DDF2),
                                shape = RoundedCornerShape(30.dp)
                            )
                    ) {
                        Text(text = "↻ Reiniciar", color = Color.White)
                    }
                }

                // Trabajo y descanso
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .padding(20.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(2f)
                            .height(65.dp)
                            .background(Color(0xFF6a5cc4)),
                        contentAlignment = Alignment.Center
                    ) {

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Trabajo",
                                color = Color(0xFFb6b6b6),
                                fontWeight = FontWeight.Bold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = {
                                    if (workMinutes > 1) {
                                        workMinutes--
                                        if (isWorkMode) secondsLeft = workMinutes * 60
                                    }
                                }) {
                                    Icon(Icons.Filled.Remove, contentDescription = "Disminuir", tint = Color.White)
                                }
                                Text(text = "$workMinutes min", color = Color.White)
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
                        // Descanso
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Descanso",
                                color = Color(0xFFb6b6b6),
                                fontWeight = FontWeight.Bold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = {
                                    if (restMinutes > 1) {
                                        restMinutes--
                                        if (!isWorkMode) secondsLeft = restMinutes * 60
                                    }
                                }) {
                                    Icon(
                                        Icons.Filled.Remove,
                                        contentDescription = "Disminuir",
                                        tint = Color.White
                                    )
                                }
                                Text(text = "$restMinutes min", color = Color.White)
                                IconButton(onClick = {
                                    restMinutes++
                                    if (!isWorkMode) secondsLeft = restMinutes * 60
                                }) {
                                    Icon(
                                        Icons.Filled.Add,
                                        contentDescription = "Aumentar",
                                        tint = Color.White
                                    )
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
