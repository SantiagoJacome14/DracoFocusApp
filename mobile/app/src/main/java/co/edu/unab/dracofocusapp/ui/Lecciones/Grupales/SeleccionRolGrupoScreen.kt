package co.edu.unab.dracofocusapp.ui.Lecciones.Grupales

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.unab.dracofocusapp.DracoFocusApplication
import co.edu.unab.dracofocusapp.data.remote.GroupMemberDto
import co.edu.unab.dracofocusapp.data.remote.GroupSessionResponse
import co.edu.unab.dracofocusapp.data.remote.SetGroupRoleRequest
import kotlinx.coroutines.launch

// ─── Colores Draco ────────────────────────────────────────────────────────────
private val DracoCyan     = Color(0xFF22DDF2)
private val DracoNavy     = Color(0xFF0B132B)
private val DracoMidNight = Color(0xFF1C2541)
private val DracoCard     = Color(0xFF1E2D4A)
private val DracoError    = Color(0xFFFF6B6B)
private val DracoSuccess  = Color(0xFF4ADE80)
private val DracoText     = Color(0xFFB3C8E0)
private val DracoDisabled = Color(0xFF3A4A5C)

// ─── Definición de roles ──────────────────────────────────────────────────────
private data class RoleInfo(
    val key: String,          // valor que va al backend: "analyst" | "programmer"
    val label: String,        // nombre visible
    val description: String,
    val icon: ImageVector,
)

private val ROLES = listOf(
    RoleInfo(
        key         = "analyst",
        label       = "Analista",
        description = "Diseña el diagrama de flujo de la solución.",
        icon        = Icons.Default.Analytics,
    ),
    RoleInfo(
        key         = "programmer",
        label       = "Programador",
        description = "Codifica la solución en Python.",
        icon        = Icons.Default.Code,
    ),
)

