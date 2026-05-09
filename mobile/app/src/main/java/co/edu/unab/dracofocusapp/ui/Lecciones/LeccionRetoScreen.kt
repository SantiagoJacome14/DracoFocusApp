package co.edu.unab.dracofocusapp.ui.Lecciones

import android.util.Log
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
    reviewMode: Boolean = false,
) {
    val app = LocalContext.current.applicationContext as DracoFocusApplication
    val currentUserId by app.tokenManager.userId.collectAsState(initial = null)

    if (currentUserId == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFF22DDF2))
        }
        return
    }

    val progressVm = viewModel<LessonProgressViewModel>(
        factory = LessonProgressViewModel.factory(
            userId = currentUserId!!,
            repository = app.lessonProgressRepository,
            rewardManager = app.rewardManager,
        ),
    )

    val exerciseVm = viewModel<co.edu.unab.dracofocusapp.viewmodel.LessonExerciseViewModel>(
        factory = co.edu.unab.dracofocusapp.viewmodel.LessonExerciseViewModel.factory(app.lessonRepository, currentUserId!!)
    )

    val uiState by exerciseVm.uiState.collectAsState()

    LaunchedEffect(lessonId) {
        exerciseVm.loadExercises(lessonId)
    }

    when (val state = uiState) {
        is co.edu.unab.dracofocusapp.viewmodel.LessonExerciseState.Loading -> {
            Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0B132B)), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF22DDF2))
            }
        }
        is co.edu.unab.dracofocusapp.viewmodel.LessonExerciseState.Error -> {
            Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0B132B)), contentAlignment = Alignment.Center) {
                Text(text = state.message, color = Color.White)
            }
        }
        is co.edu.unab.dracofocusapp.viewmodel.LessonExerciseState.Success -> {
            ExerciseSessionContent(
                navController = navController,
                lesson = state.lesson,
                exercises = state.exercises,
                progressVm = progressVm,
                coopRoomId = coopRoomId,
                onBack = onBack,
                reviewMode = reviewMode,
                savedExerciseIndex = if (reviewMode) 0 else state.savedIndex,
                onSaveProgress = { index -> if (!reviewMode) exerciseVm.saveCurrentExercise(state.lesson.slug, index) },
                onClearProgress = { exerciseVm.clearCurrentExercise(state.lesson.slug) },
            )
        }
        else -> Unit
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ExerciseSessionContent(
    navController: NavController,
    lesson: co.edu.unab.dracofocusapp.data.remote.LessonDto,
    exercises: List<co.edu.unab.dracofocusapp.data.remote.ExerciseDto>,
    progressVm: LessonProgressViewModel,
    coopRoomId: String?,
    onBack: () -> Unit,
    savedExerciseIndex: Int = 0,
    onSaveProgress: (Int) -> Unit = {},
    onClearProgress: () -> Unit = {},
    reviewMode: Boolean = false,
) {
    var currentIndex by remember { mutableIntStateOf(savedExerciseIndex.coerceIn(0, (exercises.size - 1).coerceAtLeast(0))) }
    val currentExercise = exercises.getOrNull(currentIndex) ?: return

    LaunchedEffect(currentIndex) {
        Log.d("EXERCISE_RENDER", "type=${currentExercise.type} data=${currentExercise.data}")
    }

    // Exercise state
    var piezasSolucion by remember(currentIndex) { mutableStateOf(listOf<String>()) }
    var piezasDisponibles by remember(currentIndex) {
        val rawPieces = (currentExercise.data?.get("pieces") as? List<*>)?.filterIsInstance<String>() ?: emptyList()
        mutableStateOf(rawPieces.shuffled())
    }
    var indiceSeleccionQuiz by remember(currentIndex) { mutableStateOf<Int?>(null) }
    var textoRelleno by remember(currentIndex) { mutableStateOf("") }
    
    val scope = rememberCoroutineScope()
    val ia = remember { IAFeedbackManager() }
    val dracoCyan = Color(0xFF22DDF2)
    val fondo = Brush.verticalGradient(listOf(Color(0xFF0B132B), Color(0xFF1C2541)))

    var isSending by remember { mutableStateOf(false) }
    var overlayFeedback by remember { mutableStateOf<Pair<String, Boolean>?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val tipoReto = when (currentExercise.type) {
        "multiple_choice" -> RetoTipo.QUIZ_TECH
        "fill_blank" -> RetoTipo.FILL_LINE
        "code_puzzle" -> RetoTipo.PUZZLE_OR_CODE_BLOCKS
        else -> RetoTipo.PUZZLE_OR_CODE_BLOCKS
    }

    fun evalCorrecto(): Boolean {
        val ex = currentExercise
        val exData = ex.data ?: emptyMap()

        Log.d("ANSWER_DEBUG", "=== evalCorrecto ===")
        Log.d("ANSWER_DEBUG", "type=${ex.type}  tipoReto=$tipoReto")
        Log.d("ANSWER_DEBUG", "data_keys=${exData.keys}")
        Log.d("ANSWER_DEBUG", "data_answer=${exData["answer"]}")
        Log.d("ANSWER_DEBUG", "data_correct_answer=${exData["correct_answer"]}")
        Log.d("ANSWER_DEBUG", "data_correct_index=${exData["correct_index"]}")
        Log.d("ANSWER_DEBUG", "data_options=${exData["options"]}")
        Log.d("ANSWER_DEBUG", "data_solution=${exData["solution"]}")
        Log.d("ANSWER_DEBUG", "data_pieces=${exData["pieces"]}")

        val userInput: String
        val expectedAnswer: String
        val isCorrect: Boolean

        when (tipoReto) {
            RetoTipo.QUIZ_TECH -> {
                // Primary: compare selected index against data.correct_index
                val correctIdx = ex.dataInt("correct_index")
                // Fallback: compare selected option text against data.correct_answer
                val correctAnswerText = ex.dataString("correct_answer")
                val options = ex.dataStringList("options") ?: emptyList()

                userInput = indiceSeleccionQuiz?.let { options.getOrElse(it) { it.toString() } } ?: "none"

                isCorrect = when {
                    indiceSeleccionQuiz == null -> false
                    correctIdx != null -> indiceSeleccionQuiz == correctIdx
                    correctAnswerText != null -> options.getOrNull(indiceSeleccionQuiz!!) == correctAnswerText
                    else -> false
                }
                expectedAnswer = when {
                    correctIdx != null -> options.getOrElse(correctIdx) { correctIdx.toString() }
                    correctAnswerText != null -> correctAnswerText
                    else -> ""
                }
                Log.d("ANSWER_DEBUG", "QUIZ: selectedIdx=$indiceSeleccionQuiz correctIdx=$correctIdx correctText=$correctAnswerText selectedText=$userInput")
            }

            RetoTipo.FILL_LINE -> {
                userInput = textoRelleno
                // Strictly read from data.answer; data.correct_answer as explicit fallback only
                expectedAnswer = ex.dataString("answer")
                    ?: ex.dataString("correct_answer")
                    ?: ""
                Log.d("ANSWER_DEBUG", "FILL: userInput='$userInput'  expected='$expectedAnswer'")
                val normalMatch = normalize(userInput) == normalize(expectedAnswer)
                // Strip all whitespace for code-style comparison (handles indentation/spacing diffs)
                val codeMatch = userInput.replace("\\s+".toRegex(), "").lowercase(Locale.ROOT) ==
                    expectedAnswer.replace("\\s+".toRegex(), "").lowercase(Locale.ROOT)
                isCorrect = normalMatch || codeMatch
                Log.d("ANSWER_DEBUG", "FILL: normalMatch=$normalMatch  codeMatch=$codeMatch  isCorrect=$isCorrect")
            }

            RetoTipo.PUZZLE_OR_CODE_BLOCKS -> {
                val solution = ex.dataStringList("solution") ?: emptyList()
                userInput = piezasSolucion.joinToString(" ")
                expectedAnswer = solution.joinToString(" ")
                isCorrect = solution.isNotEmpty() && piezasSolucion == solution
                Log.d("ANSWER_DEBUG", "PUZZLE: selected=$piezasSolucion  solution=$solution  isCorrect=$isCorrect")
            }
        }

        Log.d("ANSWER_DEBUG", "RESULT: isCorrect=$isCorrect")
        return isCorrect
    }

    Box(Modifier.fillMaxSize().background(fondo)) {
        Column(
            Modifier.padding(horizontal = 20.dp, vertical = 12.dp).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val sessionPrefix = if (reviewMode) "REPASO: " else ""
            Text("$sessionPrefix${lesson.title.uppercase()} (${currentIndex + 1}/${exercises.size})", color = dracoCyan, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            AssistChip(onClick = {}, enabled = false, label = { Text(describeTipoBadge(tipoReto)) })
            Text(currentExercise.question, color = Color.White.copy(alpha = 0.9f), fontSize = 16.sp, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(22.dp))

            when (tipoReto) {
                RetoTipo.QUIZ_TECH -> {
                    val options = (currentExercise.data?.get("options") as? List<*>)?.filterIsInstance<String>() ?: emptyList()
                    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        options.forEachIndexed { index, texto ->
                            val elegido = indiceSeleccionQuiz == index
                            Surface(
                                onClick = { indiceSeleccionQuiz = index },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF0F1A2A),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (elegido) dracoCyan else dracoCyan.copy(alpha = 0.3f))
                            ) {
                                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (elegido) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                                        tint = dracoCyan,
                                        contentDescription = null
                                    )
                                    Spacer(Modifier.width(10.dp))
                                    Text(texto, color = Color.White, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }
                RetoTipo.FILL_LINE -> {
                    val codeBefore = (currentExercise.data?.get("code_before") as? String) ?: ""
                    val codeAfter = (currentExercise.data?.get("code_after") as? String) ?: ""
                    
                    Surface(color = Color(0xFF0F1A2A), shape = RoundedCornerShape(12.dp), border = androidx.compose.foundation.BorderStroke(1.dp, dracoCyan.copy(alpha = 0.35f))) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            if (codeBefore.isNotBlank()) Text(codeBefore, color = Color.Gray, fontSize = 14.sp)
                            Text(text = "_____", color = dracoCyan, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            if (codeAfter.isNotBlank()) Text(codeAfter, color = Color.Gray, fontSize = 14.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = textoRelleno,
                        onValueChange = { textoRelleno = it },
                        label = { Text("Tu respuesta") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                RetoTipo.PUZZLE_OR_CODE_BLOCKS -> {
                    if (piezasSolucion.isNotEmpty()) {
                        Text("Tu solución:", color = dracoCyan, fontSize = 12.sp, modifier = Modifier.align(Alignment.Start))
                        Spacer(Modifier.height(6.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            piezasSolucion.forEach { piece ->
                                Surface(
                                    onClick = {
                                        piezasSolucion = piezasSolucion - piece
                                        piezasDisponibles = piezasDisponibles + piece
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    color = dracoCyan.copy(alpha = 0.15f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, dracoCyan)
                                ) {
                                    Text(piece, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), color = dracoCyan, fontSize = 13.sp)
                                }
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                    }

                    Text("Piezas disponibles:", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp, modifier = Modifier.align(Alignment.Start))
                    Spacer(Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        piezasDisponibles.forEach { piece ->
                            Surface(
                                onClick = {
                                    piezasDisponibles = piezasDisponibles - piece
                                    piezasSolucion = piezasSolucion + piece
                                },
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF0F1A2A),
                                border = androidx.compose.foundation.BorderStroke(1.dp, dracoCyan.copy(alpha = 0.4f))
                            ) {
                                Text(piece, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), color = Color.White, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f).height(50.dp)) { Text("SALIR") }
                Button(
                    onClick = {
                        scope.launch {
                            isSending = true
                            val result = evalCorrecto()
                            
                            val leccionLegacy = Leccion(
                                id = lesson.id.toString(),
                                titulo = lesson.title,
                                contexto = currentExercise.question,
                                piezasDisponibles = emptyList(),
                                solucionCorrecta = currentExercise.dataStringList("solution")
                                    ?: listOf(currentExercise.dataString("answer") ?: currentExercise.dataString("correct_answer") ?: ""),
                                opcionesQuiz = currentExercise.dataStringList("options") ?: emptyList(),
                                respuestaRelleno = currentExercise.dataString("answer") ?: currentExercise.dataString("correct_answer")
                            )
                            
                            val entrada = when(tipoReto) {
                                RetoTipo.QUIZ_TECH -> {
                                    val options = (currentExercise.data?.get("options") as? List<*>)?.filterIsInstance<String>() ?: emptyList()
                                    indiceSeleccionQuiz?.let { options.getOrNull(it) } ?: ""
                                }
                                RetoTipo.FILL_LINE -> textoRelleno
                                else -> ""
                            }

                            val feedback = ia.generarFeedbackReto(leccionLegacy, tipoReto, entrada, result)
                            overlayFeedback = feedback to result
                            isSending = false
                        }
                    },
                    modifier = Modifier.weight(1f).height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = dracoCyan, contentColor = Color.Black),
                    enabled = !isSending
                ) {
                    Text("ENVIAR", fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(80.dp))
        }

        // FEEDBACK SHEET
        overlayFeedback?.let { (msg, isOk) ->
            ModalBottomSheet(
                onDismissRequest = {
                    overlayFeedback = null
                    if (isOk) {
                        if (currentIndex < exercises.size - 1) {
                            currentIndex++
                            onSaveProgress(currentIndex)
                        } else {
                            onClearProgress()
                            if (!reviewMode) progressVm.markLessonSucceeded(lesson.slug)
                            navController.popBackStack()
                        }
                    }
                },
                sheetState = sheetState,
                containerColor = Color(0xFF0F1A2A)
            ) {
                Column(Modifier.padding(20.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(painter = painterResource(id = R.drawable.dragon_dracofocus1), contentDescription = null, modifier = Modifier.size(120.dp))
                    Text(if (isOk) "¡Excelente!" else "Draco dice...", color = dracoCyan, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(msg, color = Color.White, textAlign = TextAlign.Center, modifier = Modifier.padding(vertical = 12.dp))
                    Button(onClick = {
                        scope.launch {
                            sheetState.hide()
                            overlayFeedback = null
                            if (isOk) {
                                if (currentIndex < exercises.size - 1) {
                                    currentIndex++
                                    onSaveProgress(currentIndex)
                                } else {
                                    onClearProgress()
                                    if (!reviewMode) progressVm.markLessonSucceeded(lesson.slug)
                                    navController.popBackStack()
                                }
                            }
                        }
                    }) { Text(if (reviewMode && currentIndex >= exercises.size - 1) "FINALIZAR REPASO" else "CONTINUAR") }
                    Spacer(Modifier.height(40.dp))
                }
            }
        }
    }
}

private fun normalize(s: String) =
    s.trim().lowercase(Locale.ROOT).replace("\\s+".toRegex(), " ")

private fun describeTipoBadge(t: RetoTipo): String =
    when (t) {
        RetoTipo.PUZZLE_OR_CODE_BLOCKS -> "Reto: puzzle de código"
        RetoTipo.QUIZ_TECH -> "Reto: quiz técnico"
        RetoTipo.FILL_LINE -> "Reto: rellenar línea"
    }
