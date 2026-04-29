package co.edu.unab.dracofocusapp.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.edu.unab.dracofocusapp.theme.DarkBlueBg

@Composable
fun MuseoDracoArteScreen(onBack: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize(), color = DarkBlueBg) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = "Museo DracoArte - Próximamente",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Button(
                onClick = { onBack() },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
            ) {
                Text("Volver")
            }
        }
    }
}
