package com.microvol.app

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.audiofx.Equalizer
import android.media.audiofx.LoudnessEnhancer
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent

class VolumeAccessibilityService : AccessibilityService() {

    private lateinit var audioManager: AudioManager
    private var equalizer: Equalizer? = null
    private var enhancer: LoudnessEnhancer? = null

    // Nivel fino de 0 a 100 (pasos de 1% o fraccionales)
    private var fineLevel = 50f
    private val stepSize = 1.0f

    override fun onServiceConnected() {
        super.onServiceConnected()
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        initAudioEngine()
    }

    private fun initAudioEngine() {
        try {
            // Inicializar Equalizer en AudioSessionId 0 (sesión de mezcla global del sistema)
            equalizer = Equalizer(1000, 0).apply {
                enabled = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            // Inicializar LoudnessEnhancer para control fino de ganancia en mB
            enhancer = LoudnessEnhancer(0).apply {
                enabled = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onKeyEvent(event: KeyEvent?): Boolean {
        if (event == null) return false

        if (event.action == KeyEvent.ACTION_DOWN) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_VOLUME_UP -> {
                    adjustFineGain(increment = true)
                    return true // Intercepta y cancela el paso brusco de fábrica de Android
                }
                KeyEvent.KEYCODE_VOLUME_DOWN -> {
                    adjustFineGain(increment = false)
                    return true // Intercepta y cancela el paso brusco de fábrica de Android
                }
            }
        }
        return super.onKeyEvent(event)
    }

    private fun adjustFineGain(increment: Boolean) {
        if (increment) {
            fineLevel = (fineLevel + stepSize).coerceAtMost(100f)
        } else {
            fineLevel = (fineLevel - stepSize).coerceAtLeast(0f)
        }

        applyFineAudioGain(fineLevel)
        notifyOverlay(fineLevel)
    }

    private fun applyFineAudioGain(level: Float) {
        // En Android 9, AudioSession 0 permite atenuación por Equalizer Band Levels (-1500mB a +1500mB)
        // Mapeo lineal/logarítmico fino de 0..100% hacia decibelios reales
        try {
            equalizer?.let { eq ->
                val minLevel = eq.bandLevelRange[0] // típicamente -1500 (-15 dB)
                val maxLevel = eq.bandLevelRange[1] // típicamente +1500 (+15 dB)
                val targetGain = (minLevel + (level / 100f) * (maxLevel - minLevel)).toInt().toShort()

                for (band in 0 until eq.numberOfBands) {
                    eq.setBandLevel(band.toShort(), targetGain)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Refuerzo fino mediante LoudnessEnhancer si la plataforma lo soporta
        try {
            enhancer?.let { enh ->
                val targetmB = ((level - 50f) * 40).toInt().coerceIn(-2000, 2000)
                enh.setTargetGain(targetmB)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun notifyOverlay(level: Float) {
        // Enviar evento broadcast al HUD flotante para actualizar el valor visual
        val intent = Intent("com.microvol.app.UPDATE_VOLUME_HUD").apply {
            putExtra("fine_volume_level", level)
        }
        sendBroadcast(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            equalizer?.release()
            enhancer?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}

    override fun onInterrupt() {}
}
