package co.edu.unab.dracofocusapp.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import co.edu.unab.dracofocusapp.DracoFocusApplication
import co.edu.unab.dracofocusapp.R
import co.edu.unab.dracofocusapp.domain.rewards.RewardManager
import co.edu.unab.dracofocusapp.museum.MuseumCatalog
import kotlinx.coroutines.launch

@Composable
fun MuseoDracoArteScreen(onBack: () -> Unit) {
    val app = LocalContext.current.applicationContext as DracoFocusApplication
    val currentUserId by app.tokenManager.userId.collectAsState(initial = null)
    val scope = rememberCoroutineScope()

    if (currentUserId == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFF22DDF2))
        }
        return
    }

    val uid = currentUserId!!

    // Source of truth: museum_unlocks table (updated by RewardManager)
    val unlockedIds by app.lessonProgressRepository
        .observeUnlockedPieceIds(uid)
        .collectAsState(initial = emptySet())

    val collectionPct = (unlockedIds.size.toFloat() /
            MuseumCatalog.ALL_PIECES.size.coerceAtLeast(1)).coerceIn(0f, 1f)

    // Refresh from backend + grant pending rewards when app returns from foreground
    val activity = LocalContext.current as? androidx.activity.ComponentActivity
    DisposableEffect(activity) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                scope.launch {
                    app.lessonProgressRepository.syncProgressFromServer(uid)
                    grantPendingRewards(app, uid)
                }
            }
        }
        activity?.lifecycle?.addObserver(observer)
        onDispose { activity?.lifecycle?.removeObserver(observer) }
    }

    // Initial sync + pending reward grant on screen entry
    LaunchedEffect(uid) {
        app.lessonProgressRepository.syncProgressFromServer(uid)
        grantPendingRewards(app, uid)
    }

    val piezas = MuseumCatalog.ALL_PIECES.map { piece ->
        PiezaMuseo(
            id = piece.catalogId,
            titulo = piece.title,
            imagenRes = piece.imageResId,
            bloqueada = piece.catalogId !in unlockedIds,
        )
    }

    val dracoCyan = Color(0xFF22DDF2)
    val progressAnimated by animateFloatAsState(
        targetValue = collectionPct,
        animationSpec = tween(durationMillis = 900),
        label = "museogrow",
    )

    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF0B132B), Color(0xFF1C2541))))) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(painterResource(id = R.drawable.ic_navegacion), contentDescription = "Volver", tint = Color.White)
                }
                Text("EL GRAN MUSEO", color = dracoCyan, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        "${unlockedIds.size} / ${MuseumCatalog.ALL_PIECES.size} piezas",
                        color = Color.White,
                        fontSize = 12.sp,
                    )
                    Text(
                        "${(progressAnimated * 100).toInt()}%",
                        color = dracoCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { progressAnimated },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = dracoCyan,
                    trackColor = Color(0xFF1C2541),
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalItemSpacing = 12.dp,
            ) {
                items(piezas, key = { it.id }) { pieza ->
                    PiezaCard(pieza)
                }
            }
        }
    }
}

/** Grants museum rewards for any completed lesson that hasn't been claimed yet (Web completions). */
private suspend fun grantPendingRewards(app: DracoFocusApplication, userId: String) {
    val unclaimed = app.lessonProgressRepository.getUnclaimedCompletedSlugs(userId)
    unclaimed.forEach { slug ->
        app.rewardManager.grantLessonCompletionReward(userId, slug)
    }
}

private data class PiezaMuseo(
    val id: String,
    val titulo: String,
    val imagenRes: Int,
    val bloqueada: Boolean,
)

@Composable
private fun PiezaCard(pieza: PiezaMuseo) {
    val borderColor by animateColorAsState(
        if (pieza.bloqueada) Color(0xFF2A3A4A) else Color(0xFF22DDF2).copy(alpha = 0.85f),
        label = "bordePieza",
    )
    val scale by animateFloatAsState(
        targetValue = if (pieza.bloqueada) 0.93f else 1f,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = Spring.StiffnessMediumLow),
        label = "scalePieza",
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { scaleX = scale; scaleY = scale },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1A2A)),
        border = BorderStroke(1.dp, borderColor),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Column {
                Image(
                    painter = painterResource(id = pieza.imagenRes),
                    contentDescription = pieza.titulo,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .alpha(if (pieza.bloqueada) 0.2f else 1f),
                    contentScale = ContentScale.Crop,
                )
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                    Text(
                        pieza.titulo,
                        color = if (pieza.bloqueada) Color(0xFF3D4E60) else Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = if (pieza.bloqueada) "Completa lecciones para desbloquear" else "Desbloqueada ✓",
                        color = if (pieza.bloqueada) Color(0xFF2D3E50) else Color(0xFF22DDF2).copy(alpha = 0.8f),
                        fontSize = 10.sp,
                    )
                }
            }

            if (pieza.bloqueada) {
                Surface(
                    color = Color.Black.copy(alpha = 0.55f),
                    shape = RoundedCornerShape(26.dp),
                    modifier = Modifier.size(48.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Bloqueado",
                        tint = Color(0xFF4A5A6A),
                        modifier = Modifier.padding(12.dp),
                    )
                }
            }
        }
    }
}
