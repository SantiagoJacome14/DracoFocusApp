package co.edu.unab.dracofocusapp.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth

fun guardarRespuestaLeccion(codigo: String, leccionId: String) {
    val db = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "desconocido"

    val respuesta = hashMapOf(
        "codigo" to codigo,
        "leccionId" to leccionId,
        "usuarioId" to userId,
        "fecha" to System.currentTimeMillis()
    )

    db.collection("respuestas_lecciones")
        .add(respuesta)
        .addOnSuccessListener {
            println("✅ Respuesta guardada correctamente")
        }
        .addOnFailureListener { e ->
            println("❌ Error al guardar: ${e.message}")
        }
}
