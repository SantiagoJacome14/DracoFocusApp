package co.edu.unab.dracofocusapp.ui.Lecciones.Solitario

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import co.edu.unab.dracofocusapp.DracoFocusApplication
import co.edu.unab.dracofocusapp.R
import co.edu.unab.dracofocusapp.ui.components.ModernTopBar
import co.edu.unab.dracofocusapp.domain.rewards.RewardManager
import co.edu.unab.dracofocusapp.ui.Lecciones.ModernLessonCard
import co.edu.unab.dracofocusapp.viewmodel.LessonProgressViewModel
import kotlinx.coroutines.flow.collectLatest
import co.edu.unab.dracofocusapp.viewmodel.SyncState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeccionesDracoSolitarioScreen(
    navController: NavController,
    onBack: () -> Unit,
) {
    Log.d("PROGRESS_SYNC", "Entró a LeccionesDracoSolitarioScreen")
    val app = LocalContext.current.applicationContext as DracoFocusApplication
    val currentUserId by app.tokenManager.userId.collectAsState(initial = null)

    if (currentUserId == null) {
        // Mostrar un estado de carga mientras recuperamos el ID del usuario
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFF22DDF2))
        }
        return
    }

    val lessonVm = viewModel<LessonProgressViewModel>(
        key = "LessonProgressViewModel_$currentUserId",
        factory = LessonProgressViewModel.factory(
            userId = currentUserId!!,
            repository = app.lessonProgressRepository,
            rewardManager = app.rewardManager,
        ),
    )
    
    val snackbarHostState = remember { SnackbarHostState() }

    // Refresca cuando la app vuelve a foreground (cubre el caso de completar en Web con Android abierto)
    val activity = LocalContext.current as? androidx.activity.ComponentActivity
    DisposableEffect(activity) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                lessonVm.refreshProgress()
            }
        }
        activity?.lifecycle?.addObserver(observer)
        onDispose { activity?.lifecycle?.removeObserver(observer) }
    }
    val completadas by lessonVm.soloFundamentosCompleted.collectAsState()

    val progressFraction by lessonVm.soloFundamentosProgress.collectAsState()

    val doneCount = completadas.size.coerceAtMost(3)
    val faltantes = (3 - doneCount).coerceAtLeast(0)

    LaunchedEffect(completadas) {
        Log.d("PROGRESS_SYNC", "Estado actual en UI para userId=$currentUserId: $doneCount/3 completadas")
        Log.d("PROGRESS_UI", "Set de completadas: $completadas")
        Log.d("PROGRESS_SYNC", "Lección 'decisiones_de_fuego' completada: ${"decisiones_de_fuego" in completadas}")
        Log.d("PROGRESS_SYNC", "Lección 'vuelo_infinito' completada: ${"vuelo_infinito" in completadas}")
        Log.d("PROGRESS_SYNC", "Lección 'el_libro_de_tareas' completada: ${"el_libro_de_tareas" in completadas}")
    }

    LaunchedEffect(Unit) {
        Log.d("PROGRESS_SYNC", "Cargando progreso al entrar a Lecciones")
        lessonVm.refreshProgress()
    }

    val gradientBackground =
        Brush.verticalGradient(listOf(Color(0xFF0B132B), Color(0xFF1C2541)))
    val dracoCyan = Color(0xFF22DDF2)

Scaffold(
    modifier = Modifier.statusBarsPadding(),
    snackbarHost = { SnackbarHost(snackbarHostState) },
    topBar = {
        ModernTopBar(
            title = "Lecciones Draco Solitario",
            showBackButton = true,
            onBackClick = { onBack() },
        )
    },
) { innerPadding ->
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBackground)
            .padding(innerPadding)
            .padding(horizontal = 20.dp, vertical = 10.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Text(
                "Avanza completando misiones y gana XP.",
                color = Color(0xFFB3B3B3),
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
            )

            Text(
                text = "Fundamentos de Programación",
                color = dracoCyan,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = "$doneCount/3 completadas",
                color = Color.White,
                fontSize = 14.sp,
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, dracoCyan, RoundedCornerShape(16.dp))
                    .background(Color(0xFF0F1A2A), RoundedCornerShape(16.dp))
                    .padding(16.dp),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    LinearProgressIndicator(
                        progress = { progressFraction },
                        color = dracoCyan,
                        trackColor = Color(0xFF1C2541),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(50)),
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        if (faltantes == 0) "¡Set completado!" else "$faltantes lecciones restantes",
                        color = Color(0xFFB3B3B3),
                        fontSize = 13.sp,
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                val isFireDone  = "decisiones_de_fuego" in completadas
                val isLoopDone  = "vuelo_infinito" in completadas
                val isArraysDone = "el_libro_de_tareas" in completadas

                // Bloqueo progresivo: cada lección requiere la anterior completada
                val isVueloLocked  = !isFireDone
                val isArraysLocked = !isLoopDone

                Log.d("PROGRESS_UI", "UI: fire=$isFireDone vuelo=$isLoopDone (locked=$isVueloLocked) arrays=$isArraysDone (locked=$isArraysLocked)")

                ModernLessonCard(
                    icon = R.drawable.ic_lesson_fire,
                    titulo = "Decisiones de Fuego",
                    subtitulo = "Condicionales • retos mixtos",
                    isCompleted = isFireDone,
                    onStart = {
                        val route = if (isFireDone) "leccion_reto/decisiones_de_fuego?review=true" else "leccion_reto/decisiones_de_fuego"
                        navController.navigate(route) { launchSingleTop = true }
                    },
                )

                ModernLessonCard(
                    icon = R.drawable.ic_lesson_loop,
                    titulo = "Vuelo Infinito",
                    subtitulo = "Bucles • retos mixtos",
                    isCompleted = isLoopDone,
                    isLocked = isVueloLocked,
                    onStart = {
                        val route = if (isLoopDone) "leccion_reto/vuelo_infinito?review=true" else "leccion_reto/vuelo_infinito"
                        navController.navigate(route) { launchSingleTop = true }
                    },
                )

                ModernLessonCard(
                    icon = R.drawable.ic_lesson_arrays,
                    titulo = "El Libro de las Tareas",
                    subtitulo = "Lists & lógica",
                    isCompleted = isArraysDone,
                    isLocked = isArraysLocked,
                    onStart = {
                        val route = if (isArraysDone) "leccion_reto/el_libro_de_tareas?review=true" else "leccion_reto/el_libro_de_tareas"
                        navController.navigate(route) { launchSingleTop = true }
                    },
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, dracoCyan.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                        .background(Color(0xFF0A1628), RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📬", fontSize = 20.sp)
                        Spacer(Modifier.width(10.dp))
                        Text(
                            "Completa lecciones para recibir sobres con imágenes y completar tu Museo.",
                            color = Color(0xFFB3C9E0),
                            fontSize = 13.sp,
                        )
                    }
                }

            }
        }

    }
}
}