package co.edu.unab.dracofocusapp.ui.Lecciones.Grupales

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.unab.dracofocusapp.DracoFocusApplication
import co.edu.unab.dracofocusapp.data.remote.ApiService
import co.edu.unab.dracofocusapp.data.remote.GroupSessionResponse
import co.edu.unab.dracofocusapp.data.remote.SubmitGroupSubmissionRequest
import kotlinx.coroutines.launch

// ─── Colores Draco ────────────────────────────────────────────────────────────
private val DracoCyan     = Color(0xFF22DDF2)
private val DracoNavy     = Color(0xFF0B132B)
private val DracoMidNight = Color(0xFF1C2541)
private val DracoCard     = Color(0xFF1E2D4A)
private val DracoText     = Color(0xFFB3C8E0)
private val DracoSuccess  = Color(0xFF4ADE80)
private val DracoError    = Color(0xFFFF6B6B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupExerciseScreen(
    groupCode: String,
    onBack: () -> Unit
) {
    val context    = LocalContext.current
    val app        = context.applicationContext as DracoFocusApplication
    val apiService = app.apiService
    val scroll     = rememberScrollState()

    var isLoading   by remember { mutableStateOf(true) }
    var session     by remember { mutableStateOf<GroupSessionResponse?>(null) }
    var loadError   by remember { mutableStateOf<String?>(null) }
    var showSuccess by remember { mutableStateOf(false) }

    // Cargar estado del grupo
    LaunchedEffect(groupCode) {
        isLoading = true
        loadError = null
        try {
            val response = apiService.getGroup(groupCode)
            if (response.isSuccessful) {
                session = response.body()
            } else {
                loadError = "Error al cargar la misión (${response.code()})"
            }
        } catch (e: Exception) {
            loadError = "Sin conexión: ${e.message}"
        } finally {
            isLoading = false
        }
    }

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
                .padding(top = 16.dp, bottom = 32.dp)
        ) {
            // Toolbar simple
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                }
                Text(
                    "Misión Grupal",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth().padding(top = 100.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = DracoCyan)
                }
            } else if (loadError != null) {
                ErrorState(message = loadError!!, onBack = onBack)
            } else {
                session?.let { s ->
                    val myRole = s.myRole
                    
                    Text(
                        "Código del grupo: $groupCode",
                        color = DracoCyan.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    )
                    
                    Spacer(Modifier.height(16.dp))

                    when (myRole) {
                        "analyst" -> AnalystContent(onFinish = { showSuccess = true })
                        "programmer" -> ProgrammerContent(
                            apiService = apiService,
                            groupCode = groupCode,
                            onFinish = { showSuccess = true }
                        )
                        else -> InvalidRoleState(onBack = onBack)
                    }
                }
            }
        }

        // Dialogo de éxito local
        if (showSuccess) {
            AlertDialog(
                onDismissRequest = { showSuccess = false },
                containerColor = DracoCard,
                title = { Text("¡Excelente trabajo en equipo!", color = DracoCyan) },
                text = { 
                    Text(
                        "Draco está orgulloso de tu esfuerzo. Tu profesor revisará la actividad y les dará retroalimentación pronto.",
                        color = Color.White
                    ) 
                },
                confirmButton = {
                    TextButton(onClick = { showSuccess = false }) {
                        Text("Entendido", color = DracoCyan)
                    }
                }
            )
        }
    }
}

@Composable
private fun AnalystContent(onFinish: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DracoCard),
        shape = RoundedCornerShape(16.dp),
        border = BoxDefaults.BorderStroke
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Analytics, contentDescription = null, tint = DracoCyan, modifier = Modifier.size(32.dp))
                Spacer(Modifier.width(12.dp))
                Text("Rol: Analista", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(16.dp))
            Text(
                "Tu misión: Diseñar el diagrama de flujo.",
                color = DracoCyan,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Diseña una solución lógica para el problema planteado. En la siguiente fase podrás subir tu diagrama como imagen para que el programador pueda verlo.",
                color = DracoText,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {},
                enabled = false,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    disabledContainerColor = DracoCyan.copy(alpha = 0.1f),
                    disabledContentColor = DracoCyan.copy(alpha = 0.4f)
                )
            ) {
                Text("Subir imagen próximamente", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ProgrammerContent(
    apiService: ApiService,
    groupCode: String,
    onFinish: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var code by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DracoCard),
        shape = RoundedCornerShape(16.dp),
        border = BoxDefaults.BorderStroke
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Code, contentDescription = null, tint = DracoCyan, modifier = Modifier.size(32.dp))
                Spacer(Modifier.width(12.dp))
                Text("Rol: Programador", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(16.dp))
            Text(
                "Tu misión: Escribir la solución en Python.",
                color = DracoCyan,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Traduce la lógica del analista a código Python limpio y funcional.",
                color = DracoText,
                fontSize = 14.sp
            )
            Spacer(Modifier.height(20.dp))
            
            // Editor de código simulado
            OutlinedTextField(
                value = code,
                onValueChange = { code = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                textStyle = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    color = Color.White,
                    fontSize = 14.sp
                ),
                placeholder = { Text("# Escribe tu código aquí...", color = DracoText.copy(alpha = 0.5f), fontFamily = FontFamily.Monospace) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DracoCyan,
                    unfocusedBorderColor = DracoCyan.copy(alpha = 0.3f),
                    cursorColor = DracoCyan
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = !isSubmitting
            )

            errorMessage?.let { msg ->
                Spacer(Modifier.height(8.dp))
                Text(msg, color = DracoError, fontSize = 13.sp)
            }

            Spacer(Modifier.height(24.dp))
            
            Button(
                onClick = {
                    if (code.isBlank()) {
                        errorMessage = "El código no puede estar vacío."
                        return@Button
                    }
                    errorMessage = null
                    isSubmitting = true
                    
                    scope.launch {
                        try {
                            val request = SubmitGroupSubmissionRequest(
                                submissionType = "python_code",
                                codeText = code
                            )
                            val response = apiService.submitGroupSubmission(groupCode, request)
                            if (response.isSuccessful) {
                                onFinish()
                            } else {
                                errorMessage = "Error al guardar: ${response.code()}"
                            }
                        } catch (e: Exception) {
                            errorMessage = "Sin conexión: ${e.message}"
                        } finally {
                            isSubmitting = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !isSubmitting,
                colors = ButtonDefaults.buttonColors(
                    containerColor = DracoCyan,
                    contentColor = Color.Black,
                    disabledContainerColor = DracoCyan.copy(alpha = 0.3f),
                    disabledContentColor = Color.Black.copy(alpha = 0.5f)
                )
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
                } else {
                    Text("Finalizar entrega", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun InvalidRoleState(onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.Info, contentDescription = null, tint = DracoError, modifier = Modifier.size(64.dp))
        Spacer(Modifier.height(16.dp))
        Text(
            "Primero debes escoger un rol de Analista o Programador.",
            color = Color.White,
            textAlign = TextAlign.Center,
            fontSize = 16.sp
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onBack,
            colors = ButtonDefaults.buttonColors(containerColor = DracoCyan, contentColor = Color.Black)
        ) {
            Text("Volver a selección de rol")
        }
    }
}

@Composable
private fun ErrorState(message: String, onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(message, color = DracoError, textAlign = TextAlign.Center)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onBack) {
            Text("Volver")
        }
    }
}

private object BoxDefaults {
    val BorderStroke = androidx.compose.foundation.BorderStroke(1.dp, DracoCyan.copy(alpha = 0.2f))
}
