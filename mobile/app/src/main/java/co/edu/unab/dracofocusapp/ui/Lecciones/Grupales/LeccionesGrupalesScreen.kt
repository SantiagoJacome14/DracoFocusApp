package co.edu.unab.dracofocusapp.ui.Lecciones.Grupales

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import co.edu.unab.dracofocusapp.data.remote.ApiService

@Composable
fun LeccionesGrupalesScreen(
    navController: NavController,
    apiService: ApiService,
    onBack: () -> Unit,
) {
    val dracoCyan = Color(0xFF22DDF2)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF0B132B), Color(0xFF1C2541)))),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = dracoCyan.copy(alpha = 0.6f),
                modifier = Modifier.size(64.dp),
            )

            Spacer(Modifier.height(24.dp))

            Text(
                "Modo Grupal",
                color = dracoCyan,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(Modifier.height(12.dp))

            Text(
                "Próximamente: juega con tus compañeros en salas Draco.",
                color = Color(0xFF8FA3BD),
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
            )

            Spacer(Modifier.height(40.dp))

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth(0.6f).height(50.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = dracoCyan),
                border = BorderStroke(1.dp, dracoCyan),
            ) {
                Text("VOLVER", fontWeight = FontWeight.Bold)
            }
        }
    }
}
