package co.edu.unab.dracofocusapp.ui.Lecciones.Grupales

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LeccionesGrupalesScreen(
    navController: NavController,
    onBack: () -> Unit,
    sesionId: String = "sesion_demo_1" // ID de la sesión cooperativa
) {
    val database = FirebaseDatabase.getInstance().getReference("sesiones_grupales/$sesionId")
    
    // Estado del compañero (Jugador B)
    var progresoCompanero by remember { mutableStateOf(listOf<String>()) }
    
    // Escuchar cambios del compañero en tiempo real
    LaunchedEffect(Unit) {
        database.child("jugador2_progreso").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val progreso = snapshot.children.mapNotNull { it.value.toString() }
                progresoCompanero = progreso
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    val dracoCyan = Color(0xFF22DDF2)

    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF0B132B), Color(0xFF1C2541))))) {
        Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
            Text("MODO COOPERATIVO", color = dracoCyan, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("Sesión: $sesionId", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)

            Spacer(modifier = Modifier.height(30.dp))

            // ÁREA DEL COMPAÑERO (Visualización "Fantasma")
            Text("PROGRESO DE TU COMPAÑERO:", color = Color.Gray, fontSize = 12.sp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    progresoCompanero.forEach { pieza ->
                        GhostPill(pieza)
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
            
            // Aquí iría el área de juego propia (similar a la solitaria)
            // que al mover piezas ejecute: database.child("jugador1_progreso").setValue(misPiezas)
            
            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = dracoCyan)
            ) {
                Text("SALIR DE LA SESIÓN", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun GhostPill(text: String) {
    val alphaAnim by animateFloatAsState(targetValue = 0.5f)
    Surface(
        color = Color(0xFF22DDF2).copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.alpha(alphaAnim)
    ) {
        Text(
            text = text,
            color = Color(0xFF22DDF2),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            fontSize = 12.sp
        )
    }
}
