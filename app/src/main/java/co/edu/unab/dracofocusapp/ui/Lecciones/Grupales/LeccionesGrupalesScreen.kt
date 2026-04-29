package co.edu.unab.dracofocusapp.ui.Lecciones.Grupales

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
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

private fun coopRoute(lessonId: String, roomId: String): String =
    "leccion_reto_coop/$lessonId/$roomId"

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LeccionesGrupalesScreen(
    navController: NavController,
    onBack: () -> Unit,
    sesionId: String = "sesion_demo_1",
) {
    val dracoCyan = Color(0xFF22DDF2)
    var roomCode by rememberSaveable { mutableStateOf(sesionId) }

    var progresoCompanero by remember { mutableStateOf(listOf<String>()) }
    var sharedFraction by remember { mutableStateOf(0f) }

    DisposableEffect(roomCode) {
        val firebase = FirebaseDatabase.getInstance()
        val legacyRef = firebase.getReference("sesiones_grupales/$roomCode/jugador2_progreso")
        val coopRef = firebase.getReference("lesson_coop_rooms/$roomCode/shared_progress/fundamentos_pct")

        val companionListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { child -> child.value?.toString() }.filter { it.isNotBlank() }
                if (list.isNotEmpty()) progresoCompanero = list
            }

            override fun onCancelled(error: DatabaseError) = Unit
        }

        val progressListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val frac = snapshot.getValue(Double::class.java)?.toFloat()
                    ?: snapshot.getValue(Float::class.java)
                    ?: 0f
                sharedFraction = frac.coerceIn(0f, 1f)
            }

            override fun onCancelled(error: DatabaseError) = Unit
        }

        legacyRef.addValueEventListener(companionListener)
        coopRef.addValueEventListener(progressListener)

        onDispose {
            legacyRef.removeEventListener(companionListener)
            coopRef.removeEventListener(progressListener)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0B132B), Color(0xFF1C2541)),
                ),
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
        ) {
            Text("SALA COOPERATIVA DRACO", color = dracoCyan, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("Comparte código con tu compañero y entrena el mismo bloque.", color = Color(0xFF8FA3BD), fontSize = 12.sp)

            Spacer(modifier = Modifier.height(18.dp))

            OutlinedTextField(
                value = roomCode,
                onValueChange = { nueva -> roomCode = if (nueva.isBlank()) sesionId else nueva.trim() },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Código de sala") },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = dracoCyan,
                    unfocusedIndicatorColor = dracoCyan.copy(alpha = 0.4f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF0F1A2A),
                    unfocusedContainerColor = Color(0xFF0F1A2A),
                ),
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                "Progreso compartido: ${(sharedFraction * 100).toInt()}%",
                color = Color.White,
                fontSize = 13.sp,
            )

            LinearProgressIndicator(
                progress = { sharedFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp, bottom = 12.dp)
                    .height(10.dp)
                    .clip(RoundedCornerShape(10.dp)),
                color = dracoCyan,
                trackColor = Color(0xFF1C2541),
            )

            Text("Señales de compañía (Realtime):", color = Color.Gray, fontSize = 11.sp)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(104.dp)
                    .background(Color.White.copy(alpha = 0.06f), RoundedCornerShape(14.dp))
                    .padding(10.dp),
            ) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    progresoCompanero.forEach { texto -> GhostPill(texto) }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            Text("Entrar en lección vinculada a la sala", color = dracoCyan, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            listOf(
                Triple("1", "Fundamentos • Lección 1", "Decisiones"),
                Triple("2", "Fundamentos • Lección 2", "Loops"),
                Triple("3", "Fundamentos • Lección 3", "Escudos"),
            ).forEach { (idLeccion, label, tema) ->
                Button(
                    onClick = {
                        navController.navigate(coopRoute(idLeccion, roomCode))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = dracoCyan, contentColor = Color.Black),
                ) {
                    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                        Text(label, fontWeight = FontWeight.Bold)
                        Text(tema, fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = dracoCyan),
                border = BorderStroke(1.dp, dracoCyan),
            ) {
                Text("VOLVER AL MENÚ", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun GhostPill(text: String) {
    val alphaAnim by animateFloatAsState(targetValue = 0.6f)
    Surface(
        color = Color(0xFF22DDF2).copy(alpha = 0.12f),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.alpha(alphaAnim),
    ) {
        Text(
            text = text,
            color = Color(0xFF22DDF2),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            fontSize = 12.sp,
        )
    }
}
