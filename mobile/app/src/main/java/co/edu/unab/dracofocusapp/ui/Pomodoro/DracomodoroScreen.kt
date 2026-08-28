package co.edu.unab.dracofocusapp.ui.Pomodoro

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import co.edu.unab.dracofocusapp.R
import co.edu.unab.dracofocusapp.util.SoundEffect
import co.edu.unab.dracofocusapp.util.SoundManager
import co.edu.unab.dracofocusapp.viewmodel.DracomodoroViewModel
import co.edu.unab.dracofocusapp.viewmodel.PomodoroMode
import co.edu.unab.dracofocusapp.viewmodel.PomodoroStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DracomodoroScreen(
    viewModel: DracomodoroViewModel,
    onBack: () -> Unit = {}
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { /* si la niega, simplemente no se muestran notificaciones de cambio de fase */ }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

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
    val runningGreen = Color(0xFF58FF99)
    val circleBorderColor by animateColorAsState(
        targetValue = if (isRunning) runningGreen else dracoCyan,
        animationSpec = tween(durationMillis = 400),
        label = "circleBorderColor"
    )

    // Destello + "pop" del círculo cuando cambia de modo (trabajo <-> descanso),
    // para que el cambio se note claramente. Se omite en la primera composición.
    val flashColor = if (isWorkMode) dracoCyan else runningGreen
    val flashAlpha = remember { Animatable(0f) }
    val circleScale = remember { Animatable(1f) }
    var isFirstModeRender by remember { mutableStateOf(true) }

    LaunchedEffect(uiState.mode) {
        if (isFirstModeRender) {
            isFirstModeRender = false
        } else {
            launch {
                flashAlpha.snapTo(0.5f)
                flashAlpha.animateTo(0f, tween(700))
            }
            launch {
                circleScale.snapTo(1f)
                circleScale.animateTo(1.18f, tween(220))
                circleScale.animateTo(1f, tween(350))
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF0B132B), Color(0xFF1C2541)))),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(flashColor.copy(alpha = flashAlpha.value))
        )
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

            AnimatedContent(
                targetState = isWorkMode,
                transitionSpec = {
                    (slideInVertically { it / 2 } + fadeIn()) togetherWith
                        (slideOutVertically { -it / 2 } + fadeOut())
                },
                label = "modeLabel"
            ) { workMode ->
                Text(
                    if (workMode) "Modo Trabajo" else "Modo Descanso",
                    color = Color(0xFFa2faf6),
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .size(circleSize)
                    .scale(circleScale.value)
                    .border(8.dp, circleBorderColor, CircleShape),
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
                                SoundManager.play(context, SoundEffect.TIMER_START)
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

            Spacer(modifier = Modifier.height(12.dp))
            SkipPhaseButton(
                isWorkMode = isWorkMode,
                onSkip = { viewModel.onSkipPhase() }
            )

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

/**
 * Botón para saltar a la otra fase (trabajo <-> descanso). Tras usarlo, se bloquea
 * 3 segundos (con cuenta regresiva visible) para evitar saltos en cadena por error.
 */
@Composable
fun SkipPhaseButton(
    isWorkMode: Boolean,
    onSkip: () -> Unit
) {
    var cooldownSeconds by remember { mutableStateOf(0) }

    LaunchedEffect(cooldownSeconds) {
        if (cooldownSeconds > 0) {
            delay(1000L)
            cooldownSeconds -= 1
        }
    }

    val canSkip = cooldownSeconds == 0
    TextButton(
        onClick = {
            onSkip()
            cooldownSeconds = 3
        },
        enabled = canSkip
    ) {
        Text(
            if (canSkip) {
                if (isWorkMode) "Saltar trabajo →" else "Saltar descanso →"
            } else {
                "Podrás saltar de nuevo en ${cooldownSeconds}s"
            },
            color = if (canSkip) Color(0xFF8FA3BD) else Color(0xFF4A5568),
            fontWeight = FontWeight.Bold
        )
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
            RepeatingIconButton(
                onClick = { onDelta(-1) },
                enabled = enabled,
                icon = Icons.Default.Remove,
                contentDescription = "Menos",
                tint = if (enabled) Color.White else Color.Gray
            )
            Text("$value", color = if (enabled) Color.White else Color.Gray, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            RepeatingIconButton(
                onClick = { onDelta(1) },
                enabled = enabled,
                icon = Icons.Default.Add,
                contentDescription = "Más",
                tint = if (enabled) Color.White else Color.Gray
            )
        }
    }
}

/**
 * IconButton que repite onClick mientras se mantenga presionado
 * (tras un breve delay inicial), en vez de solo reaccionar a un tap.
 */
@Composable
fun RepeatingIconButton(
    onClick: () -> Unit,
    enabled: Boolean,
    icon: ImageVector,
    contentDescription: String,
    tint: Color
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    LaunchedEffect(isPressed, enabled) {
        if (isPressed && enabled) {
            delay(350L)
            while (isPressed) {
                onClick()
                delay(100L)
            }
        }
    }

    IconButton(onClick = onClick, enabled = enabled, interactionSource = interactionSource) {
        Icon(icon, contentDescription, tint = tint)
    }
}
