package co.edu.unab.dracofocusapp.cooperative

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

/**
 * Sincronización mínima de salas cooperativas (Realtime Database).
 */
object LessonCooperativeSync {

    fun publishLessonSummary(roomId: String, lessonId: String, summary: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseDatabase.getInstance()
            .reference
            .child("lesson_coop_rooms/$roomId/lessons/$lessonId/summaries/$uid")
            .setValue(summary)
    }

    fun publishAggregateProgressPct(roomId: String, pct: Float) {
        FirebaseDatabase.getInstance()
            .reference
            .child("lesson_coop_rooms/$roomId/shared_progress/fundamentos_pct")
            .setValue(pct.coerceIn(0f, 1f))
    }

    fun attachSummariesListener(
        roomId: String,
        lessonId: String,
        block: (String) -> Unit,
    ): Pair<ValueEventListener, com.google.firebase.database.DatabaseReference> {
        val ref = FirebaseDatabase.getInstance()
            .reference
            .child("lesson_coop_rooms/$roomId/lessons/$lessonId/summaries")

        val listener = object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                val aggregated = snapshot.children.mapNotNull { it.getValue(String::class.java) }
                    .joinToString(" | ")
                block(aggregated)
            }

            override fun onCancelled(error: com.google.firebase.database.DatabaseError) = Unit
        }
        ref.addValueEventListener(listener)
        return Pair(listener, ref)
    }
}
