package co.edu.unab.dracofocusapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf

class FeedbackViewModel : ViewModel() {
    var retroalimentacion = mutableStateOf("")
}
