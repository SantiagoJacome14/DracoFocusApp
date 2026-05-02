package co.edu.unab.dracofocusapp.ui.Auth

import android.util.Log
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
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.CustomCredential
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onNavigateToMain: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit
) {
    val state = viewModel.state
    val dracoCyan = Color(0xFF22DDF2)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = CredentialManager.create(context)

    var googleSignInError by remember { mutableStateOf<String?>(null) }

    // Limpiar error de Google cuando cambia el estado general
    LaunchedEffect(state.error) {
        if (state.error != null) {
            googleSignInError = null
        }
    }

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

                    // Botón de Google Sign-In
                    OutlinedButton(
                        onClick = {
                            Log.d("GOOGLE_LOGIN", "[1] Botón Google presionado")

                            val googleIdOption = GetGoogleIdOption.Builder()
                                .setFilterByAuthorizedAccounts(false)
                                .setServerClientId("461716187115-vtbahb3hngqj7kfeun641oqmjvq4mhgo.apps.googleusercontent.com")
                                .setAutoSelectEnabled(false)
                                .build()

                            val request = GetCredentialRequest.Builder()
                                .addCredentialOption(googleIdOption)
                                .build()

                            Log.d("GOOGLE_LOGIN", "[2] GetCredentialRequest creado")

                            scope.launch {
                                try {
                                    Log.d("GOOGLE_LOGIN", "[3] Llamando a credentialManager.getCredential()")
                                    val result = credentialManager.getCredential(
                                        request = request,
                                        context = context
                                    )
                                    val credential = result.credential
                                    Log.d("GOOGLE_LOGIN", "[4] Credential Manager respondió: type=${credential.type}")

                                    if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                                        val idToken = googleIdTokenCredential.idToken
                                        val tokenPreview = if (idToken.length > 20) idToken.take(20) + "..." else idToken
                                        Log.d("GOOGLE_LOGIN", "[5] idToken obtenido: $tokenPreview")
                                        Log.d("GOOGLE_LOGIN", "[6] Llamando a viewModel.onGoogleSignIn()")
                                        viewModel.onGoogleSignIn(idToken)
                                    } else {
                                        Log.e("GOOGLE_LOGIN", "[4] Credential NO es GoogleIdToken. type=${credential.type}, es CustomCredential=${credential is CustomCredential}")
                                    }
                                } catch (e: NoCredentialException) {
                                    Log.e("GOOGLE_LOGIN", "[3] NoCredentialException: No se encontraron credenciales Google. El dispositivo no tiene cuenta Google o usa imagen sin Google Play", e)
                                    googleSignInError = "No se encontró una cuenta Google en este dispositivo. Agrega una cuenta Google (Configuración > Cuentas) o usa email y contraseña."
                                } catch (e: Exception) {
                                    Log.e("GOOGLE_LOGIN", "[3] Excepción en getCredential: ${e::class.simpleName} - ${e.message}", e)
                                    googleSignInError = "Error al iniciar sesión con Google: ${e.message}"
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = !state.isLoading,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, dracoCyan.copy(alpha = 0.5f)),
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

                    // Mostrar error de Google Sign-In si existe
                    if (googleSignInError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = googleSignInError!!,
                            color = Color(0xFFFF6B6B),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
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
