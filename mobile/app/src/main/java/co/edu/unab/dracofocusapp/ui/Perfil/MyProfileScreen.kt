package co.edu.unab.dracofocusapp.ui.Perfil

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import co.edu.unab.dracofocusapp.DracoFocusApplication
import co.edu.unab.dracofocusapp.R
import co.edu.unab.dracofocusapp.data.local.datastore.SettingsDataStore
import co.edu.unab.dracofocusapp.ui.components.ModernTopBar
import co.edu.unab.dracofocusapp.auth.TokenManager
import co.edu.unab.dracofocusapp.viewmodel.ProfileViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import android.util.Log
import java.io.ByteArrayOutputStream

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

    val settingsDataStore = remember { SettingsDataStore(context) }
    val notificationSoundEnabled by settingsDataStore.notificationSoundEnabled.collectAsState(initial = true)
    var soundEnabled by remember { mutableStateOf(true) }
    var showEditProfileDialog by remember { mutableStateOf(false) }

    val avatarPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch {
            try {
                // Las fotos de cámara pueden pesar varios MB; las comprimimos antes
                // de subirlas (más rápido y evita el 413/422 por tamaño en el server).
                val bytes = compressImageForUpload(context, uri)
                if (bytes != null) {
                    profileVm.uploadAvatar(bytes, "avatar.jpg", "image/jpeg") { success, errorMessage ->
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                if (success) "Foto de perfil actualizada" else (errorMessage ?: "No se pudo subir la foto")
                            )
                        }
                    }
                } else {
                    snackbarHostState.showSnackbar("No se pudo leer la imagen")
                }
            } catch (e: Exception) {
                Log.e("PROFILE", "Error procesando la imagen elegida", e)
                snackbarHostState.showSnackbar("No se pudo procesar la imagen")
            }
        }
    }

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
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .padding(bottom = 8.dp)
                        ) {
                            if (profileState.avatarUrl.isNullOrBlank()) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_avatar),
                                    contentDescription = "Avatar",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                        .clickable { avatarPickerLauncher.launch("image/*") },
                                )
                            } else {
                                AsyncImage(
                                    model = profileState.avatarUrl,
                                    contentDescription = "Avatar",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                        .clickable { avatarPickerLauncher.launch("image/*") },
                                )
                            }

                            if (profileState.isUploadingAvatar) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = 0.5f)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(28.dp), color = Color(0xFF22DDF2), strokeWidth = 3.dp)
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .size(26.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF22DDF2))
                                        .clickable { avatarPickerLauncher.launch("image/*") },
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(Icons.Default.CameraAlt, contentDescription = "Cambiar foto", tint = Color.Black, modifier = Modifier.size(14.dp))
                                }
                            }
                        }

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

                        if (!profileState.specialty.isNullOrBlank() || !profileState.location.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                if (!profileState.specialty.isNullOrBlank()) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Icon(Icons.Default.School, contentDescription = null, tint = Color(0xFF22DDF2), modifier = Modifier.size(14.dp))
                                        Text(profileState.specialty!!, color = Color(0xFFB0BEC5), fontSize = 12.sp)
                                    }
                                }
                                if (!profileState.location.isNullOrBlank()) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF22DDF2), modifier = Modifier.size(14.dp))
                                        Text(profileState.location!!, color = Color(0xFFB0BEC5), fontSize = 12.sp)
                                    }
                                }
                            }
                        }

                        if (!profileState.bio.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = profileState.bio!!,
                                color = Color(0xFFDCE4EE),
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center,
                            )
                        }

                        val hasSocialLinks = !profileState.githubUrl.isNullOrBlank() ||
                            !profileState.websiteUrl.isNullOrBlank()
                        if (hasSocialLinks) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                                profileState.websiteUrl?.takeIf { it.isNotBlank() }?.let { url ->
                                    SocialLinkIcon(url = url, icon = Icons.Default.CameraAlt, contentDescription = "Instagram")
                                }
                                profileState.githubUrl?.takeIf { it.isNotBlank() }?.let { url ->
                                    SocialLinkIcon(url = url, icon = Icons.Default.Code, contentDescription = "GitHub")
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.clickable { showEditProfileDialog = true },
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, tint = Color(0xFF22DDF2), modifier = Modifier.size(14.dp))
                            Text("Editar perfil", color = Color(0xFF22DDF2), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

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
                        title = "Sonido de notificaciones",
                        description = "Los avisos de cambio de fase siempre se muestran; esto solo controla si suenan",
                        isChecked = notificationSoundEnabled,
                        onCheckedChange = { enabled ->
                            scope.launch { settingsDataStore.setNotificationSoundEnabled(enabled) }
                        }
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

    if (showEditProfileDialog) {
        EditProfileDialog(
            initialBio = profileState.bio ?: "",
            initialSpecialty = profileState.specialty ?: "",
            initialLocation = profileState.location ?: "",
            initialGithub = profileState.githubUrl ?: "",
            initialLinkedin = profileState.linkedinUrl ?: "",
            initialWebsite = profileState.websiteUrl ?: "",
            isSaving = profileState.isSavingProfile,
            onDismiss = { showEditProfileDialog = false },
            onSave = { bio, specialty, location, github, linkedin, website ->
                profileVm.updateProfile(bio, specialty, location, github, linkedin, website) { success, errorMessage ->
                    if (success) {
                        showEditProfileDialog = false
                        scope.launch { snackbarHostState.showSnackbar("Perfil actualizado") }
                    } else {
                        scope.launch { snackbarHostState.showSnackbar(errorMessage ?: "No se pudo guardar") }
                    }
                }
            },
        )
    }
}

