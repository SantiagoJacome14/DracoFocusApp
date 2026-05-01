package co.edu.unab.dracofocusapp.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import co.edu.unab.dracofocusapp.R
import co.edu.unab.dracofocusapp.navigation.AppRoutes
import co.edu.unab.dracofocusapp.theme.DarkBlueBg
import co.edu.unab.dracofocusapp.auth.TokenManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController, tokenManager: TokenManager) {
    // Estado para la animación de opacidad (alpha)
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Animación de entrada del logo (1 segundo)
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000)
        )
        
        // Espera un momento pequeño para que no sea tan abrupto
        delay(500)

        // Verificación de token en DataStore
        val token = tokenManager.token.first()
        val isAuthenticated = !token.isNullOrBlank()

        if (isAuthenticated) {
            navController.navigate(AppRoutes.MAIN) {
                popUpTo(AppRoutes.SPLASH) { inclusive = true }
            }
        } else {
            navController.navigate(AppRoutes.AUTH) {
                popUpTo(AppRoutes.SPLASH) { inclusive = true }
            }
        }
    }

    Surface(color = DarkBlueBg, modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo con animación de transparencia
            Image(
                painter = painterResource(id = R.drawable.dragon_dracofocus1),
                contentDescription = "Logo DracoFocus",
                modifier = Modifier
                    .size(180.dp)
                    .alpha(alpha.value)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Cargando discreto
            CircularProgressIndicator(
                modifier = Modifier.size(40.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 3.dp
            )
        }
    }
}
