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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import co.edu.unab.dracofocusapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay

@Composable
fun DracomodoroScreen(
    onBack: () -> Unit = {},
    navController: NavController? = null
) {
    var workMinutes by remember { mutableStateOf(25) }
    var restMinutes by remember { mutableStateOf(5) }
    var secondsLeft by remember { mutableStateOf(workMinutes * 60) }
    var isRunning by remember { mutableStateOf(false) }
    var isWorkMode by remember { mutableStateOf(true) }

    val circleSize by animateDpAsState(
        targetValue = if (isWorkMode) 220.dp else 250.dp,
        animationSpec = tween(durationMillis = 800)
    )

    fun registrarEstudioEnFirebase() {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val usuarioRef = db.collection("usuarios_estadisticas").document(userId)
        usuarioRef.update(
            mapOf(
                "cantidad_estudio" to FieldValue.increment(1),
                "minutos_estudiados_semanal" to FieldValue.increment(workMinutes.toDouble()),
                "horas_estudio" to FieldValue.increment(workMinutes / 60.0),
                "dia" to FieldValue.serverTimestamp()
            )
        )
    }

    LaunchedEffect(isRunning, secondsLeft) {
        if (isRunning && secondsLeft > 0) {
            delay(1000L)
            secondsLeft--
        } else if (isRunning && secondsLeft == 0) {
            if (!isWorkMode) {
                registrarEstudioEnFirebase()
                navController?.navigate("ciclo_completado")
            }
            isWorkMode = !isWorkMode
            secondsLeft = if (isWorkMode) workMinutes * 60 else restMinutes * 60
        }
    }

    val timeDisplay = String.format("%02d:%02d", secondsLeft / 60, secondsLeft % 60)
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

            // UI PULIDA: Botones unificados sin espacios (Sólidos)
            Surface(
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                color = Color(0xFF1C2541),
                border = BorderStroke(1.dp, dracoCyan)
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    Button(
                        onClick = { isRunning = true },
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        shape = RoundedCornerShape(topStart = 28.dp, bottomStart = 28.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) { Text("INICIAR", color = Color.White, fontWeight = FontWeight.Bold) }
                    
                    VerticalDivider(color = dracoCyan.copy(alpha = 0.5f), thickness = 1.dp)
                    
                    Button(
                        onClick = { isRunning = false },
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        shape = RoundedCornerShape(0.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) { Text("PAUSAR", color = Color.White, fontWeight = FontWeight.Bold) }
                    
                    VerticalDivider(color = dracoCyan.copy(alpha = 0.5f), thickness = 1.dp)

                    Button(
                        onClick = {
                            isRunning = false
                            secondsLeft = if (isWorkMode) workMinutes * 60 else restMinutes * 60
                        },
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        shape = RoundedCornerShape(topEnd = 28.dp, bottomEnd = 28.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) { Text("REINICIAR", color = Color.White, fontWeight = FontWeight.Bold) }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Configuración modular
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                TimeAdjuster("TRABAJO", workMinutes, dracoCyan) { workMinutes = it; if(isWorkMode) secondsLeft = it * 60 }
                TimeAdjuster("DESCANSO", restMinutes, Color(0xFF58FF99)) { restMinutes = it; if(!isWorkMode) secondsLeft = it * 60 }
            }
        }
    }
}

@Composable
fun TimeAdjuster(label: String, value: Int, color: Color, onValueChange: (Int) -> Unit) {
    Column(
        modifier = Modifier
            .background(Color(0xFF1C2541), RoundedCornerShape(16.dp))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { if (value > 1) onValueChange(value - 1) }) {
                Icon(Icons.Default.Remove, "Menos", tint = Color.White)
            }
            Text("$value", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            IconButton(onClick = { if (value < 60) onValueChange(value + 1) }) {
                Icon(Icons.Default.Add, "Más", tint = Color.White)
            }
        }
    }
}
