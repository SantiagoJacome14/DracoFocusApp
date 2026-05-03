package co.edu.unab.dracofocusapp.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.unab.dracofocusapp.R
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.CustomCredential
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import android.util.Log
import co.edu.unab.dracofocusapp.data.remote.RetrofitInstance

// Pantalla autenticación Login
@Composable
fun AuthScreen(
    viewModel: AuthViewModel = viewModel(),
    onNavigateToMain: () -> Unit = {},     // Navega a la pantalla principal si el login es exitoso
    onNavigateToForgotPassword: () -> Unit = {},  // Navega a la pantalla de recuperación de contraseña
    onNavigateToRegister: () -> Unit = {}         // Navega a la pantalla de registro
) {

    val uiState = viewModel.uiState
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = CredentialManager.create(context)
    val tokenManager = TokenManager(context)

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
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.dragon_dracofocus1),
                contentDescription = "Mascota Draco",
                modifier = Modifier.size(130.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "DracoFocus",
                color = Color(0xFF22DDF2),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Potencia tu aprendizaje",
                color = Color(0xFFB0BEC5),
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = Color(0xFF22DDF2),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .background(Color(0xFF0F1A2A), RoundedCornerShape(16.dp))
                    .padding(24.dp)
            ) {
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = "🔹 Iniciar Sesión",
                        color = Color(0xFF22DDF2),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    CustomTextField(
                        value = uiState.loginEmail,
                        onValueChange = viewModel::onLoginEmailChanged,
                        label = "Correo Electrónico",
                        placeholder = "tu@email.com",
                        icon = painterResource(id = R.drawable.ic_email),
                        keyboardType = KeyboardType.Email
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    CustomTextField(
                        value = uiState.loginPassword,
                        onValueChange = viewModel::onLoginPasswordChanged,
                        label = "Contraseña",
                        placeholder = "********",
                        icon = painterResource(id = R.drawable.ic_password),
                        isPassword = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "¿Olvidaste tu contraseña?",
                        color = Color(0xFF22DDF2),
                        fontSize = 12.sp,
                        modifier = Modifier
                            .align(Alignment.End)
                            .clickable { onNavigateToForgotPassword() },
                        textDecoration = TextDecoration.Underline
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            viewModel.loginWithEmail(
                                uiState.loginEmail,
                                uiState.loginPassword,
                                tokenManager
                            ) {
                                onNavigateToMain()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = !uiState.isLoading,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF22DDF2),
                            contentColor = Color.Black
                        )
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                color = Color.Black,
                                modifier = Modifier.size(22.dp)
                            )
                        } else {
                            Text("Iniciar Sesión", fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = {
                            val googleIdOption = GetGoogleIdOption.Builder()
                                .setServerClientId("461716187115-vtbahb3hngqj7kfeun641oqmjvq4mhgo.apps.googleusercontent.com")
                                .setFilterByAuthorizedAccounts(false)
                                .build()

                            val request = GetCredentialRequest.Builder()
                                .addCredentialOption(googleIdOption)
                                .build()

                            scope.launch {
                                try {
                                    Log.d("GOOGLE_LOGIN", "Antes de getCredential")
                                    val result = credentialManager.getCredential(
                                        request = request,
                                        context = context
                                    )
                                    Log.d("GOOGLE_LOGIN", "Después de getCredential")
                                    val credential = result.credential
                                    if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                                        val idToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                                        Log.d("GOOGLE_LOGIN", "ID TOKEN obtenido, llamando a onGoogleSignIn")
                                        viewModel.onGoogleSignIn(idToken, tokenManager) {
                                            onNavigateToMain()
                                        }
                                    } else {
                                        Log.d("GOOGLE_LOGIN", "Credencial no es de tipo Google")
                                    }
                                } catch (e: Exception) {
                                    Log.e("GOOGLE_LOGIN", "Error en login Google", e)
                                    viewModel.onError("Error con Google: ${e.message}")
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = !uiState.isLoading,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFF22DDF2).copy(alpha = 0.5f)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_iniciarsesion),
                                contentDescription = "Google Icon",
                                modifier = Modifier.size(20.dp),
                                tint = Color.Unspecified
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Continuar con Google", fontWeight = FontWeight.Medium)
                        }
                    }

                    uiState.errorMessage?.let {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = it,
                            color = Color.Red,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    HorizontalDivider(
                        color = Color(0xFF22DDF2).copy(alpha = 0.3f),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "¿No tienes cuenta?",
                            color = Color.LightGray,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Regístrate aquí",
                            color = Color(0xFF22DDF2),
                            fontWeight = FontWeight.Bold,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.clickable { onNavigateToRegister() }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Ingeniería de Sistemas UNAB - Gamificación Educativa",
                color = Color(0xFF546E7A),
                fontSize = 11.sp
            )
        }
    }
}

// Funciones reutilizables para los iconos, el placeholder y la comtraseña
@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    icon: androidx.compose.ui.graphics.painter.Painter,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color.LightGray, fontSize = 12.sp) },
        placeholder = { Text(placeholder, color = Color.Gray, fontSize = 12.sp) },
        leadingIcon = {
            Icon(
                painter = icon,
                contentDescription = label,
                tint = Color(0xFF22DDF2)
            )
        },
        // Si el campo es contraseña, muestra el icono para alternar visibilidad
        trailingIcon = {
            if (isPassword) {
                val visibilityIcon: ImageVector =
                    if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        visibilityIcon,
                        contentDescription = if (passwordVisible) "Ocultar" else "Mostrar",
                        tint = Color(0xFF22DDF2)
                    )
                }
            }
        },
        // Controla si se muestra o no el texto de la contraseña
        visualTransformation = if (isPassword && !passwordVisible)
            PasswordVisualTransformation()
        else
            VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        textStyle = TextStyle(color = Color.White, fontSize = 14.sp),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF22DDF2),
            unfocusedBorderColor = Color(0xFF1C2541),
            cursorColor = Color(0xFF22DDF2)
        )
    )
}
