package co.edu.unab.dracofocusapp.ui.Pomodoro

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.unab.dracofocusapp.R
import co.edu.unab.dracofocusapp.viewmodel.DracomodoroViewModel
import co.edu.unab.dracofocusapp.viewmodel.PomodoroMode
import co.edu.unab.dracofocusapp.viewmodel.PomodoroStatus

@Composable
fun DracomodoroScreen(
    viewModel: DracomodoroViewModel,
    onBack: () -> Unit = {}
) {
    val uiState = viewModel.uiState
    
    // Evitar parpadeos de carga inicial
    if (!uiState.isInitialized) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFF22DDF2))
        }
        return
    }

    val isWorkMode = uiState.mode == PomodoroMode.WORK
    val isRunning = uiState.status == PomodoroStatus.RUNNING

    val circleSize by animateDpAsState(
        targetValue = if (isWorkMode) 220.dp else 250.dp,
        animationSpec = tween(durationMillis = 800),
        label = "circleSize"
    )

    val timeDisplay = String.format("%02d:%02d", uiState.secondsLeft / 60, uiState.secondsLeft % 60)
    val dracoCyan = Color(0xFF22DDF2)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF0B132B), Color(0xFF1C2541)))),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.dragon_dracofocus1),
                contentDescription = "Draco",
                modifier = Modifier.size(120.dp)
            )
            Text("Dracomodoro", color = dracoCyan, fontSize = 40.sp, fontWeight = FontWeight.Bold)
            Text(if (isWorkMode) "Modo Trabajo" else "Modo Descanso", color = Color(0xFFa2faf6), fontSize = 18.sp)

            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .size(circleSize)
                    .border(8.dp, if (isWorkMode) dracoCyan else Color(0xFF58FF99), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(timeDisplay, color = Color.White, fontSize = 48.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(40.dp))

            // UI PULIDA: Botones unificados
            Surface(
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                color = Color(0xFF1C2541),
                border = BorderStroke(1.dp, dracoCyan)
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    if (!isRunning) {
                        Button(
                            onClick = { 
                                if (uiState.status == PomodoroStatus.PAUSED) viewModel.onResume() 
                                else viewModel.onStart() 
                            },
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            shape = RoundedCornerShape(topStart = 28.dp, bottomStart = 28.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) { Text("INICIAR", color = Color.White, fontWeight = FontWeight.Bold) }
                    } else {
                        Button(
                            onClick = { viewModel.onPause() },
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            shape = RoundedCornerShape(topStart = 28.dp, bottomStart = 28.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) { Text("PAUSAR", color = Color.White, fontWeight = FontWeight.Bold) }
                    }
                    
                    VerticalDivider(color = dracoCyan.copy(alpha = 0.5f), thickness = 1.dp)

                    Button(
                        onClick = { viewModel.onReset() },
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        shape = RoundedCornerShape(topEnd = 28.dp, bottomEnd = 28.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) { Text("REINICIAR", color = Color.White, fontWeight = FontWeight.Bold) }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Configuración modular (solo permitida si no está corriendo)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                TimeAdjuster(
                    label = "TRABAJO", 
                    value = uiState.workMinutes, 
                    color = dracoCyan,
                    enabled = uiState.status == PomodoroStatus.IDLE
                ) { viewModel.adjustWorkMinutes(it) }
                
                TimeAdjuster(
                    label = "DESCANSO", 
                    value = uiState.restMinutes, 
                    color = Color(0xFF58FF99),
                    enabled = uiState.status == PomodoroStatus.IDLE
                ) { viewModel.adjustRestMinutes(it) }
            }
        }
    }
}

@Composable
fun TimeAdjuster(
    label: String, 
    value: Int, 
    color: Color, 
    enabled: Boolean,
    onDelta: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .background(Color(0xFF1C2541), RoundedCornerShape(16.dp))
            .border(1.dp, color.copy(alpha = if (enabled) 0.3f else 0.1f), RoundedCornerShape(16.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, color = if (enabled) Color.Gray else Color.DarkGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { onDelta(-1) }, enabled = enabled) {
                Icon(Icons.Default.Remove, "Menos", tint = if (enabled) Color.White else Color.Gray)
            }
            Text("$value", color = if (enabled) Color.White else Color.Gray, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            IconButton(onClick = { onDelta(1) }, enabled = enabled) {
                Icon(Icons.Default.Add, "Más", tint = if (enabled) Color.White else Color.Gray)
            }
        }
    }
}
