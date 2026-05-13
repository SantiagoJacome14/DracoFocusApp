package co.edu.unab.dracofocusapp.ui.Perfil

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import co.edu.unab.dracofocusapp.DracoFocusApplication
import co.edu.unab.dracofocusapp.R
import co.edu.unab.dracofocusapp.ui.components.ModernTopBar
import co.edu.unab.dracofocusapp.auth.TokenManager
import co.edu.unab.dracofocusapp.viewmodel.ProfileViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import android.util.Log

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyProfileScreen(
    onBack: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val app = context.applicationContext as DracoFocusApplication
    val profileVm = viewModel<ProfileViewModel>(
        factory = ProfileViewModel.factory(app.apiService)
    )
    val profileState by profileVm.state.collectAsState()

    // Refresca XP cada vez que el usuario regresa a esta pantalla
    LaunchedEffect(Unit) { profileVm.load() }

    var notificationsEnabled by remember { mutableStateOf(true) }
    var soundEnabled by remember { mutableStateOf(true) }

    val gradientBackground = Brush.verticalGradient(
        listOf(Color(0xFF0B132B), Color(0xFF1C2541))
    )

    Scaffold(
        modifier = Modifier.statusBarsPadding(),
        topBar = {
            ModernTopBar(
                title = "Mi Perfil",
                showBackButton = true,
                onBackClick = onBack
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
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
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

                        Text(
                            text = profileState.name.ifBlank { "Cargando..." },
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = profileState.email.ifBlank { "" },
                            color = Color(0xFFB0BEC5),
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        if (profileState.isLoading && !profileState.hasData) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(28.dp),
                                color = Color(0xFF22DDF2),
                                strokeWidth = 3.dp,
                            )
                        } else {
                            Text(
                                text = "Nivel ${profileState.level}",
                                color = Color(0xFF22DDF2),
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                            )
                            Text(
                                text = "${profileState.totalXp} XP",
                                color = Color.White,
                                fontSize = 14.sp,
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            LinearProgressIndicator(
                                progress = { profileState.currentLevelXp / 200f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(50)),
                                color = Color(0xFF22DDF2),
                                trackColor = Color(0xFF1C2541),
                            )
                            Text(
                                text = "${profileState.currentLevelXp} / 200 XP → Nivel ${profileState.level + 1}",
                                color = Color(0xFFB0BEC5),
                                fontSize = 11.sp,
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                StatItem("${profileState.currentStreak}", "Racha")
                                StatItem("${profileState.completedLessonsCount}", "Cursos")
                                StatItem("${profileState.dailyGoal} XP", "Meta/día")
                            }
                        }
                    }
                }

                if (profileState.error != null) {
                    OutlinedButton(
                        onClick = { profileVm.load() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF22DDF2)),
                    ) {
                        Text("Reintentar conexión")
                    }
                }

                if (profileState.isRefreshing) {
                    Text(
                        text = "Actualizando...",
                        color = Color(0xFF22DDF2),
                        fontSize = 11.sp,
                    )
                }

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
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        // Navegar inmediatamente — cleanup en background
                        onLogout()
                        Firebase.auth.signOut()
                        scope.launch {
                            try {
                                TokenManager(context).clearAuthData()
                                Log.d("LOGOUT", "TokenManager limpiado")
                            } catch (e: Exception) {
                                Log.e("LOGOUT", "Error limpiando token: ${e.message}")
                            }
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

                Spacer(modifier = Modifier.height(50.dp))
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
    icon: ImageVector,
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
