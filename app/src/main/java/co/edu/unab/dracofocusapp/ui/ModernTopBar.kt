package co.edu.unab.dracofocusapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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

@OptIn(ExperimentalMaterial3Api::class) // necesario por el uso de CenterAlignedTopAppBar
@Composable
fun ModernTopBar(
    title: String,
    onBackClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF001F3F), // azul oscuro
                        Color(0xFF000814)  // casi negro
                    )
                )
            ),
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = Color.White
        ),
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color(0xFF00E6FF) // azul neón
                )
            }
        },
        title = {
            Text(
                text = title,
                fontSize = 20.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    )
}
