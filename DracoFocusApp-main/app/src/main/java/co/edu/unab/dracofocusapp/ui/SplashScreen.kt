package co.edu.unab.dracofocusapp.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import co.edu.unab.dracofocusapp.navigation.Screen
import co.edu.unab.dracofocusapp.theme.DarkBlueBg
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun SplashScreen(navController: NavController) {
    val isAuthenticated by produceState<Boolean?>(initialValue = null, producer = {
        val auth = Firebase.auth
        value = auth.currentUser != null
    })

    LaunchedEffect(isAuthenticated) {
        when (isAuthenticated) {
            true -> navController.navigate(Screen.Main.route) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
            false -> navController.navigate(Screen.Auth.route) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
            null -> Unit
        }
    }

    Surface(
        color = DarkBlueBg,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    }
}
