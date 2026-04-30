package co.edu.unab.dracofocusapp.ui.Lecciones

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import co.edu.unab.dracofocusapp.DracoFocusApplication
import co.edu.unab.dracofocusapp.R
import co.edu.unab.dracofocusapp.api.IAFeedbackManager
import co.edu.unab.dracofocusapp.cooperative.LessonCooperativeSync
import co.edu.unab.dracofocusapp.viewmodel.LessonProgressViewModel
import kotlinx.coroutines.launch
import java.util.Locale

internal fun selectableTipos(leccion: Leccion): List<RetoTipo> = buildList {
    add(RetoTipo.PUZZLE_OR_CODE_BLOCKS)
    val quizOk =
        leccion.opcionesQuiz.size >= 2 && leccion.indiceQuizCorrecto >= 0 && leccion.indiceQuizCorrecto < leccion.opcionesQuiz.size
    if (quizOk) add(RetoTipo.QUIZ_TECH)
    val fillOk = !(leccion.lineaCodigoHueca.isNullOrBlank() || leccion.respuestaRelleno.isNullOrBlank())
    if (fillOk) add(RetoTipo.FILL_LINE)
}.distinct()

@OptIn(
    ExperimentalLayoutApi::class,
    ExperimentalMaterial3Api::class,
)
@Composable
fun LeccionRetoScreen(
    navController: NavController,
    lessonId: String,
    coopRoomId: String?,
    onBack: () -> Unit,
) {
    val app = LocalContext.current.applicationContext as DracoFocusApplication
    val progressVm = viewModel<LessonProgressViewModel>(
        factory = LessonProgressViewModel.factory(
            repository = app.lessonProgressRepository,
            rewardManager = app.rewardManager,
        ),
    )

    val leccionBase = remember(lessonId) { LeccionRepository.getLeccionById(lessonId) }
        ?: run {
            Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0B132B))) {
                Text(
                    text = "Reto no disponible.",
                    color = Color.White,
                    modifier = Modifier.padding(24.dp),
                )
            }
            return
        }

    val tiposOk = remember(leccionBase.id) { selectableTipos(leccionBase) }.ifEmpty {
        listOf(RetoTipo.PUZZLE_OR_CODE_BLOCKS)
    }

    val tipoNombreGuardado = rememberSaveable(leccionBase.id, coopRoomId) {
        mutableStateOf(tiposOk.random().name)
    }
    val tipoReto =
        kotlin.runCatching { RetoTipo.valueOf(tipoNombreGuardado.value) }
            .getOrDefault(RetoTipo.PUZZLE_OR_CODE_BLOCKS)

    var piezasDisponibles by remember(leccionBase.id) {
        mutableStateOf(leccionBase.piezasDisponibles.shuffled())
    }
    var piezasSolucion by remember(leccionBase.id) { mutableStateOf(listOf<String>()) }

    var indiceSeleccionQuiz by remember(leccionBase.id) { mutableStateOf<Int?>(null) }
    var textoRelleno by remember(leccionBase.id) { mutableStateOf("") }

    val soloPct by progressVm.soloFundamentosProgress.collectAsState()

    LaunchedEffect(coopRoomId, soloPct) {
        val roomId = coopRoomId ?: return@LaunchedEffect
        LessonCooperativeSync.publishAggregateProgressPct(roomId, soloPct)
    }

    var partnerSummary by remember { mutableStateOf("") }

    DisposableEffect(coopRoomId, lessonId) {
        val room = coopRoomId
        val registration = room?.takeUnless { it.isBlank() }?.let { rid ->
            LessonCooperativeSync.attachSummariesListener(rid, lessonId) { texto ->
                partnerSummary = texto
            }
        }
        onDispose {
            registration?.let { attach ->
                val listener = attach.first
                attach.second.removeEventListener(listener)
            }
        }
    }

    fun buildPresenceSummary(): String {
        val extra = when (tipoReto) {
            RetoTipo.PUZZLE_OR_CODE_BLOCKS -> piezasSolucion.joinToString(" ").ifBlank { "(vacío)" }
            RetoTipo.QUIZ_TECH -> indiceSeleccionQuiz?.let { leccionBase.opcionesQuiz.getOrNull(it) } ?: "(sin opción)"
            RetoTipo.FILL_LINE -> textoRelleno.ifBlank { "____" }
        }
        return "${tipoReto.name} → $extra"
    }

    LaunchedEffect(
        coopRoomId,
        tipoReto.name,
        piezasSolucion,
        indiceSeleccionQuiz,
        textoRelleno,
        lessonId,
    ) {
        val roomId = coopRoomId ?: return@LaunchedEffect
        LessonCooperativeSync.publishLessonSummary(roomId, lessonId, buildPresenceSummary())
    }

    val ia = remember { IAFeedbackManager() }
    val scope = rememberCoroutineScope()

    val dracoCyan = Color(0xFF22DDF2)

    fun evalCorrecto(): Boolean {
        return when (tipoReto) {
            RetoTipo.PUZZLE_OR_CODE_BLOCKS ->
                piezasSolucion == leccionBase.solucionCorrecta

            RetoTipo.QUIZ_TECH ->
                indiceSeleccionQuiz != null && indiceSeleccionQuiz == leccionBase.indiceQuizCorrecto

            RetoTipo.FILL_LINE -> {
                val expected = normalize(leccionBase.respuestaRelleno ?: "")
                normalize(textoRelleno) == expected
            }
        }
    }

    var isSending by remember { mutableStateOf(false) }
    var overlayFeedback by remember { mutableStateOf<Pair<String, Boolean>?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val fondo =
        Brush.verticalGradient(listOf(Color(0xFF0B132B), Color(0xFF1C2541)))

    Box(
        Modifier
            .fillMaxSize()
            .background(fondo),
    ) {
        Column(
            Modifier
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(leccionBase.titulo.uppercase(), color = dracoCyan, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            AssistChip(onClick = {}, enabled = false, label = { Text(describeTipoBadge(tipoReto)) })
            Text(leccionBase.contexto, color = Color.White.copy(alpha = 0.9f), fontSize = 15.sp)
            Spacer(modifier = Modifier.height(22.dp))

            if (coopRoomId != null && partnerSummary.isNotBlank()) {
                AssistChip(enabled = false, onClick = {}, label = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("Compañero (tiempo real)", color = Color(0xFF8FA3BD), fontSize = 11.sp)
                        Text(partnerSummary, fontSize = 12.sp)
                    }
                })
                Spacer(modifier = Modifier.height(14.dp))
            }

            when (tipoReto) {
                RetoTipo.PUZZLE_OR_CODE_BLOCKS -> {
                    Text(
                        text = "Ordena las piezas dentro del marco:",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFFCDF4F2)),
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 130.dp)
                            .background(Color(0xFF0F1A2A), RoundedCornerShape(12.dp))
                            .border(2.dp, dracoCyan, RoundedCornerShape(12.dp))
                            .padding(12.dp),
                    ) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            piezasSolucion.forEach { pieza ->
                                CodigoPill(pieza) {
                                    piezasSolucion = piezasSolucion - pieza
                                    piezasDisponibles = piezasDisponibles + pieza
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(22.dp))

                    FlowRow(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        piezasDisponibles.forEach { pieza ->
                            CodigoPill(pieza, color = Color(0xFF1C2541)) {
                                piezasDisponibles = piezasDisponibles - pieza
                                piezasSolucion = piezasSolucion + pieza
                            }
                        }
                    }
                }

                RetoTipo.QUIZ_TECH -> {
                    Text(
                        "Selección múltiple técnica",
                        color = Color(0xFFCDF4F2),
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        leccionBase.opcionesQuiz.forEachIndexed { index, texto ->
                            val elegido = indiceSeleccionQuiz == index
                            Surface(
                                onClick = { indiceSeleccionQuiz = index },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF0F1A2A),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (elegido) dracoCyan else dracoCyan.copy(alpha = 0.3f),
                                ),
                            ) {
                                Row(
                                    Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Icon(
                                        imageVector =
                                        if (elegido) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                                        tint = dracoCyan,
                                        contentDescription = null,
                                    )
                                    Spacer(Modifier.width(10.dp))
                                    Text(texto, color = Color.White, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }

                RetoTipo.FILL_LINE -> {
                    Text(
                        "Completa el hueco _____",
                        color = Color(0xFFCDF4F2),
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Surface(
                        color = Color(0xFF0F1A2A),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, dracoCyan.copy(alpha = 0.35f)),
                    ) {
                        Text(
                            text = leccionBase.lineaCodigoHueca ?: "",
                            color = Color.White,
                            modifier = Modifier.padding(14.dp),
                            fontSize = 15.sp,
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = textoRelleno,
                        onValueChange = { textoRelleno = it },
                        label = { Text("Tu respuesta") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f).height(50.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, dracoCyan),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = dracoCyan),
                ) { Text("REGRESAR") }

                Button(
                    onClick = {
                        val tieneEntrada =
                            when (tipoReto) {
                                RetoTipo.PUZZLE_OR_CODE_BLOCKS -> piezasSolucion.isNotEmpty()
                                RetoTipo.QUIZ_TECH -> indiceSeleccionQuiz != null
                                RetoTipo.FILL_LINE -> textoRelleno.isNotBlank()
                            }
                        if (!tieneEntrada || isSending) return@Button

                        scope.launch {
                            isSending = true
                            val resultado = evalCorrecto()
                            val entrada = when (tipoReto) {
                                RetoTipo.PUZZLE_OR_CODE_BLOCKS -> piezasSolucion.joinToString(" ")
                                RetoTipo.QUIZ_TECH -> indiceSeleccionQuiz?.let { leccionBase.opcionesQuiz[it] } ?: "--"
                                RetoTipo.FILL_LINE -> textoRelleno
                            }

                            val mensaje = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Default) {
                                ia.generarFeedbackReto(leccionBase, tipoReto, entrada, resultado)
                            }

                            overlayFeedback = mensaje to resultado
                            if (resultado) {
                                progressVm.markLessonSucceeded(leccionBase.id)
                            }
                            isSending = false
                        }
                    },
                    modifier = Modifier.weight(1f).height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = dracoCyan, contentColor = Color.Black),
                    enabled = !isSending,
                ) {
                    Icon(Icons.Default.CheckCircleOutline, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("ENVIAR", fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(92.dp))
        }

        LaunchedEffect(overlayFeedback) {
            if (overlayFeedback != null) {
                scope.launch { sheetState.show() }
            }
        }

        overlayFeedback?.let { (mensajeFinal, resultadoOk) ->
            ModalBottomSheet(
                onDismissRequest = {
                    overlayFeedback = null
                    scope.launch { sheetState.hide() }
                    if (resultadoOk) navController.popBackStack()
                },
                sheetState = sheetState,
                containerColor = Color(0xFF0F1A2A),
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.dragon_dracofocus1),
                        contentDescription = "Draco",
                        modifier = Modifier.size(164.dp),
                    )
                    Text(
                        if (resultadoOk) "¡Celebración Draco!" else "Draco te da una pista",
                        color = dracoCyan,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(mensajeFinal, color = Color.White, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(18.dp))
                    Button(onClick = {
                        overlayFeedback = null
                        scope.launch { sheetState.hide() }
                        if (resultadoOk) navController.popBackStack()
                    }) {
                        Text("CONTINUAR")
                    }
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }

        if (isSending && overlayFeedback == null) {
            Box(Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = dracoCyan)
            }
        }
    }
}

private fun normalize(s: String) = s.trim().lowercase(Locale.getDefault())

private fun describeTipoBadge(t: RetoTipo): String =
    when (t) {
        RetoTipo.PUZZLE_OR_CODE_BLOCKS -> "Reto: puzzle de código"
        RetoTipo.QUIZ_TECH -> "Reto: quiz técnico"
        RetoTipo.FILL_LINE -> "Reto: rellenar línea"
    }