@Composable
fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(label, color = Color(0xFFB0BEC5), fontSize = 13.sp)
    }
}

/**
 * Redimensiona y comprime la imagen elegida antes de subirla: las fotos de
 * cámara suelen pesar varios MB, más de lo necesario para un avatar.
 */
private fun compressImageForUpload(context: Context, uri: Uri, maxDimension: Int = 800, quality: Int = 85): ByteArray? {
    val original = context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) } ?: return null

    val scale = minOf(maxDimension.toFloat() / original.width, maxDimension.toFloat() / original.height, 1f)
    val scaled = if (scale < 1f) {
        Bitmap.createScaledBitmap(original, (original.width * scale).toInt(), (original.height * scale).toInt(), true)
    } else {
        original
    }

    val outputStream = ByteArrayOutputStream()
    scaled.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
    if (scaled !== original) original.recycle()
    scaled.recycle()

    return outputStream.toByteArray()
}

@Composable
fun SocialLinkIcon(url: String, icon: ImageVector, contentDescription: String) {
    val context = LocalContext.current
    Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        tint = Color(0xFF22DDF2),
        modifier = Modifier
            .size(22.dp)
            .clickable {
                val normalizedUrl = if (url.startsWith("http://") || url.startsWith("https://")) url else "https://$url"
                try {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(normalizedUrl)))
                } catch (e: Exception) {
                    Log.e("PROFILE", "No se pudo abrir el enlace: $url", e)
                }
            },
    )
}

@Composable
fun EditProfileDialog(
    initialBio: String,
    initialSpecialty: String,
    initialLocation: String,
    initialGithub: String,
    initialLinkedin: String,
    initialWebsite: String,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSave: (bio: String, specialty: String, location: String, github: String, linkedin: String, website: String) -> Unit,
) {
    var bio by remember { mutableStateOf(initialBio) }
    var specialty by remember { mutableStateOf(initialSpecialty) }
    var location by remember { mutableStateOf(initialLocation) }
    var github by remember { mutableStateOf(initialGithub) }
    var linkedin by remember { mutableStateOf(initialLinkedin) }
    var website by remember { mutableStateOf(initialWebsite) }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Color(0xFF22DDF2),
        unfocusedBorderColor = Color(0xFF1C2541),
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        cursorColor = Color(0xFF22DDF2),
        focusedLabelColor = Color(0xFF22DDF2),
        unfocusedLabelColor = Color(0xFFB0BEC5),
    )

    AlertDialog(
        onDismissRequest = { if (!isSaving) onDismiss() },
        containerColor = Color(0xFF0F1A2A),
        title = { Text("Editar perfil", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                OutlinedTextField(
                    value = bio,
                    onValueChange = { if (it.length <= 280) bio = it },
                    label = { Text("Bio") },
                    minLines = 2,
                    maxLines = 4,
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = specialty,
                    onValueChange = { specialty = it },
                    label = { Text("Carrera / Especialidad") },
                    placeholder = { Text("Ej: Ingeniería de Sistemas") },
                    singleLine = true,
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Ubicación") },
                    singleLine = true,
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = website,
                    onValueChange = { website = it },
                    label = { Text("Instagram (usuario o link)") },
                    placeholder = { Text("Ej: instagram.com/tu_usuario") },
                    singleLine = true,
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = github,
                    onValueChange = { github = it },
                    label = { Text("Conectar con GitHub") },
                    placeholder = { Text("Ej: github.com/tu_usuario") },
                    singleLine = true,
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(bio, specialty, location, github, linkedin, website) },
                enabled = !isSaving,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22DDF2), contentColor = Color.Black),
            ) {
                if (isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.Black, strokeWidth = 2.dp)
                } else {
                    Text("Guardar", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isSaving) {
                Text("Cancelar", color = Color(0xFFB0BEC5))
            }
        },
    )
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
