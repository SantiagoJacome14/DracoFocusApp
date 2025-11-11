package co.edu.unab.dracofocusapp.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.unab.dracofocusapp.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    onBackToLogin: () -> Unit
) {
    val auth = Firebase.auth
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }

    val gradientBackground = Brush.verticalGradient(
        listOf(Color(0xFF0B132B), Color(0xFF1C2541))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBackground)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        // ✅ Scroll activado
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            Image(
                painter = painterResource(id = R.drawable.dragon_dracofocus1),
                contentDescription = "Mascota Draco",
                modifier = Modifier.size(140.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Recupera tu acceso",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Te ayudaremos a restablecer tu contraseña",
                color = Color(0xFFB0BEC5),
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF22DDF2), shape = MaterialTheme.shapes.medium)
                    .background(Color(0xFF0F1A2A), MaterialTheme.shapes.medium)
                    .padding(20.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CustomRegisterField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Correo electrónico",
                        icon = R.drawable.ic_email,
                        keyboardType = KeyboardType.Email
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (email.isBlank()) {
                                message = "Por favor ingresa tu correo electrónico."
                                return@Button
                            }

                            isLoading = true
                            message = null

                            auth.sendPasswordResetEmail(email.trim())
                                .addOnCompleteListener { task ->
                                    isLoading = false
                                    message = if (task.isSuccessful) {
                                        "Correo de recuperación enviado. Revisa tu bandeja."
                                    } else {
                                        task.exception?.message ?: "Error al enviar el correo."
                                    }
                                }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF22DDF2),
                            contentColor = Color.Black
                        )
                    ) {
                        if (isLoading)
                            CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(20.dp))
                        else
                            Text("Enviar correo", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    message?.let {
                        Text(
                            text = it,
                            color = if (it.startsWith("Correo")) Color(0xFF22DDF2) else Color.Red,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Volver al Login",
                        color = Color(0xFF22DDF2),
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable { onBackToLogin() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
