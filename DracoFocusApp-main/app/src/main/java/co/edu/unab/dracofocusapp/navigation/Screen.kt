package co.edu.unab.dracofocusapp.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Auth : Screen("auth")
    object Main : Screen("main")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object Profile : Screen("profile")
    object Dracomodoro : Screen("dracomodoro")
    object MenuLecciones : Screen("menu_lecciones")
    object LeccionDecisionesDeFuego : Screen("leccion_decisiones_de_fuego")
    object LeccionesSolo : Screen("lecciones_dracosolitario")
    object LeccionesGrupales : Screen("lecciones_grupales")
    object RespuestaLeccion : Screen("respuesta_leccion_s")
    object Feedback : Screen("feedback_screen")
    object Museo : Screen("museo_dracarte")
    
    // Rutas con argumentos
    object IngresarCodigo : Screen("ingresar_codigo_grupo/{leccionId}") {
        fun createRoute(leccionId: String) = "ingresar_codigo_grupo/$leccionId"
    }
}
