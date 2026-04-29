package co.edu.unab.dracofocusapp.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import co.edu.unab.dracofocusapp.DracoFocusApplication
import co.edu.unab.dracofocusapp.R
import co.edu.unab.dracofocusapp.museum.MuseumCatalog

@Composable
fun MuseoDracoArteScreen(onBack: () -> Unit) {
    val app = LocalContext.current.applicationContext as DracoFocusApplication

    val unlockedIds by app.lessonProgressRepository.observeUnlockedPieceIds().collectAsState(emptySet())
    val collectionPct by app.lessonProgressRepository.observeMuseumCollectionProgressFraction()
        .collectAsState(initial = 0f)

    val piezas = MuseumCatalog.ALL_PIECES.map { piece ->
        piezaUi(piece, bloqueada = piece.catalogId !in unlockedIds)
    }

    val dracoCyan = Color(0xFF22DDF2)
    val progressAnimated by animateFloatAsState(
        targetValue = collectionPct.coerceIn(0f, 1f),
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
                    Text("Colección", color = Color.White, fontSize = 12.sp)
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

private data class PiezaMuseo(
    val id: String,
    val titulo: String,
    val imagenRes: Int,
    val bloqueada: Boolean,
)

private fun piezaUi(piece: MuseumCatalog.Piece, bloqueada: Boolean) =
    PiezaMuseo(piece.catalogId, piece.title, piece.imageResId, bloqueada)

@Composable
private fun PiezaCard(pieza: PiezaMuseo) {
    val borderColor by animateColorAsState(
        if (pieza.bloqueada) Color.Gray else Color(0xFF22DDF2).copy(alpha = 0.85f),
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
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
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
                        .alpha(if (pieza.bloqueada) 0.35f else 1f),
                    contentScale = ContentScale.Crop,
                )

                Text(
                    pieza.titulo,
                    color = if (pieza.bloqueada) Color.LightGray.copy(alpha = 0.7f) else Color.White,
                    modifier = Modifier.padding(12.dp),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            if (pieza.bloqueada) {
                Surface(
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(26.dp),
                    modifier = Modifier.size(52.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Bloqueado",
                        tint = Color.White,
                        modifier = Modifier.padding(12.dp),
                    )
                }
            }
        }
    }
}
