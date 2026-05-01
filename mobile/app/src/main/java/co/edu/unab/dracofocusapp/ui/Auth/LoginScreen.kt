package co.edu.unab.dracofocusapp.ui.Auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.unab.dracofocusapp.R
import co.edu.unab.dracofocusapp.auth.CustomTextField

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onNavigateToMain: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit
) {
    val state = viewModel.state
    val dracoCyan = Color(0xFF22DDF2)

    // Efecto para navegar cuando el login es exitoso
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onNavigateToMain()
        }
    }

    val gradientBackground = Brush.verticalGradient(
        listOf(Color(0xFF0B132B), Color(0xFF1C2541))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.dragon_dracofocus1),
                contentDescription = "Mascota Draco",
                modifier = Modifier.size(130.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "DracoFocus",
                color = dracoCyan,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Sincronizado con Laravel",
                color = Color(0xFFB0BEC5),
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Card de Login
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, dracoCyan, RoundedCornerShape(16.dp)),
                color = Color(0xFF0F1A2A),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "🔹 Iniciar Sesión",
                        color = dracoCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    CustomTextField(
                        value = viewModel.email,
                        onValueChange = { viewModel.onEmailChange(it) },
                        label = "Correo Electrónico",
                        placeholder = "tu@email.com",
                        icon = painterResource(id = R.drawable.ic_email),
                        keyboardType = KeyboardType.Email
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    CustomTextField(
                        value = viewModel.password,
                        onValueChange = { viewModel.onPasswordChange(it) },
                        label = "Contraseña",
                        placeholder = "********",
                        icon = painterResource(id = R.drawable.ic_password),
                        isPassword = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (state.error != null) {
                        Text(
                            text = state.error,
                            color = Color.Red,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    Button(
                        onClick = { viewModel.login() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = dracoCyan,
                            contentColor = Color.Black
                        ),
                        enabled = !state.isLoading
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
                        } else {
                            Text("INGRESAR", fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(
                        onClick = onNavigateToRegister,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("¿No tienes cuenta? Regístrate", color = dracoCyan)
                    }
                }
            }
        }
    }
}
