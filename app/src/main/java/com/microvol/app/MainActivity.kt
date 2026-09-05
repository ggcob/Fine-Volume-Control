package com.microvol.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {

    private var currentFineVolume by mutableFloatStateOf(42.5f)

    private val volumeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "com.microvol.app.UPDATE_VOLUME_HUD") {
                val level = intent.getFloatExtra("fine_volume_level", 42.5f)
                currentFineVolume = level
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val filter = IntentFilter("com.microvol.app.UPDATE_VOLUME_HUD")
        registerReceiver(volumeReceiver, filter)

        setContent {
            MicroVolTheme {
                MicroVolFullApp(
                    currentVolume = currentFineVolume,
                    onVolumeChange = { currentFineVolume = it },
                    onOpenAccessibility = {
                        startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                    },
                    onOpenOverlay = {
                        startActivity(
                            Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:$packageName")
                            )
                        )
                    }
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(volumeReceiver)
    }
}

@Composable
fun MicroVolTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            background = Color(0xFF0C141F),
            surface = Color(0xFF151C28),
            surfaceVariant = Color(0xFF1E293B),
            primary = Color(0xFF00E5FF),
            onPrimary = Color(0xFF00373D),
            secondary = Color(0xFF00B0FF),
            onBackground = Color(0xFFF1F5F9),
            outline = Color(0xFF334155)
        ),
        content = content
    )
}

@Composable
fun MicroVolFullApp(
    currentVolume: Float,
    onVolumeChange: (Float) -> Unit,
    onOpenAccessibility: () -> Unit,
    onOpenOverlay: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF070E1A),
                contentColor = Color(0xFF94A3B8)
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Tune, contentDescription = "Control") },
                    label = { Text("Volumen", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF00E5FF),
                        selectedTextColor = Color(0xFF00E5FF),
                        indicatorColor = Color(0xFF151C28)
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.GraphicEq, contentDescription = "EQ") },
                    label = { Text("Ecualizador", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF00E5FF),
                        selectedTextColor = Color(0xFF00E5FF),
                        indicatorColor = Color(0xFF151C28)
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Ajustes") },
                    label = { Text("Ajustes", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF00E5FF),
                        selectedTextColor = Color(0xFF00E5FF),
                        indicatorColor = Color(0xFF151C28)
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0C141F))
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> VolumeControlTab(currentVolume, onVolumeChange)
                1 -> EqualizerTab()
                2 -> SettingsTab(onOpenAccessibility, onOpenOverlay)
            }
        }
    }
}

@Composable
fun VolumeControlTab(
    currentVolume: Float,
    onVolumeChange: (Float) -> Unit
) {
    var stepMode by remember { mutableFloatStateOf(0.5f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "MICROVOL",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF00E5FF),
                    letterSpacing = 2.sp
                )
                Text(
                    text = "Control Fino DSP (Android 9)",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF10B981).copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "DSP ACTIVO",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF10B981)
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF151C28)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "VOLUMEN DE PRECISIÓN",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "${String.format("%.1f", currentVolume)}%",
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF00E5FF),
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "${String.format("%.2f", (currentVolume / 100f * 20f))}/20 dB Virtuales",
                    fontSize = 13.sp,
                    color = Color(0xFF64748B)
                )
                Spacer(modifier = Modifier.height(16.dp))

                Slider(
                    value = currentVolume,
                    onValueChange = onVolumeChange,
                    valueRange = 0f..100f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF00E5FF),
                        activeTrackColor = Color(0xFF00E5FF),
                        inactiveTrackColor = Color(0xFF1E293B)
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "0% Silencio", fontSize = 11.sp, color = Color(0xFF64748B))
                    Text(text = "Paso actual: ${stepMode}%", fontSize = 11.sp, color = Color(0xFF00E5FF))
                    Text(text = "100% Máx", fontSize = 11.sp, color = Color(0xFF64748B))
                }
            }
        }

        Text(
            text = "TAMAÑO DE PASO EN BOTONES FÍSICOS",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF94A3B8),
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf(0.5f, 1.0f, 2.0f, 5.0f).forEach { step ->
                val isSelected = stepMode == step
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) Color(0xFF00E5FF) else Color(0xFF151C28))
                        .border(1.dp, if (isSelected) Color(0xFF00E5FF) else Color(0xFF334155), RoundedCornerShape(12.dp))
                        .clickable { stepMode = step }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${step}%",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color(0xFF00373D) else Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun EqualizerTab() {
    var bassBoost by remember { mutableFloatStateOf(6f) }
    var midGain by remember { mutableFloatStateOf(0f) }
    var trebleGain by remember { mutableFloatStateOf(4f) }
    var eqEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "ECUALIZADOR PARAMÉTRICO",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Text(text = "Atenuación y Curva en dB", fontSize = 12.sp, color = Color(0xFF64748B))
            }
            Switch(
                checked = eqEnabled,
                onCheckedChange = { eqEnabled = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFF00E5FF),
                    checkedTrackColor = Color(0xFF00373D)
                )
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF151C28)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                EqBandSlider("Graves (60Hz)", bassBoost) { bassBoost = it }
                EqBandSlider("Medios (1kHz)", midGain) { midGain = it }
                EqBandSlider("Agudos (14kHz)", trebleGain) { trebleGain = it }
            }
        }
    }
}

@Composable
fun EqBandSlider(label: String, value: Float, onValueChange: (Float) -> Unit) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, fontSize = 13.sp, color = Color(0xFF94A3B8))
            Text(
                text = "${if (value >= 0) "+" else ""}${String.format("%.1f", value)} dB",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00E5FF)
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = -15f..15f,
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFF00E5FF),
                activeTrackColor = Color(0xFF00E5FF),
                inactiveTrackColor = Color(0xFF1E293B)
            )
        )
    }
}

@Composable
fun SettingsTab(
    onOpenAccessibility: () -> Unit,
    onOpenOverlay: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "PERMISOS Y ESTADO DEL SISTEMA",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF151C28))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = onOpenAccessibility,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B))
                ) {
                    Text("1. Servicio de Accesibilidad (Capturar Botones)", color = Color(0xFF00E5FF))
                }
                Button(
                    onClick = onOpenOverlay,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B))
                ) {
                    Text("2. Permiso Ventana Flotante (HUD en pantalla)", color = Color(0xFF00E5FF))
                }
            }
        }
    }
}