@Composable
fun SeleccionRolGrupoScreen(
    groupCode: String,
    onBack: () -> Unit,
) {
    val context    = LocalContext.current
    val app        = context.applicationContext as DracoFocusApplication
    val apiService = app.apiService
    val scope      = rememberCoroutineScope()
    val scroll     = rememberScrollState()

    // ── Estado ────────────────────────────────────────────────────────────────
    var isLoadingGroup  by remember { mutableStateOf(true) }
    var isSavingRole    by remember { mutableStateOf(false) }
    var errorMessage    by remember { mutableStateOf<String?>(null) }
    var session         by remember { mutableStateOf<GroupSessionResponse?>(null) }
    var loadError       by remember { mutableStateOf<String?>(null) }

    // Cargar estado del grupo al entrar
    LaunchedEffect(groupCode) {
        isLoadingGroup = true
        loadError      = null
        try {
            val response = apiService.getGroup(groupCode)
            if (response.isSuccessful) {
                session = response.body()
            } else {
                loadError = "No se pudo cargar el grupo (${response.code()})"
            }
        } catch (e: Exception) {
            loadError = "Sin conexión: ${e.message}"
        } finally {
            isLoadingGroup = false
        }
    }

    // ── Fondo ────────────────────────────────────────────────────────────────
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DracoNavy, DracoMidNight)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(horizontal = 24.dp)
                .padding(top = 48.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            // ── Encabezado ───────────────────────────────────────────────────
            Text(
                text       = "Escoge tu rol",
                color      = DracoCyan,
                fontSize   = 28.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text      = "Cada estudiante cumple una misión diferente.\nEl Analista diseña la solución y el Programador la codifica.",
                color     = DracoText,
                fontSize  = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
            )

            Spacer(Modifier.height(8.dp))

            // Código del grupo (referencia visual)
            Text(
                text       = "Grupo: $groupCode",
                color      = DracoCyan.copy(alpha = 0.7f),
                fontSize   = 13.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.sp,
            )

            Spacer(Modifier.height(28.dp))

            // ── Loading inicial ───────────────────────────────────────────────
            if (isLoadingGroup) {
                CircularProgressIndicator(color = DracoCyan, modifier = Modifier.size(40.dp))
                Spacer(Modifier.height(12.dp))
                Text("Cargando estado del grupo…", color = DracoText, fontSize = 13.sp)
            }

            // ── Error de carga ───────────────────────────────────────────────
            loadError?.let { err ->
                StatusBannerRole(text = err, isError = true)
                Spacer(Modifier.height(20.dp))
            }

            // ── Contenido principal ───────────────────────────────────────────
            session?.let { s ->
                val myRole      = s.myRole              // rol actual del usuario
                val members     = s.members ?: emptyList()

                // Muestra el rol actual si ya tiene uno
                if (myRole != null) {
                    val myLabel = ROLES.find { it.key == myRole }?.label ?: myRole
                    Spacer(Modifier.height(4.dp))
                    RoleBadge(label = "Tu rol actual: $myLabel")
                    Spacer(Modifier.height(20.dp))
                }

                // Tarjetas de rol
                ROLES.forEach { roleInfo ->
                    val takenByOther = isTakenByOther(members, roleInfo.key, s.myRole)
                    val isMyRole     = myRole == roleInfo.key

                    RoleCard(
                        roleInfo    = roleInfo,
                        isMyRole    = isMyRole,
                        takenByOther = takenByOther,
                        isSaving    = isSavingRole,
                        onSelect    = {
                            errorMessage = null
                            isSavingRole = true
                            scope.launch {
                                try {
                                    val resp = apiService.setGroupRole(
                                        code    = groupCode,
                                        request = SetGroupRoleRequest(role = roleInfo.key),
                                    )
                                    if (resp.isSuccessful && resp.body() != null) {
                                        session = resp.body()
                                    } else {
                                        val errBody = resp.errorBody()?.string()
                                        errorMessage = parseLaravelError(errBody)
                                            ?: "Error ${resp.code()}: no se pudo guardar el rol."
                                    }
                                } catch (e: Exception) {
                                    errorMessage = "Sin conexión: ${e.message}"
                                } finally {
                                    isSavingRole = false
                                }
                            }
                        }
                    )

                    Spacer(Modifier.height(14.dp))
                }

                // Guardando…
                AnimatedVisibility(visible = isSavingRole, enter = fadeIn(), exit = fadeOut()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Spacer(Modifier.height(4.dp))
                        CircularProgressIndicator(color = DracoCyan, modifier = Modifier.size(28.dp), strokeWidth = 2.5.dp)
                        Spacer(Modifier.height(6.dp))
                        Text("Guardando rol…", color = DracoText, fontSize = 12.sp)
                    }
                }

                // Error de guardado
                AnimatedVisibility(visible = errorMessage != null, enter = fadeIn(), exit = fadeOut()) {
                    errorMessage?.let { msg ->
                        Spacer(Modifier.height(8.dp))
                        StatusBannerRole(text = msg, isError = true)
                    }
                }

                // Confirmación cuando ya tiene rol asignado
                if (myRole == "analyst" || myRole == "programmer") {
                    val myLabel = ROLES.find { it.key == myRole }?.label ?: myRole
                    Spacer(Modifier.height(12.dp))
                    StatusBannerRole(
                        text    = "✓ Tu rol \"$myLabel\" ha sido guardado. Espera a que el grupo esté listo.",
                        isError = false,
                    )
                }

                // Lista de miembros con sus roles actuales
                if (members.isNotEmpty()) {
                    Spacer(Modifier.height(24.dp))
                    MembersCard(members = members)
                }
            }

            Spacer(Modifier.height(32.dp))

            TextButton(onClick = onBack) {
                Text("Volver", color = DracoText, fontSize = 15.sp)
            }
        }
    }
}

