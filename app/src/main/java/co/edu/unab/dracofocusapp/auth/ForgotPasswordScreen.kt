package co.edu.unab.dracofocusapp.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.unab.dracofocusapp.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import androidx.compose.ui.text.style.TextDecoration

@OptIn(ExperimentalMaterial3Api::class) //Es necesario para usar material3
@Composable
fun ForgotPasswordScreen(  // Esta funcion se llama cuando el user le da click a Volver al Login
    onBackToLogin: () -> Unit
) {
    val auth = Firebase.auth // Instancia en elFireBase para usarla con el boton
// Las var con by remember lo que hacen es funcionar hasta que se acabe el Composable
    var email by remember { mutableStateOf("") } // Guardar el correo que escribe el user
    var isLoading by remember { mutableStateOf(false) } // Muestra que esta cargando a la espera del FireBase
    var message by remember { mutableStateOf<String?>(null) } // El mensaje de si es exitoso o error para mostrarle a user

    // Aqui empieza el a crearse la pantalla
    val gradientBackground = Brush.verticalGradient( // El brush para el fondo vertical
        listOf(Color(0xFF0B132B), Color(0xFF1C2541)) // Los colores, el primero es el azul mas oscuro, el otro mas claro
    )
    // El box para que ocuppe toda la pantalla
    Box(
        modifier = Modifier
            .fillMaxSize() // ocupar todo el espacio
            .background(gradientBackground) // aplicar degradado
            .padding(24.dp), // margen
        contentAlignment = Alignment.Center // centrar todo el column
    ) {
        //Para organizar los elementos horizontalmente
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // La imagen de draco, la mascota
            Image(
                painter = painterResource(id = R.drawable.dragon_dracofocus1), //Carga imagen
                contentDescription = "Mascota Draco", // Le agrega descripcion
                modifier = Modifier.size(140.dp) // tamano
            )

            Spacer(modifier = Modifier.height(8.dp))
            // Primer mensaje, el titulo
            Text(
                "Recupera tu acceso",
                color = Color.White, // el color blanco
                fontSize = 26.sp, //tamano fuente
                fontWeight = FontWeight.Bold // .bold para la negrilla
            )
            //  subtitulo
            Text(
                "Te ayudaremos a restablecer tu contraseña",
                color = Color(0xFFB0BEC5), //color gris claro
                fontSize = 14.sp //tamano fuente
            )

            Spacer(modifier = Modifier.height(24.dp))
            // El box es el contenedor para la caja con borde azul
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF22DDF2), shape = MaterialTheme.shapes.medium) //borde azul neon
                    .background(Color(0xFF0F1A2A), MaterialTheme.shapes.medium) //fondo mas oscuro para el formulario
                    .padding(20.dp)
            ) {
                //Columna para campos y botones
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    //  Campo de correo electrónico
                    CustomRegisterField(
                        value = email, // Lo que viene del estado
                        onValueChange = { email = it }, //funcion para actualizar el estado cada que se escribe
                        label = "Correo electrónico",
                        icon = R.drawable.ic_email, //icono email
                        keyboardType = KeyboardType.Email //teclado optimizado para los correos
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botón de envío de correo
                    Button(
                        onClick = {
                            if (email.isBlank()) { //Validacion anttes de llamar a Firebase
                                message = "Por favor ingresa tu correo electrónico."
                                return@Button //para salir de onclick
                            }

                            isLoading = true // muestra que esta cargando
                            message = null //limpiar mensajes anteriores

                            //llamar a firebase
                            auth.sendPasswordResetEmail(email.trim()) //trim para borrar espacios al principio y fin
                                .addOnCompleteListener { task -> //se ejecuta cuando firebase da el exito o el error
                                    isLoading = false //oculta el cargando
                                    message = if (task.isSuccessful) { //muestra el mensaje
                                        "Correo de recuperación enviado. Revisa tu bandeja." //mensaje exito
                                    } else {
                                        task.exception?.message ?: "Error al enviar el correo." //mensaje error
                                    }
                                }
                        },
                        //diseno del boton
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF22DDF2),//color de fondo
                            contentColor = Color.Black //color de texto
                        )
                    ) {
                        //muestra si esta cargando
                        if (isLoading)
                            CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(20.dp))
                        // si no esta cargando , muestra el texto
                        else
                            Text("Enviar correo", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Mensaje de estado
                    message?.let { //si el mensaje no es nulo
                        Text(
                            text = it,
                            color = if (it.startsWith("Correo")) Color(0xFF22DDF2) else Color.Red, //el color del mensaje depende si es exito y empieza con Correo  o error
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    //  Volver al login
                    Text(
                        text = "Volver al Login",
                        color = Color(0xFF22DDF2),
                        textDecoration = TextDecoration.Underline, //subraya el text
                        modifier = Modifier.clickable { onBackToLogin() } //ejecuta la navigation
                    )
                }
            }
        }
    }
}
