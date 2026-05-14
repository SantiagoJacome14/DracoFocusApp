package co.edu.unab.dracofocusapp.ui.Lecciones.Grupales

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import co.edu.unab.dracofocusapp.DracoFocusApplication
import co.edu.unab.dracofocusapp.data.remote.CreateGroupRequest
import co.edu.unab.dracofocusapp.data.remote.GroupMemberDto
import co.edu.unab.dracofocusapp.data.remote.GroupSessionResponse
import co.edu.unab.dracofocusapp.data.remote.JoinGroupRequest
import kotlinx.coroutines.launch

// ─── Colores Draco ────────────────────────────────────────────────────────────
private val DracoCyan    = Color(0xFF22DDF2)
private val DracoNavy    = Color(0xFF0B132B)
private val DracoMidNight = Color(0xFF1C2541)
private val DracoCard    = Color(0xFF1E2D4A)
private val DracoError   = Color(0xFFFF6B6B)
private val DracoSuccess = Color(0xFF4ADE80)
private val DracoText    = Color(0xFFB3C8E0)

@Composable
fun LeccionesGrupalesScreen(
    navController: NavController,
    onBack: () -> Unit,
) {
    val context      = LocalContext.current
    val app          = context.applicationContext as DracoFocusApplication
    val apiService   = app.apiService
    val scope        = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val scrollState  = rememberScrollState()

    // ── Estado UI ────────────────────────────────────────────────────────────
    var isLoading        by remember { mutableStateOf(false) }
    var joinCode         by remember { mutableStateOf("") }
    var errorMessage     by remember { mutableStateOf<String?>(null) }
    var successMessage   by remember { mutableStateOf<String?>(null) }
    var groupSession     by remember { mutableStateOf<GroupSessionResponse?>(null) }

    // ── Fondo ────────────────────────────────────────────────────────────────
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DracoNavy, DracoMidNight)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
                .padding(top = 48.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ── Ícono + Título ────────────────────────────────────────────────
            Icon(
                imageVector = Icons.Default.Group,
                contentDescription = null,
                tint = DracoCyan,
                modifier = Modifier.size(56.dp)
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Modo Grupo",
                color = DracoCyan,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Crea una sala o únete con el código de tu compañero",
                color = DracoText,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(Modifier.height(32.dp))

            // ── SECCIÓN: Crear grupo ──────────────────────────────────────────
            GroupCard(title = "Crear un grupo nuevo") {
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        errorMessage   = null
                        successMessage = null
                        isLoading      = true
                        scope.launch {
                            isLoading = true
                            errorMessage = null
                            successMessage = null

                            var codeToNavigate: String? = null

                            try {
                                val response = apiService.createGroup(
                                    CreateGroupRequest(title = "Sala Draco")
                                )

                                if (response.isSuccessful && response.body() != null) {
                                    groupSession = response.body()
                                    successMessage = "¡Grupo creado! Comparte el código con tus compañeros."

                                    codeToNavigate = response.body()!!.code
                                } else {
                                    val errBody = response.errorBody()?.string()
                                    errorMessage = parseLaravelError(errBody)
                                        ?: "Error ${response.code()}: no se pudo crear el grupo."
                                }
                            } catch (e: Exception) {
                                errorMessage = "Sin conexión: ${e.message}"
                            } finally {
                                isLoading = false
                            }

                            codeToNavigate?.let { code ->
                                navController.navigate("seleccion_rol_grupo/$code")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DracoCyan,
                        contentColor   = Color.Black
                    ),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color    = Color.Black,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Crear grupo", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── SECCIÓN: Unirse por código ────────────────────────────────────
            GroupCard(title = "Unirme con un código") {
                OutlinedTextField(
                    value = joinCode,
                    onValueChange = {
                        joinCode     = it.uppercase().take(10)
                        errorMessage = null
                    },
                    modifier          = Modifier.fillMaxWidth(),
                    label             = { Text("Código del grupo", color = DracoText) },
                    singleLine        = true,
                    shape             = RoundedCornerShape(12.dp),
                    keyboardOptions   = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Characters,
                        imeAction      = ImeAction.Done
                    ),
                    keyboardActions   = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = DracoCyan,
                        unfocusedBorderColor = DracoText.copy(alpha = 0.4f),
                        focusedLabelColor    = DracoCyan,
                        cursorColor          = DracoCyan,
                        focusedTextColor     = Color.White,
                        unfocusedTextColor   = Color.White,
                    )
                )

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = {
                        focusManager.clearFocus()
                        val trimmed = joinCode.trim()
                        if (trimmed.isBlank()) {
                            errorMessage = "Escribe el código del grupo."
                            return@Button
                        }
                        errorMessage   = null
                        successMessage = null
                        isLoading      = true
                        scope.launch {
                            try {
                                val response = apiService.joinGroup(JoinGroupRequest(code = trimmed))
                                if (response.isSuccessful && response.body() != null) {
                                    groupSession   = response.body()
                                    successMessage = "Te uniste al grupo correctamente."
                                    joinCode       = ""
                                    // Navegar a selección de rol
                                    val code = response.body()!!.code
                                    navController.navigate("seleccion_rol_grupo/$code")
                                } else {
                                    val errBody = response.errorBody()?.string()
                                    errorMessage = parseLaravelError(errBody)
                                        ?: "Error ${response.code()}: código no válido o grupo no encontrado."
                                }
                            } catch (e: Exception) {
                                errorMessage = "Sin conexión: ${e.message}"
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DracoCyan.copy(alpha = 0.15f),
                        contentColor   = DracoCyan
                    ),
                    border = ButtonDefaults.outlinedButtonBorder,
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier    = Modifier.size(22.dp),
                            color       = DracoCyan,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Unirme con código", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }

            // ── Mensajes de error / éxito ─────────────────────────────────────
            AnimatedVisibility(visible = errorMessage != null, enter = fadeIn(), exit = fadeOut()) {
                errorMessage?.let { msg ->
                    Spacer(Modifier.height(16.dp))
                    StatusBanner(text = msg, isError = true)
                }
            }

            AnimatedVisibility(visible = successMessage != null, enter = fadeIn(), exit = fadeOut()) {
                successMessage?.let { msg ->
                    Spacer(Modifier.height(16.dp))
                    StatusBanner(text = msg, isError = false)
                }
            }

            // ── Tarjeta de resultado de grupo ─────────────────────────────────
            AnimatedVisibility(visible = groupSession != null, enter = fadeIn(), exit = fadeOut()) {
                groupSession?.let { session ->
                    Spacer(Modifier.height(20.dp))
                    GroupResultCard(session = session)
                }
            }

            Spacer(Modifier.height(32.dp))

            // ── Volver ───────────────────────────────────────────────────────
            TextButton(onClick = onBack) {
                Text("Volver", color = DracoText, fontSize = 15.sp)
            }
        }
    }
}

// ─── Tarjeta contenedora de sección ──────────────────────────────────────────
@Composable
private fun GroupCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(DracoCard)
            .border(1.dp, DracoCyan.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        Text(
            text       = title,
            color      = Color.White,
            fontSize   = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(14.dp))
        content()
    }
}

// ─── Banner de estado (error o éxito) ────────────────────────────────────────
@Composable
private fun StatusBanner(text: String, isError: Boolean) {
    val color = if (isError) DracoError else DracoSuccess
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!isError) {
            Icon(Icons.Default.Check, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(text = text, color = color, fontSize = 13.sp, lineHeight = 18.sp)
    }
}

// ─── Tarjeta con datos del grupo devuelto por el backend ─────────────────────
@Composable
private fun GroupResultCard(session: GroupSessionResponse) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(DracoCard)
            .border(1.dp, DracoCyan.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        // Código grande y copiable
        Text("Código del grupo", color = DracoText, fontSize = 12.sp)
        Spacer(Modifier.height(4.dp))
        Text(
            text       = session.code,
            color      = DracoCyan,
            fontSize   = 34.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 6.sp
        )

        Spacer(Modifier.height(12.dp))
        HorizontalDivider(color = DracoCyan.copy(alpha = 0.15f))
        Spacer(Modifier.height(12.dp))

        // Info básica
        InfoRow(label = "Título", value = session.title)
        InfoRow(label = "Estado", value = session.status.replaceFirstChar { it.uppercase() })
        session.lessonSlug?.let { InfoRow(label = "Lección", value = it) }

        // Lista de miembros si viene en la respuesta
        val members = session.members
        if (!members.isNullOrEmpty()) {
            Spacer(Modifier.height(16.dp))
            Text("Integrantes (${members.size})", color = DracoText, fontSize = 13.sp)
            Spacer(Modifier.height(8.dp))
            members.forEach { member -> MemberRow(member) }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = DracoText, fontSize = 13.sp)
        Text(value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun MemberRow(member: GroupMemberDto) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            tint = DracoCyan.copy(alpha = 0.7f),
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(8.dp))
        Column {
            Text(member.name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            member.role?.let {
                Text(it, color = DracoText, fontSize = 11.sp)
            }
        }
    }
}

// ─── Helper: extrae el mensaje de error de Laravel ───────────────────────────
private fun parseLaravelError(body: String?): String? {
    if (body.isNullOrBlank()) return null
    return try {
        // Busca "message":"..." en la respuesta JSON sin dependencia extra
        val match = Regex(""""message"\s*:\s*"([^"]+)"""").find(body)
        match?.groupValues?.getOrNull(1)
    } catch (_: Exception) {
        null
    }
}
