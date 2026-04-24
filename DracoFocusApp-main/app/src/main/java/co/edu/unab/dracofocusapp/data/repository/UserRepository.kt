package co.edu.unab.dracofocusapp.data.repository

import kotlinx.coroutines.flow.Flow

data class UserData(
    val name: String = "",
    val email: String = "",
    val level: Int = 1,
    val xp: Int = 0,
    val racha: Int = 0
)

interface UserRepository {
    fun getUserData(): Flow<UserData?>
    suspend fun updateUserData(userData: UserData): Result<Unit>
}
