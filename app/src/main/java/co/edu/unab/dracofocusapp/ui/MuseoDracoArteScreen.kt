package co.edu.unab.dracofocusapp.ui

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.unab.dracofocusapp.R

data class PiezaMuseo(val id: String, val titulo: String, val imagenRes: Int, val bloqueada: Boolean)

@Composable
fun MuseoDracoArteScreen(onBack: () -> Unit) {
    // Simulación de piezas (esto vendría de Room o DataStore)
    val piezas = listOf(
        PiezaMuseo("1", "El Despertar", R.drawable.dragon_dracofocus1, false),
        PiezaMuseo("2", "Vuelo de Fuego", R.drawable.dragon_dracofocus1, true),
        PiezaMuseo("3", "Código Ancestral", R.drawable.dragon_dracofocus1, true),
        PiezaMuseo("4", "La Cueva de Bits", R.drawable.dragon_dracofocus1, false)
    )

    val dracoCyan = Color(0xFF22DDF2)

    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF0B132B), Color(0xFF1C2541))))) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(painterResource(id = R.drawable.ic_navegacion), contentDescription = "Volver", tint = Color.White)
                }
                Text("EL GRAN MUSEO", color = dracoCyan, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Barra de Progreso Personalizada (UX Espectacular)
            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Colección", color = Color.White, fontSize = 12.sp)
                    Text("50%", color = dracoCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = 0.5f,
                    modifier = Modifier.fillMaxWidth().height(8.dp).background(Color.DarkGray, RoundedCornerShape(4.dp)),
                    color = dracoCyan,
                    trackColor = Color.Transparent
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalItemSpacing = 12.dp
            ) {
                items(piezas) { pieza ->
                    PiezaCard(pieza)
                }
            }
        }
    }
}

@Composable
fun PiezaCard(pieza: PiezaMuseo) {
    val alpha = if (pieza.bloqueada) 0.3f else 1f
    
    Card(
        modifier = Modifier.fillMaxWidth().alpha(alpha),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1A2A)),
        border = BorderStroke(1.dp, if(pieza.bloqueada) Color.Gray else Color(0xFF22DDF2).copy(alpha = 0.5f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Column {
                Image(
                    painter = painterResource(id = pieza.imagenRes),
                    contentDescription = pieza.titulo,
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    contentScale = ContentScale.Crop
                )
                Text(
                    pieza.titulo,
                    color = Color.White,
                    modifier = Modifier.padding(12.dp),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            if (pieza.bloqueada) {
                Surface(
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Bloqueado",
                        tint = Color.White,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}
