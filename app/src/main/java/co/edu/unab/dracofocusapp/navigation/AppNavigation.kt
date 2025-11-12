package co.edu.unab.dracofocusapp.navigation

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import co.edu.unab.dracofocusapp.auth.AuthScreen
import co.edu.unab.dracofocusapp.auth.ForgotPasswordScreen
import co.edu.unab.dracofocusapp.auth.RegisterScreen
import co.edu.unab.dracofocusapp.main.MainScreen
import co.edu.unab.dracofocusapp.ui.Pomodoro.DracomodoroScreen
import co.edu.unab.dracofocusapp.ui.Lecciones.Solitario.LeccionDecisionesDeFuegoScreen
import co.edu.unab.dracofocusapp.ui.Lecciones.Solitario.LeccionVueloInfinitoScreen
import co.edu.unab.dracofocusapp.ui.Lecciones.Solitario.LeccionesDracoSolitarioScreen
import co.edu.unab.dracofocusapp.ui.Lecciones.Grupales.LeccionesGrupalesScreen
import co.edu.unab.dracofocusapp.ui.Perfil.MyProfileScreen
import co.edu.unab.dracofocusapp.theme.AppColorScheme
import co.edu.unab.dracofocusapp.theme.DarkBlueBg
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import androidx.compose.foundation.text.KeyboardOptions
import co.edu.unab.dracofocusapp.ui.Lecciones.RespuestaLeccionesScreen
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.navArgument
import co.edu.unab.dracofocusapp.ui.Lecciones.FeedbackScreen
import co.edu.unab.dracofocusapp.ui.Lecciones.Grupales.IngresarCodigoGrupoScreen
import co.edu.unab.dracofocusapp.ui.Lecciones.MenuLeccionesScreen
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import androidx.navigation.NavType
import co.edu.unab.dracofocusapp.viewmodel.FeedbackViewModel

//borrar este comentario prueba
//Especificar Rutas
object AppRoutes { // este es para los String para evitar errores al escribir los nombers de las rutas
    const val SPLASH = "splash" //ruta para la verificacion de login
    const val AUTH = "auth" // ruta del contenedor principal de login
    const val MAIN = "main" // ruta pantalla principal
    const val REGISTER = "register" // ruta pantalla de registro
    const val FORGOT_PASSWORD = "forgot_password" // ruta pantalla recuperacion de password
    const val PROFILE = "profile" //ruta perfil

    const val DRACOMODORO = "dracomodoro"
    const val LECCION_DECISIONES_DE_FUEGO = "leccion_decisiones_de_fuego"
    const val LECCION_VUELO_INFINITO = "leccion_draco_s2"
    const val MUSEO_DRACARTE = "museo_dracarte"
    const val LECCIONES_SOLO = "lecciones_dracosolitario"
    const val LECCIONES_GRUPALES = "lecciones_grupales"
    const val RESPUESTA_LECCION_S = "respuesta_leccion_s"

    const val MENU_LECCIONES = "menu_lecciones"
    const val FEEDBACK_SCREEN = "feedback_screen"


}

