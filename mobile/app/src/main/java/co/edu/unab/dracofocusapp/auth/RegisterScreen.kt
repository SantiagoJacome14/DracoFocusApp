package co.edu.unab.dracofocusapp.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import co.edu.unab.dracofocusapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernTopBar(
    title: String,
    showBackButton: Boolean = true,
    onBackClick: () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent
        )
    )
}

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel = viewModel(),
    onBack: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }

    var focusedField by remember { mutableStateOf("") }

    val gradientBackground = Brush.verticalGradient(
        listOf(Color(0xFF0B132B), Color(0xFF1C2541))
    )

    Scaffold(
        topBar = {
            ModernTopBar(
                title = "Registro",
                onBackClick = onBack
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBackground)
                .padding(innerPadding)
                .padding(24.dp),
            contentAlignment = Alignment.TopCenter
        ) {
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
                    "Únete a Dracofocus",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Crea tu cuenta y comienza tu aventura",
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
                            value = uiState.signUpName,
                            onValueChange = viewModel::onSignUpNameChanged,
                            label = "Nombre Completo",
                            icon = R.drawable.ic_user,
                            onFocus = { focusedField = "nombre" }
                        )
                        if (focusedField == "nombre")
                            Text("Pon tu nombre completo", color = Color.Gray, fontSize = 12.sp)

                        Spacer(modifier = Modifier.height(12.dp))

                        CustomRegisterField(
                            value = uiState.signUpEmail,
                            onValueChange = viewModel::onSignUpEmailChanged,
                            label = "Correo Electrónico",
                            icon = R.drawable.ic_email,
                            keyboardType = KeyboardType.Email,
                            onFocus = { focusedField = "correo" }
                        )
                        if (focusedField == "correo")
                            Text("Asegúrate de escribir un correo válido con @", color = Color.Gray, fontSize = 12.sp)

                        Spacer(modifier = Modifier.height(12.dp))

                        CustomRegisterField(
                            value = uiState.signUpSemester,
                            onValueChange = viewModel::onSignUpSemesterChanged,
                            label = "Semestre",
                            icon = R.drawable.ic_school,
                            keyboardType = KeyboardType.Number,
                            onFocus = { focusedField = "semestre" }
                        )
                        if (focusedField == "semestre")
                            Text("Solo números del 1 al 10", color = Color.Gray, fontSize = 12.sp)

                        Spacer(modifier = Modifier.height(12.dp))

                        CustomRegisterField(
                            value = uiState.signUpPassword,
                            onValueChange = viewModel::onSignUpPasswordChanged,
                            label = "Contraseña",
                            icon = R.drawable.ic_password,
                            isPassword = true,
                            keyboardType = KeyboardType.Password,
                            onFocus = { focusedField = "contrasena" }
                        )
                        if (focusedField == "contrasena")
                            Text("Crea una contraseña fuerte con letras y números", color = Color.Gray, fontSize = 12.sp)

                        Spacer(modifier = Modifier.height(12.dp))

                        CustomRegisterField(
                            value = uiState.signUpConfirmPassword,
                            onValueChange = viewModel::onSignUpConfirmPasswordChanged,
                            label = "Confirmar Contraseña",
                            icon = R.drawable.ic_password,
                            isPassword = true
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                viewModel.registerWithEmail(tokenManager) {
                                    onRegisterSuccess()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = MaterialTheme.shapes.medium,
                            enabled = !uiState.isLoading,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF22DDF2),
                                contentColor = Color.Black
                            )
                        ) {
                            if (uiState.isLoading)
                                CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(20.dp))
                            else
                                Text("Crear Cuenta", fontWeight = FontWeight.Bold)
                        }

                        uiState.errorMessage?.let {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(it, color = Color.Red, fontSize = 13.sp)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "¿Ya tienes cuenta? Inicia sesión",
                            color = Color(0xFF22DDF2),
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.clickable { onBack() }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun CustomRegisterField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: Int,
    onFocus: (() -> Unit)? = null,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color.LightGray, fontSize = 12.sp) },
        leadingIcon = {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = label,
                tint = Color(0xFF22DDF2)
            )
        },
        trailingIcon = {
            if (isPassword) {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                        tint = Color(0xFF22DDF2)
                    )
                }
            }
        },
        visualTransformation = if (isPassword && !passwordVisible)
            PasswordVisualTransformation()
        else
            VisualTransformation.None,
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
        textStyle = TextStyle(color = Color.White, fontSize = 14.sp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onFocus?.invoke() },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF22DDF2),
            unfocusedBorderColor = Color(0xFF1C2541),
            cursorColor = Color(0xFF22DDF2)
        )
    )
}
