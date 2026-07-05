package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_settings")
data class SettingsEntity(
    @PrimaryKey val username: String = "admin",
    val connectionMode: String = "ADB", // "ADB" or "WIFI"
    val ipAddress: String = "192.168.1.15",
    val port: String = "8765",
    val password: String = "••••••••",
    val autoShutdown: Boolean = true,
    val activeComputerId: Int? = null,
    val tempUnit: String = "C",
    val cpuTempLimit: Int = 80,
    val cpuLoadLimit: Int = 90,
    val gpuTempLimit: Int = 80,
    val gpuLoadLimit: Int = 90,
    val apiKey: String = "",  // API Key para autenticar con el agente
    val customThemeColors: String = ""
)
