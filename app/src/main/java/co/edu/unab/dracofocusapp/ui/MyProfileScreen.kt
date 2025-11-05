package co.edu.unab.dracofocusapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.unab.dracofocusapp.R
import co.edu.unab.dracofocusapp.ui.components.ModernTopBar
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyProfileScreen(
    onBackToMain: () -> Unit = {},
    onNavigateToAuth: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    // Obtener usuario desde firebase
    val user = Firebase.auth.currentUser
    val userName = user?.displayName ?: "Usuario"
    val userEmail = user?.email ?: "Sin correo"


    var notificationsEnabled by remember { mutableStateOf(true) }
    var soundEnabled by remember { mutableStateOf(true) }
    //Fondo degradado
    val gradientBackground = Brush.verticalGradient(
        listOf(Color(0xFF0B132B), Color(0xFF1C2541))
    )

    Scaffold(
        modifier = Modifier.statusBarsPadding(),
        topBar = {
            ModernTopBar(
                title = "Mi Perfil",
                showBackButton = false // sin flecha
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBackground)
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // 🧍 Avatar + Datos
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFF22DDF2), RoundedCornerShape(16.dp))
                        .background(Color(0xFF0F1A2A), RoundedCornerShape(16.dp))
                        .padding(24.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_avatar),
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .size(90.dp)
                                .padding(bottom = 8.dp)
                        )

                        Text(userName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(userEmail, color = Color(0xFFB0BEC5), fontSize = 14.sp)

                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Nivel 9", color = Color(0xFF22DDF2), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text("1670 XP", color = Color.White, fontSize = 14.sp)

                        Spacer(modifier = Modifier.height(18.dp))
                        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                            StatItem("9", "Racha")
                            StatItem("6", "Cursos")
                            StatItem("17h", "Estudio")
                        }
                    }
                }

                // ⚙️ CONFIGURACIÓN
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFF22DDF2), RoundedCornerShape(16.dp))
                        .background(Color(0xFF0F1A2A), RoundedCornerShape(16.dp))
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Configuración",
                        color = Color(0xFF22DDF2),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    SettingCard(
                        icon = Icons.Default.Notifications,
                        title = "Notificaciones",
                        description = "Recordatorios de estudio",
                        isChecked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it }
                    )

                    SettingCard(
                        icon = Icons.Default.VolumeUp,
                        title = "Efectos de sonido",
                        description = "Sonidos de las interacciones",
                        isChecked = soundEnabled,
                        onCheckedChange = { soundEnabled = it }
                    )

                    SettingCard(
                        icon = Icons.Default.Brightness4,
                        title = "Color del Tema",
                        description = "Personaliza tu experiencia",
                        showSwitch = false
                    )
                }

                // cerrar sesion
                Button(
                    onClick = {
                        Firebase.auth.signOut()
                        scope.launch {
                            snackbarHostState.showSnackbar("Sesión cerrada correctamente")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF22DDF2),
                        contentColor = Color.Black
                    )
                ) {
                    Text("Cerrar sesión", fontWeight = FontWeight.Bold)
                }

            }
        }
    }
}

@Composable
fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(label, color = Color(0xFFB0BEC5), fontSize = 13.sp)
    }
}

@Composable
fun SettingCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    isChecked: Boolean = false,
    onCheckedChange: (Boolean) -> Unit = {},
    showSwitch: Boolean = true
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                icon,
                contentDescription = title,
                tint = Color(0xFF22DDF2),
                modifier = Modifier.size(26.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold)
                Text(description, color = Color(0xFFB0BEC5), fontSize = 13.sp)
            }
        }
        if (showSwitch) {
            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFF22DDF2),
                    checkedTrackColor = Color(0xFF1C2541)
                )
            )
        }
    }
}