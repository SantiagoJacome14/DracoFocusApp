package co.edu.unab.dracofocusapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import co.edu.unab.dracofocusapp.R


@Composable
fun ModernLessonCard(
    icon: Int? = null,
    titulo: String,
    subtitulo: String,
    xp: String,
    navController: NavController,
    isCompleted: Boolean,
    onStart: () -> Unit,
    onComplete: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF22DDF2), RoundedCornerShape(16.dp))
            .background(Color(0xFF0F1A2A), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {

            icon?.let {
                Image(
                    painter = painterResource(id = it),
                    contentDescription = titulo,
                    modifier = Modifier.size(48.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f).padding(horizontal = 10.dp)
            ) {
                Text(titulo, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(subtitulo, color = Color(0xFFB3B3B3), fontSize = 13.sp)
            }

            IconButton(
                onClick = { onStart() },
                enabled = !isCompleted
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_play),
                    contentDescription = "Iniciar lección",
                    tint = if (isCompleted) Color.Gray else Color(0xFF22DDF2),
                    modifier = Modifier.size(28.dp)
                )
            }

            Button(
                onClick = { onComplete() },
                enabled = !isCompleted,
                colors = if (isCompleted)
                    ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF455A64),
                        contentColor = Color.White
                    )
                else
                    ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF22DDF2),
                        contentColor = Color.Black
                    ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(if (isCompleted) "Hecho" else xp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
