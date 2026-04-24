package co.edu.unab.dracofocusapp.ui.Lecciones

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import co.edu.unab.dracofocusapp.data.guardarRespuestaLeccion

@Composable
fun RespuestaLeccionesScreen(onBack: () -> Unit) {
    var codigoUsuario by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Responde la lección",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = codigoUsuario,
                onValueChange = { codigoUsuario = it },
                label = { Text("Escribe tu código aquí") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (codigoUsuario.isNotBlank()) {
                        isLoading = true
                        // 🔥 Guardar en Firebase
                        guardarRespuestaLeccion(codigoUsuario, "leccion_id_demo")
                        isLoading = false
                        mensaje = "✅ Respuesta enviada correctamente"
                    } else {
                        mensaje = "⚠️ Escribe tu respuesta antes de enviar"
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8A78FF))
            ) {
                Text("Enviar respuesta", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
            ) {
                Text("Volver", color = Color.White)
            }

            if (mensaje.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = mensaje,
                    color = if (mensaje.contains("✅")) Color(0xFF00FFAA) else Color.Red,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator(color = Color(0xFF8A78FF))
            }
        }
    }
}

