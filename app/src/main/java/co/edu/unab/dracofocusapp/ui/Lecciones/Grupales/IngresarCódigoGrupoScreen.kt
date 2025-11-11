package co.edu.unab.dracofocusapp.ui.Lecciones.Grupales

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.navigation.NavController
import co.edu.unab.dracofocusapp.auth.ModernTopBar
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString


@Composable
fun IngresarCodigoGrupoScreen(
    navController: NavController,
    onBack: () -> Unit
) {
    var codigo by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    val background = Brush.verticalGradient(
        listOf(Color(0xFF0B132B), Color(0xFF1C2541))
    )

    Scaffold(
        topBar = {
            ModernTopBar(
                title = "Conexión en Grupo",
                showBackButton = true,
                onBackClick = onBack
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(background)
                .padding(innerPadding)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                Text(
                    text = "Ingresa el código de tu compañero para estudiar juntos",
                    color = Color(0xFFB3B3B3),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )

                OutlinedTextField(
                    value = codigo,
                    onValueChange = {
                        codigo = it
                        error = false
                    },
                    label = { Text("Código de invitación") },
                    singleLine = true,
                    isError = error,
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color(0xFF22DDF2),
                        unfocusedIndicatorColor = Color.Gray,
                        cursorColor = Color(0xFF22DDF2),
                        focusedLabelColor = Color(0xFF22DDF2)
                    )
                )

                if (error) {
                    Text(
                        "Código incorrecto. Intenta de nuevo.",
                        color = Color(0xFFFF6B6B),
                        fontSize = 13.sp
                    )
                }

                Button(
                    onClick = {
                        if (codigo.trim().length >= 4) {
                            navController.navigate("lecciones_grupales")
                        } else {
                            error = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF22DDF2),
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.fillMaxWidth(0.7f)
                ) {
                    Text("Validar Código", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                var mostrarDialogo by remember { mutableStateOf(false) }
                var codigoGenerado by remember { mutableStateOf("") }

                TextButton(
                    onClick = {
                        error = false
                        codigoGenerado = (1000..9999).random().toString()
                        mostrarDialogo = true
                    }
                ) {
                    Text(
                        "No tengo código, generar uno",
                        color = Color(0xFF22DDF2),
                        fontWeight = FontWeight.Bold
                    )
                }

                val clipboardManager = LocalClipboardManager.current

                if (mostrarDialogo) {
                    AlertDialog(
                        onDismissRequest = { mostrarDialogo = false },
                        title = { Text("Código Generado") },
                        text = { Text("Tu código es: $codigoGenerado\nCompártelo con tu compañero") },
                        confirmButton = {
                            Row {
                                TextButton(onClick = {
                                    clipboardManager.setText(AnnotatedString(codigoGenerado))
                                }) {
                                    Text("Copiar")
                                }
                                Spacer(Modifier.width(8.dp))
                                TextButton(onClick = { mostrarDialogo = false }) {
                                    Text("Cerrar")
                                }
                            }
                        }
                    )
                }

            }
        }
    }
}