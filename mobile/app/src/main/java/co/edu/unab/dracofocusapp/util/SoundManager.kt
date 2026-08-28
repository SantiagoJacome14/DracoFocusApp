package co.edu.unab.dracofocusapp.util

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import co.edu.unab.dracofocusapp.data.local.datastore.SettingsDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

enum class SoundEffect { CLICK, SUCCESS, ERROR, LESSON_COMPLETED, TIMER_START }

/**
 * Centraliza los efectos de sonido de la app. No usa archivos de audio (el proyecto
 * no tenía ninguno): genera tonos cortos con ToneGenerator, ligero y sin assets.
 * Respeta la preferencia "Efectos de sonido" de Perfil (SettingsDataStore).
 *
 * Si más adelante se agregan archivos de sonido reales (mp3/ogg en res/raw), esta
 * es la única clase que habría que tocar para reproducirlos con SoundPool en vez
 * de tonos generados.
 */
object SoundManager {
    private const val VOLUME_PERCENT = 60
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    @Volatile private var toneGenerator: ToneGenerator? = null

    private fun getOrCreateToneGenerator(): ToneGenerator {
        return toneGenerator ?: synchronized(this) {
            toneGenerator ?: ToneGenerator(AudioManager.STREAM_NOTIFICATION, VOLUME_PERCENT).also {
                toneGenerator = it
            }
        }
    }

    fun play(context: Context, effect: SoundEffect) {
        scope.launch {
            val enabled = SettingsDataStore(context).soundEffectsEnabled.first()
            if (!enabled) return@launch
            try {
                val tg = getOrCreateToneGenerator()
                when (effect) {
                    SoundEffect.CLICK -> tg.startTone(ToneGenerator.TONE_PROP_BEEP, 40)
                    SoundEffect.SUCCESS -> tg.startTone(ToneGenerator.TONE_PROP_ACK, 150)
                    SoundEffect.ERROR -> tg.startTone(ToneGenerator.TONE_PROP_NACK, 200)
                    SoundEffect.LESSON_COMPLETED -> tg.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 300)
                    SoundEffect.TIMER_START -> tg.startTone(ToneGenerator.TONE_PROP_PROMPT, 120)
                }
            } catch (e: Exception) {
                // Dispositivo sin salida de audio disponible u otro fallo no crítico; ignorar.
            }
        }
    }

    /** Libera el ToneGenerator. Llamar solo si se sabe que no se necesitará más (p. ej. Application.onTerminate). */
    fun release() {
        synchronized(this) {
            toneGenerator?.release()
            toneGenerator = null
        }
    }
}
