package co.edu.unab.dracofocusapp.ui.Avances

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
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
import co.edu.unab.dracofocusapp.data.remote.BadgeDto
import co.edu.unab.dracofocusapp.data.remote.CompletedLessonStatsDto
import co.edu.unab.dracofocusapp.data.remote.UserStatsDto
import co.edu.unab.dracofocusapp.viewmodel.AvancesViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProgressScreen(navController: NavController) {
    val app = LocalContext.current.applicationContext as DracoFocusApplication
    val vm: AvancesViewModel = viewModel(factory = AvancesViewModel.factory(app.apiService))
    val uiState by vm.state.collectAsState()

    // Refresca cada vez que el usuario vuelve a esta pantalla
    LaunchedEffect(Unit) { vm.load() }

    val gradientBackground = Brush.verticalGradient(listOf(Color(0xFF0B132B), Color(0xFF1C2541)))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(8.dp))

        Image(
            painter = painterResource(id = R.drawable.dragon_dracofocus1),
            contentDescription = "Draco",
            modifier = Modifier.size(130.dp),
        )

        Spacer(Modifier.height(16.dp))

        when (val state = uiState) {
            is AvancesViewModel.UiState.Loading -> {
                Spacer(Modifier.height(40.dp))
                CircularProgressIndicator(color = Color(0xFF22DDF2))
                Spacer(Modifier.height(12.dp))
                Text("Cargando avances...", color = Color(0xFFB0BEC5), fontSize = 14.sp)
            }

            is AvancesViewModel.UiState.Error -> {
                Spacer(Modifier.height(20.dp))
                NeonCard {
                    Text(
                        text = state.message,
                        color = Color(0xFFFF5252),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = { vm.load() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22DDF2), contentColor = Color.Black),
                    ) {
                        Text("Reintentar", fontWeight = FontWeight.Bold)
                    }
                }
            }

            is AvancesViewModel.UiState.Success -> {
                AvancesContent(stats = state.stats)
            }
        }

        Spacer(Modifier.height(50.dp))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AvancesContent(stats: UserStatsDto) {
    // Título dinámico según progreso
    val titulo = when {
        stats.progressPercent == 100 -> "¡Completaste todas las lecciones!"
        stats.progressPercent >= 50 -> "¡Vas muy bien, sigue así!"
        stats.completedLessonsCount == 0 -> "Empieza tu primera lección"
        else -> "Buen progreso, continúa estudiando"
    }
    Text(
        text = titulo,
        color = Color.White,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
    )

    Spacer(Modifier.height(20.dp))

    // Fila de stats: XP | Nivel | Racha
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        MiniStatCard(value = "${stats.totalXp}", label = "XP Total", modifier = Modifier.weight(1f))
        MiniStatCard(value = "Niv. ${stats.level}", label = "Nivel", modifier = Modifier.weight(1f))
        MiniStatCard(value = "${stats.currentStreak}", label = "Racha", modifier = Modifier.weight(1f))
    }

    Spacer(Modifier.height(18.dp))

    // Fila: progreso circular | barra XP nivel
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        NeonCard(modifier = Modifier.weight(1f)) {
            Text(
                "Progreso Lecciones",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(10.dp))
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressWithText(stats.progressPercent / 100f)
            }
            Spacer(Modifier.height(8.dp))
            Text(
                "${stats.completedLessonsCount} de ${stats.totalLessonsCount} lecciones",
                color = Color(0xFFB0BEC5),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        NeonCard(modifier = Modifier.weight(1f)) {
            val xpInLevel = stats.chartData?.xpCurrentLevel ?: (stats.totalXp % 200)
            val xpNext = (stats.chartData?.xpNextLevel ?: 200).coerceAtLeast(1)
            Text(
                "Niv. ${stats.level} → ${stats.level + 1}",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(10.dp))
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressWithText(xpInLevel.toFloat() / xpNext.toFloat())
            }
            Spacer(Modifier.height(8.dp))
            Text(
                "$xpInLevel / $xpNext XP",
                color = Color(0xFFB0BEC5),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }

    // Insignias
    Spacer(Modifier.height(18.dp))
    NeonCard {
        Text("Insignias", color = Color(0xFF22DDF2), fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(Modifier.height(10.dp))
        if (stats.badges.isEmpty()) {
            Text(
                "Completa lecciones para desbloquear insignias",
                color = Color(0xFFB0BEC5),
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        } else {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                stats.badges.forEach { badge -> BadgeChip(badge) }
            }
        }
    }

    // Actividad reciente
    if (stats.completedLessons.isNotEmpty()) {
        Spacer(Modifier.height(18.dp))
        NeonCard {
            Text("Actividad Reciente", color = Color(0xFF22DDF2), fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(10.dp))
            stats.completedLessons.take(4).forEachIndexed { index, lesson ->
                if (index > 0) {
                    HorizontalDivider(color = Color(0xFF22DDF2).copy(alpha = 0.15f), modifier = Modifier.padding(vertical = 6.dp))
                }
                RecentLessonRow(lesson)
            }
        }
    }
}

@Composable
private fun MiniStatCard(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .border(1.dp, Color(0xFF22DDF2), RoundedCornerShape(14.dp))
            .background(Color(0xFF0F1A2A), RoundedCornerShape(14.dp))
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(value, color = Color(0xFF22DDF2), fontWeight = FontWeight.Bold, fontSize = 16.sp, textAlign = TextAlign.Center)
        Spacer(Modifier.height(2.dp))
        Text(label, color = Color(0xFFB0BEC5), fontSize = 11.sp, textAlign = TextAlign.Center)
    }
}

