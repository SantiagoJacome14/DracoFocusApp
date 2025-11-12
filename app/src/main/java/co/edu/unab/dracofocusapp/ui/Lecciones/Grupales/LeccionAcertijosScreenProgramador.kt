package co.edu.unab.dracofocusapp.ui.Lecciones.Grupales

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import co.edu.unab.dracofocusapp.auth.ModernTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeccionAcertijosScreenProgramador(
    navController: NavController,
    onComplete: () -> Unit = {},
    onBack: () -> Unit = { navController.popBackStack() }
) {
}