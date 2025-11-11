package co.edu.unab.dracofocusapp.ui.Lecciones.Solitario

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import co.edu.unab.dracofocusapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel
import co.edu.unab.dracofocusapp.viewmodel.FeedbackViewModel

@Composable
fun LeccionElLibroDeTareasScreen(
    navController: NavController,
    onBack: () -> Unit
) {

    val gradientBackground = Brush.verticalGradient(
        listOf(Color(0xFF0B132B), Color(0xFF1C2541))
    )

    var codigoUsuario by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val db = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "desconocido"
    var yaNavego by rememberSaveable { mutableStateOf(false) }

    val feedbackViewModel: FeedbackViewModel = viewModel()

    // ✅ Escuchar respuesta de la IA
    DisposableEffect(Unit) {
        val listener = db.collection("feedback_lecciones")
            .whereEqualTo("user_id", userId)
            .whereEqualTo("leccion_id", "el_libro_de_tareas")
            .addSnapshotListener { snapshots, e ->
                if (e != null || snapshots == null || yaNavego) return@addSnapshotListener

                for (doc in snapshots.documents) {
                    val feedback = doc.getString("feedback_ai") ?: ""
                    if (feedback.isNotBlank()) {
                        yaNavego = true
                        feedbackViewModel.retroalimentacion.value = feedback

                        // ✅ Marcar como completada
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("leccion_completada", 3)

                        scope.launch {
                            delay(300)
                            navController.navigate("feedback_screen") {
                                popUpTo("leccion_el_libro_de_tareas") { inclusive = true }
                            }
                        }
                    }
                }
            }

        onDispose { listener.remove() }
    }

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBackground)
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {

                // ✅ ENCABEZADO
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Lección\nEL LIBRO DE LAS TAREAS",
                        color = Color(0xFF57F5ED),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Image(
                        painter = painterResource(id = R.drawable.img_dr),
                        contentDescription = "Dragón escribiendo",
                        modifier = Modifier.size(80.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Listas y recorridos",
                    color = Color(0xFFCDF4F2),
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ✅ OBJETIVO
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F1A2A), RoundedCornerShape(12.dp))
                        .border(3.dp, Color(0xFF57F5ED), RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "OBJETIVO: ",
                            color = Color(0xFFCDF4F2),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Almacenar tareas y recorrerlas con ciclos.",
                            color = Color(0xFFCBC8C8),
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ✅ EJERCICIO
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F1A2A), RoundedCornerShape(12.dp))
                        .border(3.dp, Color(0xFF57F5ED), RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Text(
                            text = "EJERCICIO",
                            color = Color(0xFFCDF4F2),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Crea una lista con las tareas de Draco:\n['estudiar', 'volar', 'descansar', 'tomar café'].\nMuestra cada tarea con su número de orden.",
                            color = Color(0xFFCBC8C8),
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = codigoUsuario,
                            onValueChange = { codigoUsuario = it },
                            label = { Text("#Escribe tu código aquí") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            textStyle = LocalTextStyle.current.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 14.sp,
                                color = Color.Black
                            ),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFEBFFFE),
                                unfocusedContainerColor = Color(0xFFEBFFFE),
                                focusedIndicatorColor = Color(0xFF57F5ED),
                                unfocusedIndicatorColor = Color(0xFF57F5ED),
                                cursorColor = Color(0xFF0F2B5D)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                // ✅ BOTONES
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            navController.navigate("lecciones_solitario") {
                                popUpTo("leccion_el_libro_de_tareas") { inclusive = true }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF22DDF2),
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 10.dp)
                    ) {
                        Text("Regresar", color = Color(0xFFEBFFFE))
                    }

                    Button(
                        onClick = {
                            if (codigoUsuario.isNotBlank()) {
                                isLoading = true
                                scope.launch {
                                    try {
                                        val respuesta = hashMapOf(
                                            "user_id" to userId,
                                            "leccion_id" to "el_libro_de_tareas",
                                            "codigo" to codigoUsuario,
                                            "estado" to "pendiente",
                                            "timestamp" to System.currentTimeMillis()
                                        )

                                        db.collection("respuestas_pendientes")
                                            .add(respuesta)
                                            .addOnSuccessListener {
                                                Log.d("Firebase", "✅ Enviado correctamente")
                                            }
                                            .addOnFailureListener { e ->
                                                Log.e("Firebase", "❌ Error: ${e.message}")
                                            }
                                    } catch (e: Exception) {
                                        Log.e("Envio", "Error: ${e.message}")
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            } else {
                                Log.w("API", "⚠ Código vacío, no se envió.")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF22DDF2),
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Enviar", color = Color(0xFFEBFFFE))
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // ✅ LOADER
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xAA000000)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Color(0xFF22DDF2))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Draco está revisando tu código...",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}