@Composable
private fun BadgeChip(badge: BadgeDto) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF22DDF2).copy(alpha = 0.12f),
        border = ButtonDefaults.outlinedButtonBorder,
        tonalElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = Color(0xFF22DDF2),
                modifier = Modifier.size(14.dp),
            )
            Text(badge.title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun RecentLessonRow(lesson: CompletedLessonStatsDto) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Color(0xFF22DDF2),
            modifier = Modifier.size(20.dp),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = lesson.title ?: lesson.slug,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
            )
            if (lesson.completedAt != null) {
                Text(
                    text = formatDate(lesson.completedAt),
                    color = Color(0xFFB0BEC5),
                    fontSize = 11.sp,
                )
            }
        }
        if (lesson.xpReward > 0) {
            Text(
                text = "+${lesson.xpReward} XP",
                color = Color(0xFF22DDF2),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

// ---------- COMPONENTES COMPARTIDOS ---------- //

@Composable
fun NeonCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF22DDF2), RoundedCornerShape(18.dp))
            .background(Color(0xFF0F1A2A), RoundedCornerShape(18.dp))
            .padding(16.dp),
        content = content,
    )
}

@Composable
fun CircularProgressWithText(percentage: Float) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(110.dp),
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawArc(
                color = Color(0x33FFFFFF),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(16f),
            )
            drawArc(
                color = Color(0xFF22DDF2),
                startAngle = -90f,
                sweepAngle = percentage.coerceIn(0f, 1f) * 360f,
                useCenter = false,
                style = Stroke(16f),
            )
        }
        Text(
            text = "${(percentage.coerceIn(0f, 1f) * 100).toInt()}%",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
        )
    }
}

// Kept for future use
@Composable
fun BarChart(values: List<Int>) {
    Canvas(modifier = Modifier.height(60.dp).fillMaxWidth()) {
        val max = values.maxOrNull()?.coerceAtLeast(1) ?: 1
        val barWidth = size.width / (values.size * 2)
        values.forEachIndexed { index, value ->
            val height = (value.toFloat() / max) * size.height
            drawRect(
                color = Color(0xFF22DDF2),
                topLeft = Offset(index * barWidth * 2, size.height - height),
                size = Size(barWidth, height),
            )
        }
    }
}

private fun formatDate(isoString: String): String {
    return try {
        val datePart = isoString.take(10)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        val date = sdf.parse(datePart) ?: return "Completada"
        SimpleDateFormat("dd MMM yyyy", Locale("es")).format(date)
    } catch (e: Exception) {
        "Completada"
    }
}
