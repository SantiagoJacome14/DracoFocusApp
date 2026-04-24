package co.edu.unab.dracofocusapp.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.Image
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import androidx.compose.runtime.Composable

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
        // Botón de retroceso si está habilitado
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
// Pantalla de registro
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onBackToLogin: () -> Unit, // Acción al presionar "Volver" o "¿Ya tienes cuenta?"
    onRegisterSuccess: () -> Unit // Acción tras completar el registro exitosamente
) {
    // Inicialización de Firebase Authentication y Firestore
    val auth = Firebase.auth
    val db = Firebase.firestore

    // Variables de estado para los campos del formulario
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var semester by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Control de campo enfocado y mensajes de error
    var focusedField by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Fondo degradado
    val gradientBackground = Brush.verticalGradient(
        listOf(Color(0xFF0B132B), Color(0xFF1C2541))
    )
    Scaffold(
        topBar = {
            ModernTopBar(
                title = "Registro",
                onBackClick = onBackToLogin
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
                // Imagen Draco
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

                // Caja que contiene los campos de registro
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFF22DDF2), shape = MaterialTheme.shapes.medium)
                        .background(Color(0xFF0F1A2A), MaterialTheme.shapes.medium)
                        .padding(20.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        // Nombre completo
                        CustomRegisterField(
                            value = fullName,
                            onValueChange = { if (it.all { c -> c.isLetter() || c.isWhitespace() }) fullName = it },
                            label = "Nombre Completo",
                            icon = R.drawable.ic_user,
                            onFocus = { focusedField = "nombre" }
                        )
                        if (focusedField == "nombre")
                            Text("Pon tu nombre completo", color = Color.Gray, fontSize = 12.sp)

                        Spacer(modifier = Modifier.height(12.dp))

                        // Correo
                        CustomRegisterField(
                            value = email,
                            onValueChange = { email = it },
                            label = "Correo Electrónico",
                            icon = R.drawable.ic_email,
                            keyboardType = KeyboardType.Email,
                            onFocus = { focusedField = "correo" }
                        )
                        if (focusedField == "correo")
                            Text("Asegúrate de escribir un correo válido con @", color = Color.Gray, fontSize = 12.sp)

                        Spacer(modifier = Modifier.height(12.dp))

                        // Semestre
                        CustomRegisterField(
                            value = semester,
                            onValueChange = { if (it.all { c -> c.isDigit() }) semester = it },
                            label = "Semestre",
                            icon = R.drawable.ic_school,
                            keyboardType = KeyboardType.Number,
                            onFocus = { focusedField = "semestre" }
                        )
                        if (focusedField == "semestre")
                            Text("Solo números del 1 al 10", color = Color.Gray, fontSize = 12.sp)

                        Spacer(modifier = Modifier.height(12.dp))

                        // Contraseña
                        CustomRegisterField(
                            value = password,
                            onValueChange = { password = it },
                            label = "Contraseña",
                            icon = R.drawable.ic_password,
                            isPassword = true,
                            keyboardType = KeyboardType.Password,
                            onFocus = { focusedField = "contrasena" }
                        )
                        if (focusedField == "contrasena")
                            Text("Crea una contraseña fuerte con letras y números", color = Color.Gray, fontSize = 12.sp)

                        Spacer(modifier = Modifier.height(12.dp))

                        // Confirmar contraseña
                        CustomRegisterField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = "Confirmar Contraseña",
                            icon = R.drawable.ic_password,
                            isPassword = true
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // BOTÓN REGISTRO
                        Button(
                            onClick = {
                                if (fullName.isBlank() || email.isBlank() || semester.isBlank() ||
                                    password.isBlank() || confirmPassword.isBlank()
                                ) {
                                    errorMessage = "Por favor completa todos los campos."
                                    return@Button
                                }
                                if (!email.contains("@")) {
                                    errorMessage = "El correo debe contener '@'."
                                    return@Button
                                }
                                if (password != confirmPassword) {
                                    errorMessage = "Las contraseñas no coinciden."
                                    return@Button
                                }

                                // Carga o error
                                isLoading = true
                                errorMessage = null

                                // Crear usuario en Firebase
                                auth.createUserWithEmailAndPassword(email.trim(), password)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            val currentUser = auth.currentUser
                                            val uid = currentUser?.uid ?: return@addOnCompleteListener

                                            // Actualiza el nombre en FirebaseAuth
                                            val profileUpdates =
                                                com.google.firebase.auth.UserProfileChangeRequest.Builder()
                                                    .setDisplayName(fullName)
                                                    .build()

                                            currentUser.updateProfile(profileUpdates)
                                                .addOnCompleteListener {
                                                    // Guarda los datos también en Firestore
                                                    val userData = hashMapOf(
                                                        "nombre" to fullName,
                                                        "correo" to email.trim(),
                                                        "semestre" to semester
                                                    )

                                                    db.collection("usuarios").document(uid)
                                                        .set(userData)
                                                        .addOnSuccessListener {
                                                            isLoading = false
                                                            onRegisterSuccess()
                                                        }
                                                        .addOnFailureListener {
                                                            isLoading = false
                                                            errorMessage = "Error guardando datos."
                                                        }
                                                }
                                        } else {
                                            isLoading = false
                                            errorMessage = task.exception?.message
                                                ?: "Error al registrar usuario."
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
                                Text("Crear Cuenta", fontWeight = FontWeight.Bold)
                        }

                        //  Mensaje de error
                        errorMessage?.let {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(it, color = Color.Red, fontSize = 13.sp)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        //  Enlace a login
                        Text(
                            text = "¿Ya tienes cuenta? Inicia sesión",
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
}
// Fun reutilizable para registro
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
    // Controla si la contraseña es visible o no
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
        // Ícono de alternancia de visibilidad para campos de contraseña
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
        // Oculta el texto si el campo es una contraseña
        visualTransformation = if (isPassword && !passwordVisible)
            PasswordVisualTransformation()
        else
            VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
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
