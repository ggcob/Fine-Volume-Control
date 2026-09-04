package com.microvol.app

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
        MicroVolTheme {
            MainScreen(
                onOpenAccessibility = {
                    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                    startActivity(intent)
                },
                onOpenOverlayPermission = {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName")
                    )
                    startActivity(intent)
                }
            )
        }
    }
}

}

@Composable
fun MicroVolTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            background = Color(0xFF0C141F),
            surface = Color(0xFF151C28),
            primary = Color(0xFF00E5FF),
            onPrimary = Color(0xFF00373D),
            onBackground = Color(0xFFE2E8F0)
        ),
        content = content
    )
}

@Composable
fun MainScreen(
    onOpenAccessibility: () -> Unit,
    onOpenOverlayPermission: () -> Unit
) {
    var fineVolume by remember { mutableFloatStateOf(42.5f) }
    var stepSize by remember { mutableFloatStateOf(0.5f) }

Surface(
    modifier = Modifier.fillMaxSize(),
    color = MaterialTheme.colorScheme.background
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Cabecera
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "MICROVOL",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 2.sp
            )
            Text(
                text = "v1.0 (No-Root)",
                fontSize = 12.sp,
                color = Color(0xFF64748B)
            )
        }

        // Monitor de Volumen Actual
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "NIVEL DE PRECISIÓN", fontSize = 12.sp, color = Color(0xFF94A3B8))
                Text(
                    text = "${String.format("%.1f", fineVolume)}%",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )
                Slider(
                    value = fineVolume,
                    onValueChange = { fineVolume = it },
                    valueRange = 0f..100f,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "0%", fontSize = 11.sp, color = Color(0xFF64748B))
                    Text(text = "Paso: ${stepSize}%", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                    Text(text = "100%", fontSize = 11.sp, color = Color(0xFF64748B))
                }
            }
        }

        // Permisos requeridos
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "PERMISOS DE SISTEMA",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Button(
                    onClick = onOpenAccessibility,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B))
                ) {
                    Text(text = "1. Activar Servicio Accesibilidad", color = Color(0xFF00E5FF))
                }

                Button(
                    onClick = onOpenOverlayPermission,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B))
                ) {
                    Text(text = "2. Permitir Ventana Flotante (HUD)", color = Color(0xFF00E5FF))
                }
            }
        }
    }
}

}