@Composable
fun AppNavigation() { //crea el estado de navigation
    val navController = rememberNavController()
    val auth = Firebase.auth
    LaunchedEffect(auth) {
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user == null) {
                // usuario deslogeado
                navController.navigate(AppRoutes.AUTH) { // vuelve al login
                    popUpTo(0) { inclusive = true } // limpia toda la pila
                    launchSingleTop = true
                }
            }
        }
    }



    MaterialTheme(colorScheme = AppColorScheme) {
        NavHost(// es el componente para la navegacion
            navController = navController, //el controlador a usar
            startDestination = AppRoutes.SPLASH // la pantalla que se muestra al iniciar
        ) {
            // Rutas

            //  Pantalla verifica si hay usuario logueado
            composable(AppRoutes.SPLASH) {
                SplashScreen(navController = navController)
            }

            //  Pantalla de autenticacion del login y register
            composable(AppRoutes.AUTH) {
                AuthScreen( //para ir a la pantalla principal si se hace el login exitoso
                    onNavigateToMain = {
                        navController.navigate(AppRoutes.MAIN) {
                            //borra las pantallas de auth del historial para no poder volver
                            popUpTo(AppRoutes.AUTH) { inclusive = true }
                            launchSingleTop = true // para que no crear otra instancia si ya existe
                        }
                    },
                    //ir a la pantalla de password olvidada
                    onNavigateToForgotPassword = {
                        navController.navigate(AppRoutes.FORGOT_PASSWORD)
                    },
                    //ir a la pantalla de registro
                    onNavigateToRegister = {
                        navController.navigate(AppRoutes.REGISTER)
                    }
                )
            }
            //  Pantalla de registro
            composable(AppRoutes.REGISTER) {
                RegisterScreen( // para volver a la pantalla anterior, la de login
                    onBackToLogin = {
                        navController.popBackStack() //se devuelve una pantalla
                    },
                    // para ir a la pantalla principal cuando se registra exitosamente
                    onRegisterSuccess = {
                        navController.navigate(AppRoutes.MAIN) {
                            //borra las pantallas de auth del historial despues del registro
                            popUpTo(AppRoutes.AUTH) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }

            //  Pantalla de recuperacion de password
            composable(AppRoutes.FORGOT_PASSWORD) {
                ForgotPasswordScreen(
                    onBackToLogin = { navController.popBackStack() }
                )
            }

            // Pantalla principal
            composable(AppRoutes.MAIN) {
                MainScreen(
                    onNavigateToAuth = {
                        navController.navigate(AppRoutes.AUTH) {
                            popUpTo(AppRoutes.MAIN) { inclusive = true }
                        }
                    },
                    onNavigateToProfile = {
                        navController.navigate(AppRoutes.PROFILE)
                    }
                )
            }
            // Pantalla Dracomodoro
            composable(AppRoutes.DRACOMODORO) {
                DracomodoroScreen(onBack = { navController.popBackStack() })
            }
            // Pantalla Elegir que tipo de leccion
            composable(AppRoutes.MENU_LECCIONES) {
                MenuLeccionesScreen(navController = navController)
            }

            // Pantallas de Lecciones
            composable(AppRoutes.LECCION_DECISIONES_DE_FUEGO) {
                LeccionDecisionesDeFuegoScreen(
                    navController = navController,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(AppRoutes.LECCION_VUELO_INFINITO) {
                LeccionVueloInfinitoScreen (
                    navController = navController,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(AppRoutes.LECCIONES_SOLO) {
                LeccionesDracoSolitarioScreen(
                    navController = navController,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(AppRoutes.LECCIONES_GRUPALES) {
                LeccionesGrupalesScreen(
                        navController = navController,
                        onBack = { navController.popBackStack() }
                )
            }
            composable(AppRoutes.RESPUESTA_LECCION_S) {
                RespuestaLeccionesScreen(onBack = { navController.popBackStack() })
            }

            composable(AppRoutes.FEEDBACK_SCREEN) {
                val feedbackViewModel: FeedbackViewModel = viewModel()
                FeedbackScreen(
                    navController = navController,
                    retroalimentacion = feedbackViewModel.retroalimentacion.value
                )
            }


            // Pantalla Museo
            composable("museo_dracarte") {
                MuseoDracoArteScreen(onBack = { navController.popBackStack() })


        }
            // Pantalla para el codigo de lecciones en grupo
            composable("ingresar_codigo_grupo/{leccionId}") { backStackEntry ->
                IngresarCodigoGrupoScreen(
                    navController = navController,
                    backStackEntry = backStackEntry,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("lecciones_grupales") {
                LeccionesGrupalesScreen(
                    navController = navController,
                    onBack = { navController.navigate("grupo_codigo") }
                )
            }




            // Pantalla de mi perfil
            composable(AppRoutes.PROFILE) {
                MyProfileScreen(
                    onBackToMain = { navController.popBackStack() },
                    onNavigateToAuth = {
                        navController.navigate(AppRoutes.AUTH) {
                            popUpTo(AppRoutes.MAIN) { inclusive = true } // elimina la pantalla principal
                            launchSingleTop = true //evita duplicados

                        }
                    }
                )
            }
        }
    }
}


@Composable
fun MuseoDracoArteScreen(onBack: () -> Boolean) {
    TODO("Not yet implemented")
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: Painter,
    placeholder: String = "",
    isPassword: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default // opcion de teclado por default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        leadingIcon = {
            Icon(
                painter = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary //usa el color principal del tema
            )
        },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None, //si isPassword es true oculta el texto
        keyboardOptions = keyboardOptions, // tipo de teclado
        shape = MaterialTheme.shapes.medium
    )
}
//Verifica si la cuenta esta activa
@Composable
fun SplashScreen(navController: NavController) {
    // transforma el comprobar firebase en un estado
    val isAuthenticated by produceState<Boolean?>(initialValue = null, producer = {
        val auth = Firebase.auth
        value = auth.currentUser != null // comprobar si es nulo o no si esta logueado
    })

    LaunchedEffect(isAuthenticated) { // se ejecuta uma sola vez al inicio y cuando isAuthenticated cambia algun valor
        when (isAuthenticated) {
            true -> navController.navigate(AppRoutes.MAIN) {
                popUpTo(AppRoutes.SPLASH) { inclusive = true }//si esta autenticado, empieza a navegar a main y borra el splash del historial
            }
            false -> navController.navigate(AppRoutes.AUTH) {
                popUpTo(AppRoutes.SPLASH) { inclusive = true } //si NO esta autenticado, empieza a navegar a auth y borra el splash del historial
            }
            null -> Unit // no hace nada mientras espera la respuesta de firebase
        }
    }

    Surface(
        color = DarkBlueBg,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)// cargando... para indicar que la app esta verificando el estado
        }
    }
}



