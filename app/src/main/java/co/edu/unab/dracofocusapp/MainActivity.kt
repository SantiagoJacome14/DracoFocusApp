package co.edu.unab.dracofocusapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import co.edu.unab.dracofocusapp.navigation.AppNavigation
import co.edu.unab.dracofocusapp.theme.AppColorScheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = AppColorScheme) {
                Surface {
                    AppNavigation()
                }
            }
        }
    }
}
