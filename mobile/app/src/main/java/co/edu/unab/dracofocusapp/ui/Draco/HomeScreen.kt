package co.edu.unab.dracofocusapp.ui.Draco

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import co.edu.unab.dracofocusapp.DracoFocusApplication
import co.edu.unab.dracofocusapp.R
import co.edu.unab.dracofocusapp.main.BottomNavItem
import co.edu.unab.dracofocusapp.viewmodel.ProfileViewModel
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

@Composable
fun HomeScreen(navController: NavController) {
    val app = LocalContext.current.applicationContext as DracoFocusApplication
    val profileVm: ProfileViewModel = viewModel(factory = ProfileViewModel.factory(app.apiService))
    val profileState by profileVm.state.collectAsState()
    val sessionXp by app.sessionXpToday.collectAsState()

    // Refresca daily_progress_xp del backend cada vez que el usuario vuelve a Home
    LaunchedEffect(Unit) { profileVm.load() }

    // Usa el mayor entre backend (incluye Web) y local optimista (XP ganado en esta sesión Android)
    val dailyXp = maxOf(profileState.dailyProgressXp, sessionXp)
    val dailyGoal = profileState.dailyGoal.coerceAtLeast(1)
    val dailyProgress = (dailyXp.toFloat() / dailyGoal.toFloat()).coerceIn(0f, 1f)

    val dracoPhrase = remember(dailyXp, dailyGoal) {
        getDracoMotivationalPhrase(dailyXp, dailyGoal)
    }

    val gradientBackground = Brush.verticalGradient(
        listOf(Color(0xFF0B132B), Color(0xFF1C2541))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBackground)
            .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {
        // Scroll
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()), // ← aquí está la clave
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // CARD INICIAL
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(Color(0xFF0F1A2A)),
                border = BorderStroke(2.dp, Color(0xFF22DDF2))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(20.dp)
                ) {
                    val selectedVideo = remember(dailyProgress) {
                        if (dailyProgress > 0.5f) {
                            listOf(R.raw.dracoidle, R.raw.dracochilltomandocafe).random()
                        } else {
                            listOf(R.raw.dracotriste, R.raw.dracotristongo).random()
                        }
                    }

                    DracoVideo(videoRes = selectedVideo)

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "¡Bienvenido de nuevo! 🎉",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = dracoPhrase,
                        color = Color(0xFFBBD8F4),
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // ---------- OBJETIVO DIARIO ----------
            Text("Objetivo Diario", color = Color(0xFF22DDF2), fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = dailyProgress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(12.dp)),
                color = Color(0xFF22DDF2)
            )
            Spacer(Modifier.height(4.dp))
            Text("$dailyXp / $dailyGoal XP hoy", color = Color.White, fontSize = 13.sp)

            Spacer(Modifier.height(30.dp))

            // ---------- BOTONES ----------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MejorButton("ESTUDIAR", R.drawable.ic_study, Color(0xFF1F0980)) {
                    navController.navigate(BottomNavItem.Pomodoro.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }

                MejorButton("LECCIONES", R.drawable.ic_book, Color(0xFF22DDF2)) {
                    navController.navigate("lecciones") {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }

            Spacer(Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MejorButton("MUSEO", R.drawable.ic_museum, Color(0xFF22DDF2)) {
                    navController.navigate("museo_dracarte") {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }

                MejorButton("AVANCES", R.drawable.ic_calendar, Color(0xFF1F0980)) {
                    navController.navigate(BottomNavItem.Avances.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }

            Spacer(Modifier.height(30.dp)) // margen final para que no quede pegado al borde
        }
    }
}

fun getDracoMotivationalPhrase(dailyXp: Int, dailyGoal: Int): String {
    val safeGoal = dailyGoal.coerceAtLeast(1)
    val progress = (dailyXp.toFloat() / safeGoal.toFloat()).coerceIn(0f, 1f)

    val noXpPhrases = listOf(
        "Draco está un poco triste… todavía no has hecho ninguna lección hoy. Haz una y ayúdalo a recuperar energía 🐉",
        "Draco te está esperando con sus gafitas puestas. Una lección puede animarlo mucho 📚",
        "Hoy Draco aún no ha recibido energía de estudio. ¿Lo ayudas completando una lección?",
        "Draco está algo dormido porque todavía no has estudiado hoy. ¡Despiértalo con una lección!",
        "Tu compañero Draco necesita un poco de XP para sonreír. Empieza con una lección corta.",
        "Draco mira su museo mágico y nota que falta energía hoy. ¡Haz una lección y dale vida!",
        "Aún no empieza la aventura de hoy. Draco confía en que puedes dar el primer paso.",
        "Draco está esperando tu primera misión del día. Una lección es suficiente para comenzar."
    )

    val lowXpPhrases = listOf(
        "¡Bien! Draco ya empezó a recuperar energía. Sigue con otra lección para hacerlo más fuerte ⚡",
        "Draco ya se está animando. Cada XP cuenta en esta aventura.",
        "Vas comenzando muy bien. Draco siente que hoy puede ser un gran día.",
        "La energía de Draco está despertando. Continúa y verás cómo sube tu progreso.",
        "Buen inicio. Una lección más y Draco estará todavía más motivado.",
        "Draco ya notó tu esfuerzo. Sigue avanzando poco a poco.",
        "¡Primeros pasos completados! Ahora sigue alimentando la energía de Draco.",
        "La misión de hoy ya empezó. Draco camina contigo en este reto."
    )

    val halfXpPhrases = listOf(
        "¡Muy bien! Draco ya tiene más de la mitad de su energía diaria. Sigue así 🐉✨",
        "Draco está orgulloso de ti. Ya pasaste la mitad del camino.",
        "Vas excelente. Estás muy cerca de completar la misión del día.",
        "Draco está feliz: tu constancia está dando resultados.",
        "¡Qué buen avance! Solo falta un poco más para que Draco complete su energía.",
        "Draco ya está celebrando tu progreso. No te detengas ahora.",
        "Estás en la mejor parte de la misión. Un último esfuerzo y lo logras.",
        "Draco siente que hoy será un día exitoso. Sigue sumando XP."
    )

    val completedPhrases = listOf(
        "¡Felicidades! Completaste tu meta diaria. Hoy Draco podrá dormir tranquilo 😴🐉",
        "Misión cumplida. Draco está feliz y orgulloso de tu esfuerzo.",
        "¡Excelente trabajo! Hoy le diste a Draco toda la energía que necesitaba.",
        "Draco puede descansar feliz: cumpliste tu objetivo diario.",
        "¡Meta alcanzada! Tu esfuerzo de hoy hizo sonreír a Draco.",
        "Draco está celebrando contigo. Hoy completaste la aventura diaria.",
        "¡Lo lograste! Draco cerrará el día con energía completa.",
        "Hoy Draco duerme tranquilo porque tú cumpliste tu misión de aprendizaje."
    )

    return when {
        dailyXp <= 0 -> noXpPhrases.random()
        progress < 0.5f -> lowXpPhrases.random()
        progress < 1f -> halfXpPhrases.random()
        else -> completedPhrases.random()
    }
}
@Composable
fun MejorButton(text: String, icon: Int, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .width(170.dp)
            .height(70.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }
    }
}
@Composable
fun DracoVideo(videoRes: Int) {
    val context = androidx.compose.ui.platform.LocalContext.current

    val exoPlayer = remember(videoRes) {
        ExoPlayer.Builder(context).build().apply {
            val uri = Uri.parse("android.resource://${context.packageName}/$videoRes")
            setMediaItem(MediaItem.fromUri(uri))
            repeatMode = Player.REPEAT_MODE_ONE
            prepare()
            playWhenReady = true
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    AndroidView(
        factory = {
            PlayerView(it).apply {
                player = exoPlayer
                useController = false
            }
        },
        modifier = Modifier.size(180.dp)
    )
}