// ─── Tarjeta de rol ───────────────────────────────────────────────────────────
@Composable
private fun RoleCard(
    roleInfo    : RoleInfo,
    isMyRole    : Boolean,
    takenByOther: Boolean,
    isSaving    : Boolean,
    onSelect    : () -> Unit,
) {
    val borderColor = when {
        isMyRole     -> DracoSuccess
        takenByOther -> DracoDisabled.copy(alpha = 0.5f)
        else         -> DracoCyan.copy(alpha = 0.4f)
    }
    val bgColor = when {
        isMyRole     -> DracoSuccess.copy(alpha = 0.08f)
        takenByOther -> DracoDisabled.copy(alpha = 0.12f)
        else         -> DracoCard
    }
    val textColor = if (takenByOther && !isMyRole) DracoText.copy(alpha = 0.4f) else Color.White

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(bgColor)
            .border(1.5.dp, borderColor, RoundedCornerShape(18.dp))
            .padding(20.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (takenByOther && !isMyRole) Icons.Default.Lock else roleInfo.icon,
                contentDescription = null,
                tint = if (takenByOther && !isMyRole) DracoDisabled else DracoCyan,
                modifier = Modifier.size(32.dp),
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(roleInfo.label, color = textColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(3.dp))
                Text(roleInfo.description, color = DracoText.copy(if (takenByOther && !isMyRole) 0.4f else 1f), fontSize = 13.sp, lineHeight = 18.sp)
            }
            if (isMyRole) {
                Icon(Icons.Default.Check, contentDescription = "Tu rol", tint = DracoSuccess, modifier = Modifier.size(26.dp))
            }
        }

        if (!takenByOther || isMyRole) {
            Spacer(Modifier.height(14.dp))
            Button(
                onClick  = onSelect,
                modifier = Modifier.fillMaxWidth().height(46.dp),
                shape    = RoundedCornerShape(12.dp),
                enabled  = !isSaving && !isMyRole,
                colors   = ButtonDefaults.buttonColors(
                    containerColor        = if (isMyRole) DracoSuccess.copy(alpha = 0.25f) else DracoCyan,
                    contentColor          = if (isMyRole) DracoSuccess else Color.Black,
                    disabledContainerColor = DracoCyan.copy(alpha = 0.15f),
                    disabledContentColor  = DracoCyan.copy(alpha = 0.5f),
                ),
            ) {
                Text(
                    text       = if (isMyRole) "Rol seleccionado" else "Ser ${roleInfo.label}",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 15.sp,
                )
            }
        } else {
            Spacer(Modifier.height(10.dp))
            Text(
                "Rol tomado por otro estudiante",
                color    = DracoError.copy(alpha = 0.8f),
                fontSize = 12.sp,
            )
        }
    }
}

// ─── Badge de rol actual ──────────────────────────────────────────────────────
@Composable
private fun RoleBadge(label: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(DracoCyan.copy(alpha = 0.12f))
            .border(1.dp, DracoCyan.copy(alpha = 0.4f), RoundedCornerShape(50.dp))
            .padding(horizontal = 16.dp, vertical = 6.dp),
    ) {
        Text(label, color = DracoCyan, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

// ─── Banner de estado ─────────────────────────────────────────────────────────
@Composable
private fun StatusBannerRole(text: String, isError: Boolean) {
    val color = if (isError) DracoError else DracoSuccess
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.10f))
            .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = text, color = color, fontSize = 13.sp, lineHeight = 18.sp)
    }
}

// ─── Tarjeta de miembros ──────────────────────────────────────────────────────
@Composable
private fun MembersCard(members: List<GroupMemberDto>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DracoCard)
            .border(1.dp, DracoCyan.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
            .padding(16.dp),
    ) {
        Text("Integrantes del grupo", color = DracoText, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(10.dp))
        members.forEach { member ->
            val roleLabel = when (member.role) {
                "analyst"    -> "Analista"
                "programmer" -> "Programador"
                "leader"     -> "Líder"
                else         -> member.role ?: "Sin rol"
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically,
            ) {
                Text(member.name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Text(roleLabel, color = DracoCyan, fontSize = 12.sp)
            }
        }
    }
}

// ─── Helper: extrae mensaje de error de Laravel ───────────────────────────────
private fun parseLaravelError(body: String?): String? {
    if (body.isNullOrBlank()) return null
    return try {
        Regex(""""message"\s*:\s*"([^"]+)"""").find(body)?.groupValues?.getOrNull(1)
    } catch (_: Exception) { null }
}

// ─── Helper: ¿está tomado ese rol por otro miembro (que no soy yo)? ───────────
private fun isTakenByOther(
    members : List<GroupMemberDto>,
    roleKey : String,
    myRole  : String?,
): Boolean {
    if (myRole == roleKey) return false          // yo tengo ese rol → no está "bloqueado"
    return members.any { it.role == roleKey }    // otro lo tiene
}
