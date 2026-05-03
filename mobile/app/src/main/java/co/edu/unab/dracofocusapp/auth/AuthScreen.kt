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
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
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
import co.edu.unab.dracofocusapp.data.remote.LoginRequest
import co.edu.unab.dracofocusapp.data.remote.RetrofitInstance

// Pantalla autenticación Login
@Composable
fun AuthScreen(
    viewModel: AuthViewModel = viewModel(),
    onNavigateToMain: () -> Unit = {},     // Navega a la pantalla principal si el login es exitoso
    onNavigateToForgotPassword: () -> Unit = {},  // Navega a la pantalla de recuperación de contraseña
    onNavigateToRegister: () -> Unit = {}         // Navega a la pantalla de registro
) {

    //Estados

    // Estado de la interfaz de usuario
    val uiState = viewModel.uiState

    // Instancia de Firebase Authentication
    val auth = Firebase.auth

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = CredentialManager.create(context)
    val tokenManager = TokenManager(context)

    // Estados locales para el cargando y errores
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Fondo con el degradado
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
            // Mascota
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

            // La tarjeta del Login
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

                    // Olvidaste tu contraseña
                    Text(
                        text = "¿Olvidaste tu contraseña?",
                        color = Color(0xFF22DDF2),
                        fontSize = 12.sp,
                        modifier = Modifier
                            .align(Alignment.End)
                            .clickable { onNavigateToForgotPassword() },// Para enlazar
                        textDecoration = TextDecoration.Underline
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botón de inicio de sesión
                    Button(
                        onClick = {
                            if (uiState.loginEmail.isBlank() || uiState.loginPassword.isBlank()) {
                                errorMessage = "Por favor completa todos los campos."
                                return@Button
                            }

                            isLoading = true
                            errorMessage = null

                            auth.signInWithEmailAndPassword(
                                uiState.loginEmail.trim(),
                                uiState.loginPassword
                            ).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    scope.launch {
                                        try {
                                            val apiService = RetrofitInstance.getApiService(tokenManager)
                                            val response = apiService.login(
                                                LoginRequest(
                                                    uiState.loginEmail.trim(),
                                                    uiState.loginPassword
                                                )
                                            )
                                            if (response.isSuccessful && response.body() != null) {
                                                tokenManager.saveAuthData(
                                                    response.body()!!.accessToken,
                                                    response.body()!!.user.id.toString()
                                                )
                                                Log.d("EMAIL_LOGIN", "userId guardado: ${response.body()!!.user.id}")
                                            }
                                        } catch (e: Exception) {
                                            Log.e("EMAIL_LOGIN", "Error calling Laravel login", e)
                                        } finally {
                                            isLoading = false
                                            viewModel.onLoginSuccess()
                                            onNavigateToMain()
                                        }
                                    }
                                } else {
                                    isLoading = false
                                    val error = task.exception?.message ?: "Error al iniciar sesión."
                                    errorMessage = when {
                                        "password" in error.lowercase() -> "Contraseña incorrecta."
                                        "no user" in error.lowercase() -> "No existe una cuenta con este correo."
                                        else -> error
                                    }
                                    viewModel.onError(errorMessage!!)
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF22DDF2),
                            contentColor = Color.Black
                        )
                    ) {
                        if (isLoading)
                            CircularProgressIndicator(
                                color = Color.Black,
                                modifier = Modifier.size(22.dp)
                            )
                        else
                            Text("Iniciar Sesión", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Botón de Google Sign-In
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
                                    isLoading = true
                                    Log.d("GOOGLE_LOGIN", "Antes de getCredential")
                                    val result = credentialManager.getCredential(
                                        request = request,
                                        context = context
                                    )
                                    Log.d("GOOGLE_LOGIN", "Después de getCredential")
                                    val credential = result.credential
                                    Log.d("GOOGLE_LOGIN", "Credential recibida: ${credential::class.java.simpleName}")
                                    if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                                        val idToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                                        Log.d("GOOGLE_LOGIN", "ID TOKEN obtenido")
                                        Log.d("GOOGLE_LOGIN", "Llamando a onGoogleSignIn")
                                        viewModel.onGoogleSignIn(idToken, tokenManager) {
                                            isLoading = false
                                            onNavigateToMain()
                                        }
                                    } else {
                                        Log.d("GOOGLE_LOGIN", "Credencial no es de tipo Google")
                                        isLoading = false
                                    }
                                } catch (e: Exception) {
                                    Log.e("GOOGLE_LOGIN", "Error en login Google", e)
                                    isLoading = false
                                    viewModel.onError("Error con Google: ${e.message}")
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = !isLoading,
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

                    //  Mensaje de error preventivo
                    errorMessage?.let {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = it,
                            color = Color.Red,
                            fontSize = 13.sp,
                            style = TextStyle(fontWeight = FontWeight.Medium)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    HorizontalDivider(
                        color = Color(0xFF22DDF2).copy(alpha = 0.3f),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    // Registrarse
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
                            modifier = Modifier.clickable { onNavigateToRegister() } //Lo enlaza
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
