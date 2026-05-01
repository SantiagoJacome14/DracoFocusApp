package co.edu.unab.dracofocusapp.ui.Lecciones.Solitario

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import co.edu.unab.dracofocusapp.api.IAFeedbackManager
import co.edu.unab.dracofocusapp.ui.Lecciones.LeccionRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LeccionDecisionesDeFuegoScreen(
    navController: NavController,
    onBack: () -> Unit
) {
    val leccion = LeccionRepository.getLeccionById("1") ?: return
    var piezasDisponibles by remember { mutableStateOf(leccion.piezasDisponibles.shuffled()) }
    var piezasSolucion by remember { mutableStateOf(listOf<String>()) }
    
    var isLoading by remember { mutableStateOf(false) }
    var feedbackText by remember { mutableStateOf<String?>(null) }
    var esExitoso by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()
    val db = FirebaseFirestore.getInstance()
    val iaManager = remember { IAFeedbackManager() }
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "anonimo"

    val dracoCyan = Color(0xFF22DDF2)

    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF0B132B), Color(0xFF1C2541))))) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(leccion.titulo.uppercase(), color = dracoCyan, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(leccion.contexto, color = Color.White, fontSize = 16.sp, textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(24.dp))

            // ÁREA DE SOLUCIÓN
            Box(
                modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 120.dp)
                    .background(Color(0xFF0F1A2A), RoundedCornerShape(12.dp))
                    .border(2.dp, dracoCyan, RoundedCornerShape(12.dp)).padding(12.dp)
            ) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    piezasSolucion.forEach { pieza ->
                        CodigoPill(pieza) {
                            piezasSolucion = piezasSolucion - pieza
                            piezasDisponibles = piezasDisponibles + pieza
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // PIEZAS DISPONIBLES
            FlowRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                piezasDisponibles.forEach { pieza ->
                    CodigoPill(pieza, color = Color(0xFF1C2541)) {
                        piezasDisponibles = piezasDisponibles - pieza
                        piezasSolucion = piezasSolucion + pieza
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // BOTONES
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f).height(50.dp), border = BorderStroke(1.dp, dracoCyan)) {
                    Text("REGRESAR", color = dracoCyan)
                }

                Button(
                    onClick = {
                        if (piezasSolucion.isNotEmpty()) {
                            isLoading = true
                            val codigoArmado = piezasSolucion.joinToString(" ")
                            val correcto = (piezasSolucion == leccion.solucionCorrecta)
                            
                            scope.launch {
                                val feedback = iaManager.generarFeedback(leccion, codigoArmado, correcto)
                                feedbackText = feedback
                                esExitoso = correcto
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier.weight(1f).height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = dracoCyan, contentColor = Color.Black)
                ) { Text("ENVIAR", fontWeight = FontWeight.Bold) }
            }
        }

        // DIÁLOGO DE FEEDBACK IA
        if (feedbackText != null) {
            AlertDialog(
                onDismissRequest = { feedbackText = null },
                containerColor = Color(0xFF0F1A2A),
                title = { Text(if(esExitoso) "¡Draco está feliz!" else "Draco te da una pista", color = dracoCyan) },
                text = { Text(feedbackText!!, color = Color.White) },
                confirmButton = {
                    TextButton(onClick = { 
                        feedbackText = null 
                        if(esExitoso) onBack()
                    }) {
                        Text("ENTENDIDO", color = dracoCyan, fontWeight = FontWeight.Bold)
                    }
                },
                shape = RoundedCornerShape(16.dp)
            )
        }

        if (isLoading) {
            Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.7f)), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = dracoCyan)
            }
        }
    }
}

@Composable
fun CodigoPill(text: String, color: Color = Color(0xFF22DDF2), onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = color,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color(0xFF22DDF2).copy(alpha = 0.5f))
    ) {
        Text(
            text = text,
            color = if (color == Color(0xFF22DDF2)) Color.Black else Color.White,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
