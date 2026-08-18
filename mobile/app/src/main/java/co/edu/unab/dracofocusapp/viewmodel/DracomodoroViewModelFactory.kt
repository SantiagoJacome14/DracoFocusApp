package co.edu.unab.dracofocusapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import co.edu.unab.dracofocusapp.data.local.datastore.PomodoroDataStore
import co.edu.unab.dracofocusapp.domain.util.Clock

class DracomodoroViewModelFactory(
    private val dataStore: PomodoroDataStore,
    private val clock: Clock
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DracomodoroViewModel::class.java)) {
            return DracomodoroViewModel(dataStore, clock) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
