package co.edu.unab.dracofocusapp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import co.edu.unab.dracofocusapp.data.local.datastore.PomodoroDataStore
import co.edu.unab.dracofocusapp.data.remote.ApiService
import co.edu.unab.dracofocusapp.domain.util.Clock

class DracomodoroViewModelFactory(
    private val dataStore: PomodoroDataStore,
    private val clock: Clock,
    private val appContext: Context,
    private val apiService: ApiService
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DracomodoroViewModel::class.java)) {
            return DracomodoroViewModel(dataStore, clock, appContext, apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
