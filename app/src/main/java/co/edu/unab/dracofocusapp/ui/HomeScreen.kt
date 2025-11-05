package co.edu.unab.dracofocusapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.unab.dracofocusapp.R
import androidx.compose.ui.draw.clip

@Composable
fun HomeScreen(
    onNavigateToStudy: () -> Unit = {},
    onNavigateToLessons: () -> Unit = {},
    onNavigateToMuseum: () -> Unit = {},
    onNavigateToProgress: () -> Unit = {}
) {

    val gradientBackground = Brush.verticalGradient(
        listOf(Color(0xFF0B132B), Color(0xFF1C2541))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBackground)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(Modifier.height(12.dp))

        Image(
            painter = painterResource(id = R.drawable.dragon_dracofocus1),
            contentDescription = "Draco",
            modifier = Modifier.size(190.dp)
        )

        Spacer(Modifier.height(10.dp))

        Text(
            text = "¡Excelente trabajo! 🎉 Draco está muy orgulloso de ti.",
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 10.dp)
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = "¡Sigue estudiando para ayudarlo a completar su museo de arte pieza por pieza! 🪄⭐",
            color = Color(0xFFBBD8F4),
            fontSize = 14.sp,
            modifier = Modifier.padding(horizontal = 10.dp),
            lineHeight = 20.sp
        )

        Spacer(Modifier.height(24.dp))

        // Barra de progreso
        Text("Objetivo Diario", color = Color.White, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = 0.8f,
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(12.dp)),
            color = Color(0xFF22DDF2)
        )
        Spacer(Modifier.height(6.dp))
        Text("80%", color = Color(0xFF22DDF2), fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(28.dp))

        // BOTONES
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MainActionButton("ESTUDIO", R.drawable.ic_study, Color(0xFF8A78FF), onNavigateToStudy)
            MainActionButton("LECCIÓN", R.drawable.ic_book, Color(0xFF22DDF2), onNavigateToLessons)
        }

        Spacer(Modifier.height(18.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MainActionButton("MUSEO", R.drawable.ic_museum, Color(0xFF22DDF2), onNavigateToMuseum)
            MainActionButton("AVANCES", R.drawable.ic_calendar, Color(0xFF8A78FF), onNavigateToProgress)
        }
    }
}

@Composable
fun MainActionButton(text: String, icon: Int, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = color),
        modifier = Modifier
            .width(150.dp)
            .height(70.dp),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(26.dp)
            )
            Spacer(Modifier.width(10.dp))
            Text(text, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}
