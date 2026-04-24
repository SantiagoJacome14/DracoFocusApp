package co.edu.unab.dracofocusapp.data.repository

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : UserRepository {

    override fun getUserData(): Flow<UserData?> = flow {
        val user = firebaseAuth.currentUser
        if (user != null) {
            // Por ahora extraemos del perfil de Auth, luego se puede conectar a Firestore
            emit(UserData(
                name = user.displayName ?: "Usuario",
                email = user.email ?: "",
                level = 9, // Simulados por ahora
                xp = 1670,
                racha = 9
            ))
        } else {
            emit(null)
        }
    }

    override suspend fun updateUserData(userData: UserData): Result<Unit> {
        // Implementación futura con Firestore
        return Result.success(Unit)
    }
}
