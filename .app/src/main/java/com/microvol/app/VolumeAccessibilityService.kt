package com.microvol.app

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.media.AudioManager
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent

class VolumeAccessibilityService : AccessibilityService() {

private lateinit var audioManager: AudioManager
private var currentFineStep = 0.5f // Paso ultra fino (0.5%)

override fun onServiceConnected() {
    super.onServiceConnected()
    audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
}

override fun onKeyEvent(event: KeyEvent?): Boolean {
    if (event == null) return false

    val action = event.action
    val keyCode = event.keyCode

    // Interceptamos solo cuando el botón es presionado hacia abajo
    if (action == KeyEvent.ACTION_DOWN) {
        when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP -> {
                adjustFineVolume(increment = true)
                return true // Bloquea el salto brusco del sistema
            }
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                adjustFineVolume(increment = false)
                return true // Bloquea el salto brusco del sistema
            }
        }
    }
    return super.onKeyEvent(event)
}

private fun adjustFineVolume(increment: Boolean) {
    val maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
    val currentVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
    
    val delta = if (increment) 1 else -1
    val nextVol = (currentVol + delta).coerceIn(0, maxVol)
    
    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, nextVol, 0)
}

override fun onAccessibilityEvent(event: AccessibilityEvent?) {}

override fun onInterrupt() {}

}
