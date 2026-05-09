package co.edu.unab.dracofocusapp.ui.Lecciones

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ModernLessonCard(
    icon: Int,
    titulo: String,
    subtitulo: String,
    isCompleted: Boolean,
    isLocked: Boolean = false,
    onStart: () -> Unit,
) {
    val dracoCyan = Color(0xFF22DDF2)
    val borderColor = when {
        isLocked    -> Color(0xFF2A3A4A)
        isCompleted -> Color(0xFF58FF99)
        else        -> dracoCyan.copy(alpha = 0.3f)
    }
    val iconBgColor = when {
        isLocked    -> Color(0xFF151E2D)
        isCompleted -> Color(0xFF005C41)
        else        -> Color(0xFF1C2541)
    }
    val iconTint = when {
        isLocked    -> Color(0xFF3D4E60)
        isCompleted -> Color(0xFF58FF99)
        else        -> dracoCyan
    }
    val rightIcon = when {
        isLocked    -> Icons.Default.Lock
        isCompleted -> Icons.Default.CheckCircle
        else        -> Icons.Default.PlayArrow
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isLocked) { onStart() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1A2A)),
        border = BorderStroke(1.dp, borderColor),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                color = iconBgColor,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(50.dp),
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.padding(12.dp),
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    titulo,
                    color = if (isLocked) Color(0xFF3D4E60) else Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    if (isLocked) "Completa la lección anterior para desbloquear" else subtitulo,
                    color = if (isLocked) Color(0xFF2D3E50) else Color.Gray,
                    fontSize = 12.sp,
                )
            }

            Icon(
                imageVector = rightIcon,
                contentDescription = null,
                tint = iconTint,
            )
        }
    }
}

@Composable
fun CodigoPill(text: String, color: Color = Color(0xFF22DDF2), onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = color,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color(0xFF22DDF2).copy(alpha = 0.5f))
    ) {
        Text(
            text = text,
            color = if (color == Color(0xFF22DDF2)) Color.Black else Color.White,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
