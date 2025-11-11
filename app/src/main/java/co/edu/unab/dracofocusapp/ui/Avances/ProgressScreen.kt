package co.edu.unab.dracofocusapp.ui.Avances

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.unab.dracofocusapp.R
import androidx.navigation.NavController

@Composable
fun ProgressScreen(navController: NavController)
 {

    val gradientBackground = Brush.verticalGradient(
        listOf(Color(0xFF0B132B), Color(0xFF1C2541))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Excelente semana de estudio",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(12.dp))

        Image(
            painter = painterResource(id = R.drawable.dragon_dracofocus1),
            contentDescription = "Draco",
            modifier = Modifier.size(160.dp)
        )

        Spacer(Modifier.height(16.dp))

        NeonCard {
            Text("INSIGNIA DE DRACO", color = Color(0xFF22DDF2), fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("Completaste el 33% de tus lecciones esta semana y mejoraste tu tiempo de estudio.", color = Color.White)
        }

        Spacer(Modifier.height(22.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {

            NeonCard(modifier = Modifier.weight(1f)) {

                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Text("Minutos estudiados", color = Color.White, fontWeight = FontWeight.Bold)

                    Spacer(Modifier.height(6.dp))

                    Text("298 min", color = Color(0xFF22DDF2), fontSize = 20.sp, fontWeight = FontWeight.Bold)

                    Spacer(Modifier.height(10.dp))

                    BarChart(listOf(15, 45, 60, 35, 55, 80, 50))

                    Spacer(Modifier.height(10.dp))

                    // Ver más detalles
                    TextButton(onClick = { /* aquí luego abriremos otra screen */ }) {
                        Text("Ver más detalles", color = Color(0xFF22DDF2), fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(Modifier.width(15.dp))

            NeonCard(modifier = Modifier.weight(1f), ) {

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Progreso Lecciones", color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(10.dp))
                    CircularProgressWithText(0.33f)
                    Spacer(Modifier.height(10.dp))
                    // Ver más detalles
                    TextButton(onClick = { /* Aquí luego abriremos otra pantalla */ }) {
                        Text("Ver más detalles", color = Color(0xFF22DDF2), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(Modifier.height(22.dp))

        NeonCard {
            Text("Siguiente objetivo", color = Color(0xFF22DDF2), fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("Estudia 15 min de Estructura de Datos para alcanzar tu meta semanal.", color = Color.White)
        }
    }
}


// ---------- COMPONENTES ---------- //

@Composable
fun NeonCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = modifier
            .border(1.dp, Color(0xFF22DDF2), RoundedCornerShape(18.dp))
            .background(Color(0xFF0F1A2A), RoundedCornerShape(18.dp))
            .padding(16.dp),
        content = content
    )
}

@Composable
fun BarChart(values: List<Int>) {
    Canvas(modifier = Modifier.height(60.dp).fillMaxWidth()) {
        val max = values.maxOrNull() ?: 1
        val barWidth = size.width / (values.size * 2)
        values.forEachIndexed { index, value ->
            val height = (value.toFloat() / max) * size.height
            drawRect(
                color = Color(0xFF22DDF2),
                topLeft = Offset(index * barWidth * 2, size.height - height),
                size = Size(barWidth, height)
            )
        }
    }
}


@Composable
fun CircularProgressWithText(percentage: Float) {

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(120.dp)
    ) {

        Canvas(modifier = Modifier.matchParentSize()) {

            drawArc(
                color = Color(0x33FFFFFF),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(18f)
            )

            drawArc(
                color = Color(0xFF22DDF2),
                startAngle = -90f,
                sweepAngle = percentage * 360,
                useCenter = false,
                style = Stroke(18f)
            )
        }

        Text(
            text = "${(percentage * 100).toInt()}%",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
    }
}
