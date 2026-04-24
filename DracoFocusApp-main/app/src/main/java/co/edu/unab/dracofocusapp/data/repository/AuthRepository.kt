package co.edu.unab.dracofocusapp.data.repository

import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: FirebaseUser?
    fun login(email: String, password: String): Flow<Result<FirebaseUser>>
    fun register(email: String, password: String, name: String): Flow<Result<FirebaseUser>>
    fun logout()
    fun forgotPassword(email: String): Flow<Result<Unit>>
}
