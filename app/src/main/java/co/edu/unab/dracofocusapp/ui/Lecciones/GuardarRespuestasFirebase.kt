package co.edu.unab.dracofocusapp.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth

fun guardarRespuestaLeccion(codigo: String, leccionId: String) {
    val db = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "desconocido"

    val respuesta = hashMapOf(
        "user_id" to userId,
        "leccion_id" to leccionId,
        "codigo" to codigo,
        "estado" to "pendiente",
        "timestamp" to System.currentTimeMillis()
    )

    // Guardar en una colección dedicada a las respuestas pendientes
    db.collection("respuestas_pendientes")
        .add(respuesta)
        .addOnSuccessListener {
            println("✅ Respuesta enviada a DracoFocusAI")
        }
        .addOnFailureListener { e ->
            println("❌ Error al guardar: ${e.message}")
        }
}

