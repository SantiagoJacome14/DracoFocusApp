package co.edu.unab.dracofocusapp.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import co.edu.unab.dracofocusapp.R
import co.edu.unab.dracofocusapp.main.BottomNavItem

@Composable
fun HomeScreen(navController: NavController) {

    val gradientBackground = Brush.verticalGradient(
        listOf(Color(0xFF0B132B), Color(0xFF1C2541))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBackground)
            .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.fillMaxSize()
        ) {

            // CARD INICIAL
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(Color(0xFF0F1A2A)),
                border = BorderStroke(2.dp, Color(0xFF22DDF2))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(20.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.dragon_dracofocus1),
                        contentDescription = "Draco",
                        modifier = Modifier.size(180.dp)
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "¡Bienvenido de nuevo! 🎉",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = "Draco está feliz de verte. Cada estudio te acerca a completar su museo mágico 🪄",
                        color = Color(0xFFBBD8F4),
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // ---------- OBJETIVO DIARIO ----------
            Text("Objetivo Diario", color = Color(0xFF22DDF2), fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = 0.8f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(12.dp)),
                color = Color(0xFF22DDF2)
            )
            Spacer(Modifier.height(4.dp))
            Text("80% completado", color = Color.White, fontSize = 13.sp)

            Spacer(Modifier.height(30.dp))

            // ---------- BOTONES  ----------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MejorButton("ESTUDIAR", R.drawable.ic_study, Color(0xFF8A78FF)) {
                    navController.navigate(BottomNavItem.Pomodoro.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }

                MejorButton("LECCIONES", R.drawable.ic_book, Color(0xFF22DDF2)) {
                    navController.navigate("menu_lecciones") {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }

            Spacer(Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MejorButton("MUSEO", R.drawable.ic_museum, Color(0xFF22DDF2)) {
                    navController.navigate("museo_dracarte") {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }

                MejorButton("AVANCES", R.drawable.ic_calendar, Color(0xFF8A78FF)) {
                    navController.navigate(BottomNavItem.Avances.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        }
    }
}

@Composable
fun MejorButton(text: String, icon: Int, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .width(170.dp)
            .height(70.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }
    }
}
