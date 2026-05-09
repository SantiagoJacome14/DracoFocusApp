package co.edu.unab.dracofocusapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import co.edu.unab.dracofocusapp.data.remote.ExerciseDto
import co.edu.unab.dracofocusapp.data.remote.LessonDto
import co.edu.unab.dracofocusapp.data.repo.LessonRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class LessonExerciseState {
    object Idle : LessonExerciseState()
    object Loading : LessonExerciseState()
    data class Success(
        val lesson: LessonDto,
        val exercises: List<ExerciseDto>,
        val savedIndex: Int = 0,
    ) : LessonExerciseState()
    data class Error(val message: String) : LessonExerciseState()
}

class LessonExerciseViewModel(
    private val lessonRepository: LessonRepository,
    private val userId: String,
) : ViewModel() {

    private val _uiState = MutableStateFlow<LessonExerciseState>(LessonExerciseState.Idle)
    val uiState: StateFlow<LessonExerciseState> = _uiState

    fun loadExercises(lessonId: String) {
        viewModelScope.launch {
            _uiState.value = LessonExerciseState.Loading
            Log.d("LESSON_DEBUG", "loadExercises: input=$lessonId")

            try {
                // Determine if lessonId is numeric ID or Slug
                val slug = if (lessonId.toIntOrNull() != null) {
                    val resolved = lessonRepository.getSlugById(lessonId.toInt())
                    Log.d("LESSON_DEBUG", "Resolving numeric ID $lessonId to slug: $resolved")
                    resolved ?: lessonId
                } else {
                    lessonId
                }

                Log.d("LESSON_DEBUG", "Final slug for API request: $slug")

                val response = lessonRepository.fetchExercisesForLesson(slug)
                if (response != null) {
                    val savedIndex = lessonRepository.loadExerciseProgress(userId, slug)
                    Log.d("LESSON_DEBUG", "Exercises loaded for slug=$slug count=${response.exercises.size} savedIndex=$savedIndex")
                    _uiState.value = LessonExerciseState.Success(response.lesson, response.exercises, savedIndex)
                } else {
                    Log.e("LESSON_DEBUG", "Failed to load exercises for slug=$slug (null response)")
                    _uiState.value = LessonExerciseState.Error("No se pudieron cargar los ejercicios")
                }
            } catch (e: Exception) {
                Log.e("LESSON_DEBUG", "Exception in loadExercises for lessonId=$lessonId", e)
                _uiState.value = LessonExerciseState.Error("Error: ${e.message}")
            }
        }
    }

    fun saveCurrentExercise(lessonSlug: String, index: Int) {
        viewModelScope.launch {
            lessonRepository.saveExerciseProgress(userId, lessonSlug, index)
        }
    }

    fun clearCurrentExercise(lessonSlug: String) {
        viewModelScope.launch {
            lessonRepository.clearExerciseProgress(userId, lessonSlug)
        }
    }

    companion object {
        fun factory(repository: LessonRepository, userId: String): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return LessonExerciseViewModel(repository, userId) as T
                }
            }
    }
}
