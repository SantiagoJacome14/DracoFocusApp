package co.edu.unab.dracofocusapp.ui.Lecciones.Solitario

import android.net.Uri
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
import androidx.compose.runtime.saveable.rememberSaveable

@Composable
fun LeccionDecisionesDeFuegoScreen(
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

    // ✅ LISTENER FIRESTORE PARA SABER CUANDO LA IA RESPONDE
    DisposableEffect(Unit) {
        val listenerRegistration = db.collection("feedback_lecciones")
            .whereEqualTo("user_id", userId)
            .whereEqualTo("leccion_id", "decisiones_de_fuego")
            .addSnapshotListener { snapshots, e ->
                if (e != null || snapshots == null || yaNavego) return@addSnapshotListener

                for (doc in snapshots.documents) {
                    val feedback = doc.getString("feedback_ai") ?: ""
                    if (feedback.isNotBlank()) {
                        yaNavego = true
                        feedbackViewModel.retroalimentacion.value = feedback

                        // ✅ Marca la lección como completa al regresar
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("leccion_completada", 0)

                        scope.launch {
                            delay(300)
                            navController.navigate("feedback_screen") {
                                popUpTo("leccion_decisiones_de_fuego") { inclusive = true }
                            }
                        }
                    }
                }
            }

        onDispose {
            listenerRegistration.remove()
        }
    }

    Scaffold(bottomBar = {}) { innerPadding ->
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

                //  Encabezado
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "LECCIÓN",
                            color = Color(0xFF57F5ED),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "DECISIONES DE FUEGO",
                            color = Color(0xFF57F5ED),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                }

                Text(
                    text = "Estructuras de control: Condicionales",
                    color = Color(0xFFCDF4F2),
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // OBJETIVO DE LA LECCIÓN
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F1A2A), RoundedCornerShape(12.dp))
                        .border(3.dp, Color(0xFF57F5ED), RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "OBJETIVO: ",
                            color = Color(0xFFCDF4F2),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Aplicar 'if, elif, else' para decidir qué hará Draco según su nivel de energía.",
                            color = Color(0xFFCBC8C8),
                            fontSize = 14.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // EJERCICIO
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
                            text = "Draco tiene niveles de energía. Si su energía es menor a 30, debe descansar; si está entre 30 y 70, puede estudiar; si es mayor de 70, puede entrenar vuelo.\n\nCrea un programa que muestre lo que debe hacer Draco según su energía.",
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
                                popUpTo("leccion_decisiones_de_fuego") { inclusive = true }
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
                                            "leccion_id" to "decisiones_de_fuego",
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

            // ✅ LOADING
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
                            text = "Draco está revisando tu código...",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}