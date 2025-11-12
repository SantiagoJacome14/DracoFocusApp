package co.edu.unab.dracofocusapp.ui.Lecciones.Grupales

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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeccionGuardianesDelTesoroProgramadorScreen(
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

    // Snackbar gamificado
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
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
                            text = "GUARDIANES DEL TESORO",
                            color = Color(0xFF57F5ED),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Lección grupal 1 — Manejo de variables y condicionales",
                            color = Color(0xFFCDF4F2),
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                // OBJETIVO
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F1A2A), RoundedCornerShape(12.dp))
                        .border(3.dp, Color(0xFF57F5ED), RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Text(
                            text = "OBJETIVO",
                            color = Color(0xFFCDF4F2),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Draco custodia un cofre mágico que solo puede abrirse si el usuario ingresa correctamente una contraseña secreta. Si el intento falla tres veces, el cofre se bloquea.",
                            color = Color(0xFFCBC8C8),
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 💻 ROL PROGRAMADOR
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F1A2A), RoundedCornerShape(12.dp))
                        .border(3.dp, Color(0xFF57F5ED), RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Text(
                            text = "ROL: PROGRAMADOR",
                            color = Color(0xFFCDF4F2),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Implementa el código en Python aplicando estructuras if, while y contadores.",
                            color = Color(0xFFCBC8C8),
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // 🧾 Editor de código
                        OutlinedTextField(
                            value = codigoUsuario,
                            onValueChange = { codigoUsuario = it },
                            label = { Text("# Escribe tu código aquí") },
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

                // 🟩 Botones
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            navController.navigate("lecciones_grupales") {
                                popUpTo("leccion_guardianes_programador") { inclusive = true }
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
                                            "leccion_id" to "guardianes_tesoro_programador",
                                            "tipo" to "codigo_python",
                                            "contenido" to codigoUsuario,
                                            "estado" to "pendiente",
                                            "timestamp" to System.currentTimeMillis()
                                        )

                                        db.collection("entregas_grupales")
                                            .add(respuesta)
                                            .addOnSuccessListener {
                                                Log.d("Firebase", "Código enviado correctamente")

                                                // Actualizar estadísticas del usuario
                                                val userStatsRef = db.collection("usuarios_estadisticas").document(userId)
                                                userStatsRef.get()
                                                    .addOnSuccessListener { document ->
                                                        if (document.exists()) {
                                                            val actual = document.getLong("cantidad_lecciones_grupales_completadas") ?: 0
                                                            userStatsRef.update("cantidad_lecciones_grupales_completadas", actual + 1)
                                                        } else {
                                                            val newData = hashMapOf("cantidad_lecciones_grupales_completadas" to 1)
                                                            userStatsRef.set(newData)
                                                        }

                                                        // Mostrar Snackbar gamificado
                                                        scope.launch {
                                                            snackbarHostState.showSnackbar("🎉 Programa enviado, ¡Draco te felicita!")
                                                        }
                                                    }
                                            }
                                            .addOnFailureListener { e ->
                                                Log.e("Firebase", "Error: ${e.message}")
                                            }
                                    } catch (e: Exception) {
                                        Log.e("Envio", "Error: ${e.message}")
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            } else {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Escribe tu código antes de enviar.")
                                }
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

            // ⏳ Loading overlay
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
