package co.edu.unab.dracofocusapp.ui.Pomodoro

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.ui.text.style.TextAlign
import co.edu.unab.dracofocusapp.R

@Composable
fun CicloCompletadoScreen(navController: NavController) {

    // Animación de zoom en la medalla
    var startAnimation by remember { mutableStateOf(false) }
    val scaleAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 700)
    )
    LaunchedEffect(Unit) { startAnimation = true }

    // Fondo degradado elegante
    val gradientBg = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF121629),
            Color(0xFF1E2757),
            Color(0xFF283980)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBg),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "¡Ciclo Completado!",
                fontSize = 36.sp,
                color = Color(0xFF22DDF2),
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(15.dp))

            Image(
                painter = painterResource(id = R.drawable.medalla_dorado),
                contentDescription = "Medalla",
                modifier = Modifier
                    .size(200.dp)
                    .scale(scaleAnim)
            )

            Spacer(modifier = Modifier.height(25.dp))

            // Mensaje
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .background(Color.White.copy(alpha = 0.07f), RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Text(
                    text = "Increíble trabajo 🎉\nDraco está orgulloso de tu esfuerzo.\n\n💙 Tu enfoque mejora cada día 🔥",
                    color = Color(0xFFE9F7FF),
                    fontSize = 18.sp,
                    lineHeight = 26.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(35.dp))

            // BOTÓN
            Button(
                onClick = {
                    navController.navigate("pomodoro") {
                        popUpTo("pomodoro") { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .height(55.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF22DDF2),
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text = "✨ Continuar Estudiando",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}