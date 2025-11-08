package co.edu.unab.dracofocusapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import co.edu.unab.dracofocusapp.theme.BlueGradientEnd
import co.edu.unab.dracofocusapp.theme.TealGradientStart
import co.edu.unab.dracofocusapp.ui.components.ModernTopBar

@Composable
fun BaseDracoScreen(
    title: String,
    onBackClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val gradientBackground = Brush.verticalGradient(
        colors = listOf(TealGradientStart.copy(alpha = 0.15f), BlueGradientEnd.copy(alpha = 0.35f))
    )

    Scaffold(
        topBar = {
            ModernTopBar(
                title = title,
                onBackClick = onBackClick ?: {}
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
                verticalArrangement = Arrangement.Top,
                content = content
            )
        }
    }
}
