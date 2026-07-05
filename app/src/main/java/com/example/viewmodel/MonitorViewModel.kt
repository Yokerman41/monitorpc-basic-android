package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.SettingsEntity
import com.example.data.SettingsRepository
import com.example.data.ComputerEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject
import java.io.IOException
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Job
import com.example.ui.utils.tr
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.net.wifi.WifiManager
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okhttp3.Response
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.supervisorScope
import java.net.InetAddress

// Process Data Class
data class ProcessItem(
    val pid: Int,
    val name: String,
    val cpu: Double,
    val memoryMb: Int,
    val iconName: String, // "terminal", "browser", "code", "settings_input_component"
    val status: String // "High Load", "Normal", "Idle", "Stable"
)

// Individual CPU Core Info
data class CpuCoreInfo(
    val id: Int,
    val speedGhz: Double,
    val tempCelsius: Int,
    val loadPercentage: Double
)

// Storage Drive Info
data class DriveInfo(
    val id: Int,
    val name: String,
    val type: String, // "NVMe SSD", "SATA SSD", "HDD 7200RPM"
    val lifePct: Int,
    val status: String, // "Excelente", "Atención", "Estable"
    val iconName: String, // "memory", "storage", "hard_drive"
    val tempCelsius: Int,
    val usedGb: Double,
    val totalGb: Double
)

// SMART Metric item
data class SmartMetric(
    val attribute: String,
    val value: Int,
    val worst: Int,
    val threshold: Int,
    val status: String
)

data class DiscoveredComputer(
    val name: String,
    val ip: String,
    val port: String
)

data class ScriptItem(
    val id: String,
    val name: String,
    val command: String
)

// Entire UI State Container
data class MonitorUiState(
    // Connection configs (persisted)
    val connectionMode: String = "ADB",
    val ipAddress: String = "",
    val port: String = "8765",
    val username: String = "admin",
    val password: String = "••••••••",
    val apiKey: String = "",
    val customThemeColors: String = "",
    val autoShutdown: Boolean = true,
    val activeComputerId: Int? = null,
    val tempUnit: String = "C",
    val computers: List<ComputerEntity> = emptyList(),
    val showComputersScreen: Boolean = false,
    val showProcessesScreen: Boolean = false,
    val showScriptsScreen: Boolean = false,
    val isProUser: Boolean = true,
    val showProRequiredDialog: Boolean = false,

    // Pairing via PIN
    val pairingPin: String = "",
    val isPairing: Boolean = false,
    val pairingError: String? = null,

    // Warning Thresholds
    val cpuTempLimit: Int = 80,
    val cpuLoadLimit: Int = 90,
    val gpuTempLimit: Int = 80,
    val gpuLoadLimit: Int = 90,

    // Real-Time metrics from agent
    val pcUptimeFormatted: String = "0s",
    val pcOsName: String = "Desconocido",
    val pcComputerName: String = "",
    val diskReadSpeedFormatted: String = "0.0 KB/s",
    val diskWriteSpeedFormatted: String = "0.0 KB/s",
    val connectionError: String? = null,
    val agentVersion: String = "",

    // UI feedback (Snackbar)
    val feedbackMessage: String? = null,

    // Confirmation dialog flags
    val showLockConfirmDialog: Boolean = false,
    val showSuspendConfirmDialog: Boolean = false,
    val showLogoutPcConfirmDialog: Boolean = false,

    // Real-time fluctuating metrics
    val isLoggedIn: Boolean = false,
    val showTutorial: Boolean = false,
    val cpuName: String = "Desconectado",
    val cpuOverallLoad: Int = 0,
    val cpuOverallTemp: Int = 0,
    val cpuOverallSpeedGhz: Double = 0.0,
    val cpuVoltage: Double = 0.0,
    val cpuSparklineHistory: List<Int> = List(30) { 0 },
    val cpuMaxFreqGhz: Double = 0.0,
    val cpuAvgFreqGhz: Double = 0.0,
    val cpuMaxTemp: Int = 0,
    val cpuMinTemp: Int = 0,
    val cpuTempHistory: List<Int> = List(30) { 0 },

    // Cores (1-8)
    val cores: List<CpuCoreInfo> = emptyList(),

    // GPU Metrics
    val gpuModel: String = "No conectado",
    val gpuLoad: Int = 0,
    val gpuLoad3d: Int = 0,
    val gpuVideoEncode: Int = 0,
    val gpuVideoDecode: Int = 0,
    val gpuVramUsedGb: Double = 0.0,
    val gpuVramTotalGb: Double = 0.0,
    val gpuTemp: Int = 0,

    val ramUsedGb: Double = 0.0,
    val ramTotalGb: Double = 0.0,
    val ramManufacturer: String = "Corsair Vengeance",
    val ramType: String = "DDR4",
    val ramSpeedMhz: Int = 3200,
    val ramModulesCount: Int = 2,

    // Wifi / Latency
    val wifiSSID: String = "WiFi",
    val wifiFrequency: String = "",
    val wifiLatencyMs: Int = 0,
    val wifiIpAddress: String = "",
    val wifiSignalStrength: String = "",
    val networkUploadSpeed: String = "0 B/s",
    val networkDownloadSpeed: String = "0 B/s",

    // Drives
    val drives: List<DriveInfo> = emptyList(),

    // Detailed SMART
    val smartMetrics: List<SmartMetric> = emptyList(),

    // Global Storage summary
    val globalStorageTotalTb: Double = 0.0,
    val globalStorageUsedTb: Double = 0.0,
    val globalStorageFreeTb: Double = 0.0,
    val globalStorageUsedPct: Int = 0,

    // Processes List
    val totalProcesses: Int = 0,
    val totalThreads: Int = 0,
    val processesSearchKeyword: String = "",
    val processesSortByCpu: Boolean = false,
    val processes: List<ProcessItem> = emptyList(),

    // Fan curve config
    val fansEnabled: Boolean = false,
    val currentFanSpeedRpm: Int = 0,

    // Connection testing simulated feedback State
    val testConnectionRunStatus: TestStatus = TestStatus.IDLE,
    val isConnected: Boolean = false,
    val gpuSparklineHistory: List<Int> = List(30) { 0 },
    val ramSparklineHistory: List<Int> = List(30) { 0 },
    val discoveredComputers: List<DiscoveredComputer> = emptyList(),
    val scripts: List<ScriptItem> = emptyList(),
    val fps: Double = 0.0,
    val cpuViewMode: String = "DEFAULT",
    val gpuViewMode: String = "DEFAULT",
    val ramViewMode: String = "DEFAULT"
)

sealed class TestStatus {
    object IDLE : TestStatus()
    object TESTING : TestStatus()
    data class SUCCESS(val message: String) : TestStatus()
    data class FAILURE(val message: String) : TestStatus()
}

class MonitorViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = SettingsRepository(db.settingsDao(), db.computerDao())

    private val _uiState = MutableStateFlow(MonitorUiState())
    val uiState: StateFlow<MonitorUiState> = _uiState.asStateFlow()

    private val prefs = application.getSharedPreferences("dashboard_view_modes", android.content.Context.MODE_PRIVATE)

    // Base HTTP client shared connection pool
    private val baseHttpClient = OkHttpClient()

    // Separate HTTP client for REST polling (fast timeouts, no retry)
    private val httpClient = baseHttpClient.newBuilder()
        .connectTimeout(5000, java.util.concurrent.TimeUnit.MILLISECONDS)
        .readTimeout(5000, java.util.concurrent.TimeUnit.MILLISECONDS)
        .writeTimeout(5000, java.util.concurrent.TimeUnit.MILLISECONDS)
        .retryOnConnectionFailure(false)
        .build()

    // Separate WebSocket client (longer read timeout for persistent connection)
    private val wsClient = baseHttpClient.newBuilder()
        .connectTimeout(5000, java.util.concurrent.TimeUnit.MILLISECONDS)
        .readTimeout(0, java.util.concurrent.TimeUnit.MILLISECONDS)  // 0 = no timeout for WS
        .writeTimeout(5000, java.util.concurrent.TimeUnit.MILLISECONDS)
        .pingInterval(3000, java.util.concurrent.TimeUnit.MILLISECONDS)
        .retryOnConnectionFailure(false)
        .build()
    private var lastBytesSent: Long = 0L
    private var lastBytesRecv: Long = 0L
    private var lastPollTime: Long = 0L
    private var consecutiveFailures = 0

    private val _screenBitmap = MutableStateFlow<ImageBitmap?>(null)
    val screenBitmap: StateFlow<ImageBitmap?> = _screenBitmap.asStateFlow()

    private var mirrorWebSocket: WebSocket? = null
    private var telemetryJob: Job? = null
    private var settingsCollectionJob: Job? = null
    private var computersCollectionJob: Job? = null

    private var nsdManager: NsdManager? = null
    private var discoveryListener: NsdManager.DiscoveryListener? = null
    private var multicastLock: WifiManager.MulticastLock? = null
    private var webSocket: WebSocket? = null
    private var isWebSocketConnected = false

    // Track if the telemetry loop is already running to avoid double-starts
    private var telemetryLoopActive = false

    init {
        // Load persisted view modes and theme
        val cpuMode = prefs.getString("cpu", "DEFAULT") ?: "DEFAULT"
        val gpuMode = prefs.getString("gpu", "DEFAULT") ?: "DEFAULT"
        val ramMode = prefs.getString("ram", "DEFAULT") ?: "DEFAULT"
        val themeJson = prefs.getString("theme", "") ?: ""
        _uiState.value = _uiState.value.copy(
            cpuViewMode = cpuMode,
            gpuViewMode = gpuMode,
            ramViewMode = ramMode,
            customThemeColors = themeJson,
            isProUser = true,
            isLoggedIn = true,
            username = "admin"
        )

        startNsdDiscovery()
        startUserCollection("admin")
    }

    private fun startUserCollection(username: String) {
        settingsCollectionJob?.cancel()
        computersCollectionJob?.cancel()

        settingsCollectionJob = viewModelScope.launch {
            repository.getSettingsFlow(username).collect { entity ->
                if (entity != null) {
                    val prevIp = _uiState.value.ipAddress
                    _uiState.value = _uiState.value.copy(
                        connectionMode = entity.connectionMode,
                        ipAddress = entity.ipAddress,
                        port = entity.port,
                        username = entity.username,
                        password = entity.password,
                        autoShutdown = entity.autoShutdown,
                        activeComputerId = entity.activeComputerId,
                        tempUnit = entity.tempUnit,
                        cpuTempLimit = entity.cpuTempLimit,
                        cpuLoadLimit = entity.cpuLoadLimit,
                        gpuTempLimit = entity.gpuTempLimit,
                        gpuLoadLimit = entity.gpuLoadLimit,
                        apiKey = entity.apiKey,
                        customThemeColors = entity.customThemeColors
                    )
                    prefs.edit().putString("theme", entity.customThemeColors).apply()
                    if (!telemetryLoopActive || entity.ipAddress != prevIp) {
                        startTelemetryLoop()
                    }
                }
            }
        }

        computersCollectionJob = viewModelScope.launch {
            repository.getComputersFlow(username).collect { list ->
                _uiState.value = _uiState.value.copy(computers = list)
            }
        }
    }

    /** Helper to build a Request with the API key header attached if set. */
    private fun buildRequest(url: String): Request {
        val key = _uiState.value.apiKey
        val builder = Request.Builder().url(url)
        if (key.isNotBlank()) builder.addHeader("X-API-Key", key)
        val licenseHeader = if (_uiState.value.isProUser) "PRO" else "BASIC"
        builder.addHeader("X-App-License", licenseHeader)
        return builder.build()
    }

    /** Helper to build a POST Request with the API key header. */
    private fun buildPostRequest(url: String, body: okhttp3.RequestBody): Request {
        val key = _uiState.value.apiKey
        val builder = Request.Builder().url(url).post(body)
        if (key.isNotBlank()) builder.addHeader("X-API-Key", key)
        val licenseHeader = if (_uiState.value.isProUser) "PRO" else "BASIC"
        builder.addHeader("X-App-License", licenseHeader)
        return builder.build()
    }

    private fun parseAndSetMetrics(json: JSONObject, latency: Int = 0) {
        try {
            val cpuJson = json.getJSONObject("cpu")
            val gpuJson = json.optJSONObject("gpu")
            val ramJson = json.getJSONObject("ram")
            val netJson = json.getJSONObject("network")
            val disksJson = json.optJSONArray("disks")
            val systemJson = json.optJSONObject("system")
            val diskIoJson = json.optJSONObject("disk_io")
            val fps = json.optDouble("fps", 0.0)

            val uptimeFormatted = if (systemJson != null) {
                val uptimeSeconds = systemJson.optLong("uptime", 0L)
                formatUptime(uptimeSeconds)
            } else {
                "0s"
            }
            val osName = systemJson?.optString("os_name", "Desconocido") ?: "Desconocido"
            val computerName = systemJson?.optString("computer_name", "") ?: ""
            
            val diskReadBytesSec = diskIoJson?.optDouble("read_bytes_sec", 0.0) ?: 0.0
            val diskWriteBytesSec = diskIoJson?.optDouble("write_bytes_sec", 0.0) ?: 0.0
            val diskReadSpeedFormatted = formatSpeed(diskReadBytesSec)
            val diskWriteSpeedFormatted = formatSpeed(diskWriteBytesSec)

            // CPU
            val cpuUsage = cpuJson.getDouble("usage_percent").toInt()
            val newCpuName = cpuJson.optString("name", _uiState.value.cpuName)
            val totalProc = cpuJson.optInt("total_processes", _uiState.value.totalProcesses)
            val totalThreads = cpuJson.optInt("total_threads", _uiState.value.totalThreads)

            // RAM
            val ramUsed = ramJson.getDouble("used_gb")
            val ramTotal = ramJson.getDouble("total_gb")
            val ramManufacturer = ramJson.optString("manufacturer", "Corsair Vengeance")
            val ramType = ramJson.optString("type", "DDR4")
            val ramSpeed = ramJson.optInt("speed_mhz", 3200)
            val ramModules = ramJson.optInt("modules_count", 2)

            // Network
            val bytesSent = netJson.getLong("bytes_sent")
            val bytesRecv = netJson.getLong("bytes_recv")

            val currentTime = System.currentTimeMillis()
            var uploadSpeed = "0.0 KB/s"
            var downloadSpeed = "0.0 KB/s"

            if (lastPollTime > 0L && currentTime > lastPollTime) {
                val timeSec = (currentTime - lastPollTime) / 1000.0
                val uploadBytesPerSec = ((bytesSent - lastBytesSent) / timeSec).coerceAtLeast(0.0)
                val downloadBytesPerSec = ((bytesRecv - lastBytesRecv) / timeSec).coerceAtLeast(0.0)
                uploadSpeed = formatSpeed(uploadBytesPerSec)
                downloadSpeed = formatSpeed(downloadBytesPerSec)
            }

            lastBytesSent = bytesSent
            lastBytesRecv = bytesRecv
            lastPollTime = currentTime

            // GPU — real data si nvidia-smi disponible, sino simulado
            var newGpuModel = _uiState.value.gpuModel
            var newGpuLoad = (65 + Random.nextInt(-3, 4)).coerceIn(58, 72)
            var newGpuTemp = (62 + Random.nextInt(-1, 2)).coerceIn(59, 66)
            var newGpuVramUsed = _uiState.value.gpuVramUsedGb
            var newGpuVramTotal = _uiState.value.gpuVramTotalGb
            var newGpuLoad3d = _uiState.value.gpuLoad3d
            var newGpuVideoEncode = _uiState.value.gpuVideoEncode
            var newGpuVideoDecode = _uiState.value.gpuVideoDecode

            if (gpuJson != null) {
                newGpuModel = gpuJson.optString("name", newGpuModel)
                if (gpuJson.optBoolean("available", false)) {
                    newGpuLoad = gpuJson.optInt("load", newGpuLoad)
                    newGpuTemp = gpuJson.optInt("temp", newGpuTemp)
                    newGpuVramUsed = gpuJson.optDouble("vram_used_gb", newGpuVramUsed)
                    newGpuVramTotal = gpuJson.optDouble("vram_total_gb", newGpuVramTotal)
                    newGpuLoad3d = gpuJson.optInt("load_3d", newGpuLoad)
                    newGpuVideoEncode = gpuJson.optInt("video_encode", 0)
                    newGpuVideoDecode = gpuJson.optInt("video_decode", 0)
                }
            }

            // CPU speed fluctuation
            val nextCpuSpeed = if (cpuJson.has("current_speed_ghz")) {
                cpuJson.getDouble("current_speed_ghz")
            } else {
                (4.5 + Random.nextDouble(-0.1, 0.25)).coerceIn(4.2, 5.0)
            }
            val roundedSpeed = Math.round(nextCpuSpeed * 10.0) / 10.0
            
            val nextCpuTemp = if (cpuJson.has("temperature_c")) {
                cpuJson.getInt("temperature_c")
            } else {
                val loadFactor = cpuUsage / 100.0
                val baseTemp = 38.0 + (loadFactor * 42.0)
                (baseTemp + Random.nextInt(-2, 3)).toInt().coerceIn(30, 95)
            }
            
            val nextCpuVoltage = if (cpuJson.has("voltage_v")) {
                cpuJson.getDouble("voltage_v")
            } else {
                Math.round((1.34 + Random.nextDouble(-0.02, 0.03)) * 100.0) / 100.0
            }

            // CPU overall load sparkline history (last 30 points)
            val nextSparkline = _uiState.value.cpuSparklineHistory.toMutableList()
            if (nextSparkline.size >= 30) nextSparkline.removeAt(0)
            nextSparkline.add(cpuUsage)

            // CPU temp history window (last 30 points)
            val nextTempHistory = _uiState.value.cpuTempHistory.toMutableList()
            if (nextTempHistory.size >= 30) nextTempHistory.removeAt(0)
            nextTempHistory.add(nextCpuTemp)

            // GPU sparkline history
            val nextGpuSparkline = _uiState.value.gpuSparklineHistory.toMutableList()
            if (nextGpuSparkline.size >= 30) nextGpuSparkline.removeAt(0)
            nextGpuSparkline.add(newGpuLoad)

            // RAM sparkline history
            val nextRamSparkline = _uiState.value.ramSparklineHistory.toMutableList()
            if (nextRamSparkline.size >= 30) nextRamSparkline.removeAt(0)
            val ramPct = if (ramTotal > 0.0) ((ramUsed / ramTotal) * 100).toInt() else 0
            nextRamSparkline.add(ramPct)

            // Cores update
            val perCoreLoadArray = cpuJson.optJSONArray("per_core_load")
            val logicalCoresCount = cpuJson.optInt("logical_cores", 8)
            val updatedCores = mutableListOf<CpuCoreInfo>()

            for (cIdx in 0 until logicalCoresCount) {
                val coreLoadPct = if (perCoreLoadArray != null && cIdx < perCoreLoadArray.length()) {
                    perCoreLoadArray.getDouble(cIdx)
                } else {
                    cpuUsage.toDouble()
                }
                val loadPercentage = coreLoadPct / 100.0

                val coreSpeed = (roundedSpeed + Random.nextDouble(-0.3, 0.3)).coerceIn(1.5, 6.0)
                val roundedCoreSpeed = Math.round(coreSpeed * 10.0) / 10.0

                val coreTemp = (nextCpuTemp + Random.nextInt(-4, 5)).coerceIn(30, 95)

                updatedCores.add(
                    CpuCoreInfo(
                        id = cIdx + 1,
                        speedGhz = roundedCoreSpeed,
                        tempCelsius = coreTemp,
                        loadPercentage = loadPercentage
                    )
                )
            }

            // Calculate dynamic statistics
            val maxCoreSpeed = updatedCores.maxOfOrNull { it.speedGhz } ?: roundedSpeed
            val avgCoreSpeed = updatedCores.map { it.speedGhz }.average().let { Math.round(it * 10.0) / 10.0 }
            val maxCoreTemp = updatedCores.maxOfOrNull { it.tempCelsius } ?: nextCpuTemp
            val minCoreTemp = updatedCores.minOfOrNull { it.tempCelsius } ?: nextCpuTemp

            // Discos
            var updatedDrives = _uiState.value.drives.map { drive ->
                val change = Random.nextInt(-1, 2)
                drive.copy(tempCelsius = (drive.tempCelsius + change).coerceIn(30, 62))
            }

            if (disksJson != null && disksJson.length() > 0) {
                val newDrives = mutableListOf<DriveInfo>()
                for (i in 0 until disksJson.length()) {
                    val d = disksJson.getJSONObject(i)
                    val dName = d.optString("name", "Disco ${i + 1}")
                    val dMountpoint = d.optString("mountpoint", "")
                    val dTotalGb = d.optDouble("total_gb", 0.0)
                    val dUsedGb = d.optDouble("used_gb", 0.0)
                    val dPercent = d.optDouble("percent", 0.0).toInt()
                    val dType = d.optString("type", "HDD")

                    val dIconName = when {
                        dType.contains("NVMe", ignoreCase = true) -> "memory"
                        dType.contains("M.2", ignoreCase = true) -> "memory"
                        dType.contains("SSD", ignoreCase = true) -> "storage"
                        else -> "hard_drive"
                    }
                    val dStatus = if (d.has("status")) {
                        d.getString("status")
                    } else {
                        when {
                            dPercent > 90 -> "Atención"
                            dPercent > 70 -> "Estable"
                            else -> "Excelente"
                        }
                    }
                    val dLifePct = if (d.has("life_pct")) {
                        d.getInt("life_pct")
                    } else {
                        (100 - dPercent).coerceIn(0, 100)
                    }
                    val existingTemp = if (d.has("temp_c")) {
                        d.getInt("temp_c")
                    } else {
                        _uiState.value.drives.getOrNull(i)?.tempCelsius ?: 40
                    }

                    val driveLetter = dMountpoint.trimEnd('\\').trimEnd('/')
                    val displayName = if (dName.startsWith("Disco ")) {
                        dName
                    } else {
                        "$dName · $driveLetter"
                    }

                    newDrives.add(
                        DriveInfo(
                            id = i + 1,
                            name = displayName,
                            type = dType,
                            lifePct = dLifePct,
                            status = dStatus,
                            iconName = dIconName,
                            tempCelsius = existingTemp,
                            usedGb = dUsedGb,
                            totalGb = dTotalGb
                        )
                    )
                }
                updatedDrives = newDrives
            }
            val totalGb = updatedDrives.sumOf { it.totalGb }
            val usedGb = updatedDrives.sumOf { it.usedGb }
            val freeGb = (totalGb - usedGb).coerceAtLeast(0.0)
            
            val totalTb = Math.round((totalGb / 1024.0) * 10.0) / 10.0
            val usedTb = Math.round((usedGb / 1024.0) * 10.0) / 10.0
            val freeTb = Math.round((freeGb / 1024.0) * 10.0) / 10.0
            val usedPct = if (totalGb > 0.0) ((usedGb / totalGb) * 100.0).toInt().coerceIn(0, 100) else 0

            // Agent version (optional field)
            val agentVersion = systemJson?.optString("agent_version", "") ?: ""

            val smartJson = json.optJSONArray("smart_metrics")
            val nextSmartMetrics = mutableListOf<SmartMetric>()
            if (smartJson != null && smartJson.length() > 0) {
                for (i in 0 until smartJson.length()) {
                    val m = smartJson.getJSONObject(i)
                    nextSmartMetrics.add(
                        SmartMetric(
                            attribute = m.optString("attribute", ""),
                            value = m.optInt("value", 100),
                            worst = m.optInt("worst", 100),
                            threshold = m.optInt("threshold", 0),
                            status = m.optString("status", "OK")
                        )
                    )
                }
            }

            // Apply updates on UI thread
            viewModelScope.launch(Dispatchers.Main) {
                _uiState.value = _uiState.value.copy(
                    cpuName = newCpuName,
                    totalProcesses = totalProc,
                    totalThreads = totalThreads,
                    cpuOverallLoad = cpuUsage,
                    cpuOverallSpeedGhz = roundedSpeed,
                    cpuOverallTemp = nextCpuTemp,
                    cpuVoltage = nextCpuVoltage,
                    cpuSparklineHistory = nextSparkline,
                    cpuTempHistory = nextTempHistory,
                    gpuSparklineHistory = nextGpuSparkline,
                    ramSparklineHistory = nextRamSparkline,
                    cpuMaxFreqGhz = maxCoreSpeed,
                    cpuAvgFreqGhz = avgCoreSpeed,
                    cpuMaxTemp = maxCoreTemp,
                    cpuMinTemp = minCoreTemp,
                    gpuModel = newGpuModel,
                    gpuLoad = newGpuLoad,
                    gpuLoad3d = newGpuLoad3d,
                    gpuVideoEncode = newGpuVideoEncode,
                    gpuVideoDecode = newGpuVideoDecode,
                    gpuTemp = newGpuTemp,
                    gpuVramUsedGb = newGpuVramUsed,
                    gpuVramTotalGb = newGpuVramTotal,
                    ramUsedGb = ramUsed,
                    ramTotalGb = ramTotal,
                    ramManufacturer = ramManufacturer,
                    ramType = ramType,
                    ramSpeedMhz = ramSpeed,
                    ramModulesCount = ramModules,
                    cores = updatedCores,
                    wifiLatencyMs = if (latency > 0) latency else _uiState.value.wifiLatencyMs,
                    drives = updatedDrives,
                    globalStorageTotalTb = totalTb,
                    globalStorageUsedTb = usedTb,
                    globalStorageFreeTb = freeTb,
                    globalStorageUsedPct = usedPct,
                    networkUploadSpeed = uploadSpeed,
                    networkDownloadSpeed = downloadSpeed,
                    pcUptimeFormatted = uptimeFormatted,
                    pcOsName = osName,
                    pcComputerName = computerName.ifEmpty { _uiState.value.pcComputerName },
                    diskReadSpeedFormatted = diskReadSpeedFormatted,
                    diskWriteSpeedFormatted = diskWriteSpeedFormatted,
                    connectionError = null,
                    isConnected = true,
                    agentVersion = agentVersion.ifEmpty { _uiState.value.agentVersion },
                    smartMetrics = if (nextSmartMetrics.isNotEmpty()) nextSmartMetrics else _uiState.value.smartMetrics,
                    fps = fps
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startTelemetryLoop() {
        telemetryJob?.cancel()
        telemetryLoopActive = false
        stopWebSocketStream()
        if (_uiState.value.activeComputerId == null) return
        telemetryLoopActive = true
        telemetryJob = viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                if (_uiState.value.activeComputerId == null) break

                // If WebSocket stream is connected, skip HTTP polling
                if (isWebSocketConnected) {
                    delay(2000)
                    continue
                }

                val ip = _uiState.value.ipAddress
                val port = _uiState.value.port.ifEmpty { "8765" }
                val url = "http://$ip:$port/metrics"
                val startTime = System.currentTimeMillis()

                try {
                    val request = buildRequest(url)
                    httpClient.newCall(request).execute().use { response ->
                        if (response.isSuccessful) {
                            val bodyString = response.body?.string() ?: ""
                            val json = JSONObject(bodyString)
                            consecutiveFailures = 0
                            val latency = (System.currentTimeMillis() - startTime).toInt()
                            parseAndSetMetrics(json, latency)

                            // Start WebSocket if not connected (any mode with valid IP)
                            if (!isWebSocketConnected) {
                                withContext(Dispatchers.Main) {
                                    startWebSocketStream()
                                }
                            }
                        } else {
                            throw IOException("HTTP error ${response.code}")
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    if (ip == _uiState.value.ipAddress) {
                        consecutiveFailures++
                        val errorMsg = getErrorMessage(e)
                        withContext(Dispatchers.Main) {
                            // #12: Increased threshold to 5 to avoid flickering on minor packet loss
                            if (consecutiveFailures >= 5) {
                                _uiState.value = _uiState.value.copy(
                                    isConnected = false,
                                    connectionError = errorMsg,
                                    cpuOverallLoad = 0,
                                    cpuOverallTemp = 0,
                                    cpuOverallSpeedGhz = 0.0,
                                    cpuVoltage = 0.0,
                                    cpuMaxFreqGhz = 0.0,
                                    cpuAvgFreqGhz = 0.0,
                                    cpuMaxTemp = 0,
                                    cpuMinTemp = 0,
                                    cores = emptyList(),
                                    gpuModel = "No conectado",
                                    gpuLoad = 0,
                                    gpuLoad3d = 0,
                                    gpuVideoEncode = 0,
                                    gpuVideoDecode = 0,
                                    gpuVramUsedGb = 0.0,
                                    gpuVramTotalGb = 0.0,
                                    gpuTemp = 0,
                                    ramUsedGb = 0.0,
                                    ramTotalGb = 0.0,
                                    drives = emptyList(),
                                    globalStorageTotalTb = 0.0,
                                    globalStorageUsedTb = 0.0,
                                    globalStorageFreeTb = 0.0,
                                    globalStorageUsedPct = 0,
                                    totalProcesses = 0,
                                    totalThreads = 0,
                                    processes = emptyList(),
                                    fansEnabled = false,
                                    currentFanSpeedRpm = 0,
                                    fps = 0.0
                                )
                                stopWebSocketStream()
                            } else {
                                _uiState.value = _uiState.value.copy(
                                    connectionError = "Reconectando... (${consecutiveFailures}/5)"
                                )
                            }
                        }
                    }
                }

                delay(3000) // Increased delay to 3s for better stability on busy networks
            }
            telemetryLoopActive = false
        }
    }

    fun startWebSocketStream() {
        stopWebSocketStream()
        val ip = _uiState.value.ipAddress
        if (ip.isEmpty() || _uiState.value.activeComputerId == null) return
        val wsPort = _uiState.value.port.toIntOrNull()?.let { it + 1 } ?: 8766
        val wsUrl = "ws://$ip:$wsPort"
        val key = _uiState.value.apiKey
        val reqBuilder = Request.Builder().url(wsUrl)
        if (key.isNotBlank()) reqBuilder.addHeader("X-API-Key", key)
        val request = reqBuilder.build()

        // Use dedicated wsClient so it doesn't share thread pool with REST calls
        webSocket = wsClient.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                isWebSocketConnected = true
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    val json = JSONObject(text)
                    parseAndSetMetrics(json)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                isWebSocketConnected = false
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                isWebSocketConnected = false
            }
        })
    }

    fun stopWebSocketStream() {
        webSocket?.close(1000, "Cerrando stream")
        webSocket = null
        isWebSocketConnected = false
    }

    fun startNsdDiscovery() {
        if (nsdManager != null) return
        val context = getApplication<Application>().applicationContext
        nsdManager = context.getSystemService(android.content.Context.NSD_SERVICE) as NsdManager

        val wifiManager = context.getSystemService(android.content.Context.WIFI_SERVICE) as WifiManager
        multicastLock = wifiManager.createMulticastLock("monitorpc_discovery")
        multicastLock?.setReferenceCounted(false)
        try {
            multicastLock?.acquire()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        discoveryListener = object : NsdManager.DiscoveryListener {
            override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
                stopNsdDiscovery()
            }
            override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
                stopNsdDiscovery()
            }
            override fun onDiscoveryStarted(serviceType: String) {}
            override fun onDiscoveryStopped(serviceType: String) {}
            override fun onServiceFound(serviceInfo: NsdServiceInfo) {
                // Log service found for debugging (broad match)
                val type = serviceInfo.serviceType.lowercase()
                val name = serviceInfo.serviceName.lowercase()
                
                // Many PC agents register as _monitorpc._tcp. or sometimes _http._tcp. with a custom name
                if (type.contains("monitorpc") || name.contains("monitorpc") || type.contains("_http")) {
                    resolveServiceWithRetry(serviceInfo)
                }
            }
            override fun onServiceLost(serviceInfo: NsdServiceInfo) {
                viewModelScope.launch(Dispatchers.Main) {
                    val currentDiscovered = _uiState.value.discoveredComputers.filter { it.name != serviceInfo.serviceName }
                    _uiState.value = _uiState.value.copy(discoveredComputers = currentDiscovered)
                }
            }
        }
        
        try {
            // Some systems need the trailing dot, others don't. We'll try the standard one with dot first.
            nsdManager?.discoverServices("_monitorpc._tcp.", NsdManager.PROTOCOL_DNS_SD, discoveryListener)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun resolveServiceWithRetry(serviceInfo: NsdServiceInfo, retryCount: Int = 0) {
        nsdManager?.resolveService(serviceInfo, object : NsdManager.ResolveListener {
            override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                if (retryCount < 3) {
                    viewModelScope.launch {
                        delay(1000)
                        resolveServiceWithRetry(serviceInfo, retryCount + 1)
                    }
                }
            }

            override fun onServiceResolved(resolvedServiceInfo: NsdServiceInfo) {
                val host = resolvedServiceInfo.host
                val address = host.hostAddress ?: ""
                val port = resolvedServiceInfo.port
                val name = resolvedServiceInfo.serviceName

                // Handle IPv6 zone indices and ensure clean address
                val cleanHost = if (address.contains("%")) address.substringBefore("%") else address
                
                if (cleanHost.isNotEmpty()) {
                    viewModelScope.launch(Dispatchers.Main) {
                        val currentDiscovered = _uiState.value.discoveredComputers.toMutableList()
                        if (currentDiscovered.none { it.ip == cleanHost }) {
                            currentDiscovered.add(DiscoveredComputer(name, cleanHost, port.toString()))
                            _uiState.value = _uiState.value.copy(discoveredComputers = currentDiscovered)
                        }
                    }
                }
            }
        })
    }

    fun stopNsdDiscovery() {
        try {
            multicastLock?.let {
                if (it.isHeld) it.release()
            }
            multicastLock = null

            if (nsdManager != null && discoveryListener != null) {
                nsdManager?.stopServiceDiscovery(discoveryListener)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            nsdManager = null
            discoveryListener = null
        }
    }

    fun refreshDiscovery() {
        stopNsdDiscovery()
        _uiState.value = _uiState.value.copy(discoveredComputers = emptyList())
        startNsdDiscovery()
        // Fallback: escaneo paralelo de subred HTTP (funciona sin mDNS)
        startSubnetScan()
    }

    /**
     * Obtiene la IP del dispositivo Android en la red WiFi actual.
     */
    private fun getDeviceLocalIp(): String? {
        val context = getApplication<Application>().applicationContext
        val wifiManager = context.getSystemService(android.content.Context.WIFI_SERVICE) as? WifiManager
        val info = wifiManager?.connectionInfo ?: return null
        val ipInt = info.ipAddress
        if (ipInt == 0) return null
        return String.format(
            "%d.%d.%d.%d",
            ipInt and 0xff,
            ipInt shr 8 and 0xff,
            ipInt shr 16 and 0xff,
            ipInt shr 24 and 0xff
        )
    }

    /**
     * Escanea en paralelo las 254 IPs de la subred /24 del teléfono probando el
     * endpoint HTTP del agente (/health). Si responde, agrega el equipo a discoveredComputers.
     * Timeout muy corto por host (600ms) para que el scan completo dure ~3-4s.
     */
    private fun startSubnetScan() {
        val localIp = getDeviceLocalIp() ?: return
        val subnet = localIp.substringBeforeLast(".")
        val agentPort = _uiState.value.port.ifEmpty { "8765" }

        // Cliente HTTP ultra-rápido solo para el scan
        val scanClient = OkHttpClient.Builder()
            .connectTimeout(600, java.util.concurrent.TimeUnit.MILLISECONDS)
            .readTimeout(600, java.util.concurrent.TimeUnit.MILLISECONDS)
            .retryOnConnectionFailure(false)
            .build()

        viewModelScope.launch(Dispatchers.IO) {
            supervisorScope {
                val jobs = (1..254).map { lastOctet ->
                    async {
                        val candidateIp = "$subnet.$lastOctet"
                        if (candidateIp == localIp) return@async // skip device's own IP
                        try {
                            val url = "http://$candidateIp:$agentPort/health"
                            val req = Request.Builder().url(url).get().build()
                            scanClient.newCall(req).execute().use { resp ->
                                if (resp.isSuccessful) {
                                    // Intentamos extraer el nombre del hostname del agente
                                    val body = resp.body?.string() ?: ""
                                    val agentName = try {
                                        val json = JSONObject(body)
                                        json.optString("hostname", "")
                                            .ifEmpty { json.optString("agent_name", "") }
                                            .ifEmpty { "MonitorPC ($candidateIp)" }
                                    } catch (e: Exception) {
                                        "MonitorPC ($candidateIp)"
                                    }
                                    withContext(Dispatchers.Main) {
                                        val current = _uiState.value.discoveredComputers.toMutableList()
                                        if (current.none { it.ip == candidateIp }) {
                                            current.add(DiscoveredComputer(agentName, candidateIp, agentPort))
                                            _uiState.value = _uiState.value.copy(discoveredComputers = current)
                                        }
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            // Host no encontrado o sin agente — ignorar
                        }
                    }
                }
                jobs.forEach { it.await() }
            }
        }
    }

    fun refreshProcesses() {
        _uiState.value.activeComputerId ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val ip = _uiState.value.ipAddress
            val port = _uiState.value.port.ifEmpty { "8765" }
            val url = "http://$ip:$port/processes"
            try {
                val request = buildRequest(url)
                httpClient.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val bodyString = response.body?.string() ?: ""
                        val jsonArray = org.json.JSONArray(bodyString)
                        val newList = mutableListOf<ProcessItem>()
                        for (i in 0 until jsonArray.length()) {
                            val obj = jsonArray.getJSONObject(i)
                            val pid = obj.getInt("pid")
                            val name = obj.getString("name")
                            val cpu = obj.getDouble("cpu")
                            val ramMb = obj.optDouble("ram_mb", 0.0).toInt()

                            val icon = when {
                                name.contains("chrome", ignoreCase = true) || name.contains("browser", ignoreCase = true) || name.contains("edge", ignoreCase = true) || name.contains("firefox", ignoreCase = true) -> "browser"
                                name.contains("cmd", ignoreCase = true) || name.contains("powershell", ignoreCase = true) || name.contains("bash", ignoreCase = true) || name.contains("terminal", ignoreCase = true) -> "terminal"
                                name.contains("code", ignoreCase = true) || name.contains("studio", ignoreCase = true) || name.contains("idea", ignoreCase = true) -> "code"
                                else -> "settings_input_component"
                            }

                            val status = when {
                                cpu > 30.0 -> "High Load"
                                cpu > 5.0 -> "Normal"
                                cpu > 0.5 -> "Stable"
                                else -> "Idle"
                            }

                            newList.add(ProcessItem(pid, name, cpu, ramMb, icon, status))
                        }
                        withContext(Dispatchers.Main) {
                            // #13: totalProcesses reflects actual fetched count, not agent cache
                            _uiState.value = _uiState.value.copy(
                                processes = newList,
                                totalProcesses = newList.size
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun killProcess(pid: Int, killAllByName: Boolean = true, onSuccess: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            val ip = _uiState.value.ipAddress
            val port = _uiState.value.port.ifEmpty { "8765" }
            val url = "http://$ip:$port/process/kill"
            val json = JSONObject()
                .put("pid", pid)
                .put("kill_all_by_name", killAllByName)
            val body = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
            try {
                val request = buildPostRequest(url, body)
                httpClient.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        // killed_pids is an array in the response
                        refreshProcesses()
                        withContext(Dispatchers.Main) {
                            showFeedback("✅ Proceso finalizado")
                            onSuccess()
                        }
                    } else if (response.code == 403) {
                        withContext(Dispatchers.Main) { showFeedback("❌ Acceso denegado — proceso del sistema") }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) { showFeedback("❌ Error al finalizar proceso") }
            }
        }
    }

    fun sendMediaControl(action: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val ip = _uiState.value.ipAddress
            val port = _uiState.value.port.ifEmpty { "8765" }
            val url = "http://$ip:$port/media/control"
            val json = JSONObject().put("action", action)
            val body = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
            try {
                val request = buildPostRequest(url, body)
                httpClient.newCall(request).execute().use { }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun refreshScripts() {
        _uiState.value.activeComputerId ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val ip = _uiState.value.ipAddress
            val port = _uiState.value.port.ifEmpty { "8765" }
            val url = "http://$ip:$port/scripts"
            try {
                val request = buildRequest(url)
                httpClient.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val bodyString = response.body?.string() ?: ""
                        val jsonArray = org.json.JSONArray(bodyString)
                        val newList = mutableListOf<ScriptItem>()
                        for (i in 0 until jsonArray.length()) {
                            val obj = jsonArray.getJSONObject(i)
                            val id = obj.getString("id")
                            val name = obj.getString("name")
                            val command = obj.getString("command")
                            newList.add(ScriptItem(id, name, command))
                        }
                        withContext(Dispatchers.Main) {
                            _uiState.value = _uiState.value.copy(scripts = newList)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun runScript(scriptId: String, scriptName: String = "", onSuccess: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            val ip = _uiState.value.ipAddress
            val port = _uiState.value.port.ifEmpty { "8765" }
            val url = "http://$ip:$port/scripts/run/$scriptId"
            try {
                val body = ByteArray(0).toRequestBody(null)
                val request = buildPostRequest(url, body)
                httpClient.newCall(request).execute().use { response ->
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            val label = scriptName.ifEmpty { scriptId }
                            showFeedback("✅ Ejecutado: $label")
                            onSuccess()
                        } else {
                            showFeedback("❌ Error al ejecutar script (${response.code})")
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) { showFeedback("❌ Sin conexión al agente") }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopNsdDiscovery()
        stopWebSocketStream()
        wsClient.dispatcher.executorService.shutdown()
    }

    /** Show a short-lived feedback message (Snackbar). Auto-clears after 3s. */
    fun showFeedback(msg: String) {
        _uiState.value = _uiState.value.copy(feedbackMessage = msg)
        viewModelScope.launch {
            delay(3000)
            _uiState.value = _uiState.value.copy(feedbackMessage = null)
        }
    }

    fun clearFeedback() {
        _uiState.value = _uiState.value.copy(feedbackMessage = null)
    }

    fun activatePro(licenseKey: String): Boolean {
        val isValid = licenseKey.trim().uppercase().startsWith("YOKERMAN-PRO-")
        if (isValid) {
            prefs.edit().putBoolean("is_pro_user", true).apply()
            _uiState.value = _uiState.value.copy(
                isProUser = true,
                feedbackMessage = "¡Plan monitorPC PRO activado con éxito!"
            )
        }
        return isValid
    }

    fun deactivatePro() {
        prefs.edit().putBoolean("is_pro_user", false).apply()
        _uiState.value = _uiState.value.copy(
            isProUser = false,
            feedbackMessage = "Licencia desactivada. Has vuelto al plan básico."
        )
    }

    fun dismissProRequiredDialog() {
        _uiState.value = _uiState.value.copy(showProRequiredDialog = false)
    }

    fun showProRequiredDialog() {
        _uiState.value = _uiState.value.copy(showProRequiredDialog = true)
    }

    fun toggleProcessSortByCpu() {
        _uiState.value = _uiState.value.copy(processesSortByCpu = !_uiState.value.processesSortByCpu)
    }

    fun updateApiKey(key: String) {
        _uiState.value = _uiState.value.copy(apiKey = key)
    }

    fun updatePairingPin(pin: String) {
        if (pin.length <= 6 && pin.all { it.isDigit() }) {
            _uiState.value = _uiState.value.copy(pairingPin = pin, pairingError = null)
        }
    }

    fun pairWithPin(customIp: String? = null, customPort: String? = null, onResult: (String?) -> Unit = {}) {
        if (_uiState.value.computers.size >= 1) {
            _uiState.value = _uiState.value.copy(pairingError = "Límite de la versión básica alcanzado (máx. 1 PC)")
            return
        }
        val pin = _uiState.value.pairingPin
        if (pin.length != 6) {
            _uiState.value = _uiState.value.copy(pairingError = "El PIN debe ser de 6 dígitos")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                _uiState.value = _uiState.value.copy(isPairing = true, pairingError = null)
            }

            val ip = customIp ?: _uiState.value.ipAddress
            val port = (customPort ?: _uiState.value.port).ifEmpty { "8765" }
            val url = "http://$ip:$port/pair"

            try {
                val deviceName = "${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}"
                val json = JSONObject()
                    .put("pin", pin)
                    .put("device_name", deviceName)
                val body = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
                val request = Request.Builder().url(url).post(body).build()

                httpClient.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val respBody = response.body?.string() ?: ""
                        val respJson = JSONObject(respBody)
                        val apiKey = respJson.getString("api_key")

                        withContext(Dispatchers.Main) {
                            _uiState.value = _uiState.value.copy(
                                pairingPin = "",
                                isPairing = false
                            )
                            if (customIp == null) {
                                _uiState.value = _uiState.value.copy(apiKey = apiKey)
                                saveConfig()
                            }
                            showFeedback("✅ Dispositivo vinculado con éxito")
                            onResult(apiKey)
                        }
                    } else {
                        val errorMsg = if (response.code == 401) "PIN incorrecto o expirado" else "Error del servidor: ${response.code}"
                        withContext(Dispatchers.Main) {
                            _uiState.value = _uiState.value.copy(isPairing = false, pairingError = errorMsg)
                            onResult(null)
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(isPairing = false, pairingError = "No se pudo conectar con el agente")
                    onResult(null)
                }
            }
        }
    }

    fun pairUsbAutomatic(onResult: (String?) -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            val url = "http://127.0.0.1:8765/pair/usb"
            try {
                val deviceName = "${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL} (USB)"
                val json = JSONObject().put("device_name", deviceName)
                val body = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
                val request = Request.Builder().url(url).post(body).build()

                httpClient.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val respBody = response.body?.string() ?: ""
                        val respJson = JSONObject(respBody)
                        val apiKey = respJson.getString("api_key")
                        withContext(Dispatchers.Main) {
                            showFeedback("✅ Vinculado por USB")
                            onResult(apiKey)
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            onResult(null)
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onResult(null)
                }
            }
        }
    }

    // Confirmation dialog controls
    fun showLockConfirm() { _uiState.value = _uiState.value.copy(showLockConfirmDialog = true) }
    fun dismissLockConfirm() { _uiState.value = _uiState.value.copy(showLockConfirmDialog = false) }
    fun showSuspendConfirm() { _uiState.value = _uiState.value.copy(showSuspendConfirmDialog = true) }
    fun dismissSuspendConfirm() { _uiState.value = _uiState.value.copy(showSuspendConfirmDialog = false) }
    fun showLogoutPcConfirm() { _uiState.value = _uiState.value.copy(showLogoutPcConfirmDialog = true) }
    fun dismissLogoutPcConfirm() { _uiState.value = _uiState.value.copy(showLogoutPcConfirmDialog = false) }

    // Setters called from UI fields
    fun updateConnectionMode(mode: String) {
        _uiState.value = _uiState.value.copy(connectionMode = mode)
        saveConfig()
        startTelemetryLoop()
    }

    fun updateIpAddress(ip: String) {
        _uiState.value = _uiState.value.copy(ipAddress = ip)
    }

    fun updatePort(port: String) {
        _uiState.value = _uiState.value.copy(port = port)
    }

    fun updateUsername(user: String) {
        _uiState.value = _uiState.value.copy(username = user)
    }

    fun updatePassword(pass: String) {
        _uiState.value = _uiState.value.copy(password = pass)
    }

    fun updateAutoShutdown(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(autoShutdown = enabled)
    }

    fun updateTempUnit(unit: String) {
        _uiState.value = _uiState.value.copy(tempUnit = unit)
        saveConfig()
    }

    fun updateFanStatus(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(fansEnabled = enabled)
    }

    fun updateSearchKeyword(keyword: String) {
        _uiState.value = _uiState.value.copy(processesSearchKeyword = keyword)
    }

    fun updateCpuTempLimit(limit: Int) {
        _uiState.value = _uiState.value.copy(cpuTempLimit = limit)
    }

    fun updateCpuLoadLimit(limit: Int) {
        _uiState.value = _uiState.value.copy(cpuLoadLimit = limit)
    }

    fun updateGpuTempLimit(limit: Int) {
        _uiState.value = _uiState.value.copy(gpuTempLimit = limit)
    }

    fun updateGpuLoadLimit(limit: Int) {
        _uiState.value = _uiState.value.copy(gpuLoadLimit = limit)
    }

    fun updateCustomThemeColors(colors: String) {
        _uiState.value = _uiState.value.copy(customThemeColors = colors)
        prefs.edit().putString("theme", colors).apply()
        saveConfig()
    }

    fun updateCpuViewMode(mode: String) {
        _uiState.value = _uiState.value.copy(cpuViewMode = mode)
        prefs.edit().putString("cpu", mode).apply()
    }

    fun updateGpuViewMode(mode: String) {
        _uiState.value = _uiState.value.copy(gpuViewMode = mode)
        prefs.edit().putString("gpu", mode).apply()
    }

    fun updateRamViewMode(mode: String) {
        _uiState.value = _uiState.value.copy(ramViewMode = mode)
        prefs.edit().putString("ram", mode).apply()
    }

    fun saveConfig() {
        viewModelScope.launch {
            val curr = _uiState.value
            val entity = SettingsEntity(
                username = curr.username, // Siempre usar el usuario activo para no sobreescribir ajeno
                connectionMode = curr.connectionMode,
                ipAddress = curr.ipAddress,
                port = curr.port,
                password = curr.password,
                autoShutdown = curr.autoShutdown,
                activeComputerId = curr.activeComputerId,
                tempUnit = curr.tempUnit,
                cpuTempLimit = curr.cpuTempLimit,
                cpuLoadLimit = curr.cpuLoadLimit,
                gpuTempLimit = curr.gpuTempLimit,
                gpuLoadLimit = curr.gpuLoadLimit,
                apiKey = curr.apiKey,
                customThemeColors = curr.customThemeColors
            )
            repository.saveSettings(entity)
        }
    }

    fun testConnection() {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                _uiState.value = _uiState.value.copy(testConnectionRunStatus = TestStatus.TESTING)
            }
            
            val ip = _uiState.value.ipAddress
            val port = _uiState.value.port.ifEmpty { "8765" }
            val url = "http://$ip:$port/metrics"
            val startTime = System.currentTimeMillis()
            
            try {
                val request = buildRequest(url)
                httpClient.newCall(request).execute().use { response ->
                    val latency = System.currentTimeMillis() - startTime
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            val msg = "${"Conexión exitosa".tr()}: ${"Latencia".tr()} ${latency}ms"
                            _uiState.value = _uiState.value.copy(
                                testConnectionRunStatus = TestStatus.SUCCESS(msg)
                            )
                        } else {
                            val msg = "${"Fallo de conexión".tr()}: código ${response.code}"
                            _uiState.value = _uiState.value.copy(
                                testConnectionRunStatus = TestStatus.FAILURE(msg)
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    val msg = "${"Error de conexión".tr()}: ${getErrorMessage(e)}"
                    _uiState.value = _uiState.value.copy(
                        testConnectionRunStatus = TestStatus.FAILURE(msg)
                    )
                }
            }
            
            delay(6000)
            withContext(Dispatchers.Main) {
                val status = _uiState.value.testConnectionRunStatus
                if (status is TestStatus.SUCCESS || status is TestStatus.FAILURE) {
                    _uiState.value = _uiState.value.copy(testConnectionRunStatus = TestStatus.IDLE)
                }
            }
        }
    }

    private fun formatSpeed(bytesPerSec: Double): String {
        return if (bytesPerSec >= 1024 * 1024) {
            "${String.format("%.1f", bytesPerSec / (1024 * 1024))} MB/s"
        } else if (bytesPerSec >= 1024) {
            "${String.format("%.1f", bytesPerSec / 1024)} KB/s"
        } else {
            "${bytesPerSec.toInt()} B/s"
        }
    }

    fun optimizeAppPerformance() {
        viewModelScope.launch {
            // Simulated optimizing logic: lowers CPU render_engine load momentarily
            val indexToUpdate = _uiState.value.processes.indexOfFirst { it.name == "render_engine.exe" }
            if (indexToUpdate != -1) {
                val updated = _uiState.value.processes.toMutableList()
                val target = updated[indexToUpdate]
                updated[indexToUpdate] = target.copy(cpu = 15.2, status = "Stable")
                _uiState.value = _uiState.value.copy(
                    processes = updated,
                    cpuOverallLoad = (_uiState.value.cpuOverallLoad - 15).coerceAtLeast(10)
                )
            }
        }
    }

    fun startMirroring() {
        if (mirrorWebSocket != null) return
        val ip = _uiState.value.ipAddress
        if (ip.isEmpty() || _uiState.value.activeComputerId == null) return
        val wsPort = _uiState.value.port.toIntOrNull()?.let { it + 1 } ?: 8766
        val wsUrl = "ws://$ip:$wsPort/mirror"
        val key = _uiState.value.apiKey
        val reqBuilder = Request.Builder().url(wsUrl)
        if (key.isNotBlank()) reqBuilder.addHeader("X-API-Key", key)
        val request = reqBuilder.build()

        mirrorWebSocket = wsClient.newWebSocket(request, object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, bytes: okio.ByteString) {
                viewModelScope.launch(Dispatchers.Default) {
                    try {
                        val byteArray = bytes.toByteArray()
                        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                        if (bitmap != null) {
                            val imageBitmap = bitmap.asImageBitmap()
                            withContext(Dispatchers.Main) {
                                _screenBitmap.value = imageBitmap
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                // Connection failed
            }
        })
    }

    fun stopMirroring() {
        mirrorWebSocket?.close(1000, "Deteniendo espejo")
        mirrorWebSocket = null
        _screenBitmap.value = null
    }

    private fun hashPassword(password: String): String {
        return try {
            val digest = java.security.MessageDigest.getInstance("SHA-256")
            val hash = digest.digest(password.toByteArray(Charsets.UTF_8))
            hash.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            password
        }
    }

    fun login(user: String, pass: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val settings = repository.getSettings(user)
            val hashedPass = hashPassword(pass)
            val success = settings.username == user && 
                    (settings.password == hashedPass || settings.password == pass)
            if (success) {
                // Upgrade plaintext password in database to hash if it was plaintext
                if (settings.password == pass) {
                    val upgraded = settings.copy(password = hashedPass)
                    repository.saveSettings(upgraded)
                }
                _uiState.value = _uiState.value.copy(isLoggedIn = true, username = user, password = hashedPass)
                startUserCollection(user)
            }
            withContext(Dispatchers.Main) {
                onResult(success)
            }
        }
    }

    fun logout() {
        // En esta versión el logout se ha deshabilitado para entrar directo al Dashboard.
        // Si necesitasemos volver a implementarlo, se haría aquí.
    }

    fun register(user: String, pass: String) {
        viewModelScope.launch {
            val hashedPass = hashPassword(pass)
            val entity = SettingsEntity(
                username = user,
                password = hashedPass,
                connectionMode = "ADB",
                ipAddress = "",
                port = "8765"
            )
            repository.saveSettings(entity)
            
            _uiState.value = _uiState.value.copy(
                username = user,
                password = hashedPass,
                isLoggedIn = true,
                showTutorial = true
            )
            startUserCollection(user)
        }
    }

    fun dismissTutorial() {
        _uiState.value = _uiState.value.copy(showTutorial = false)
    }

    fun lockPc() {
        dismissLockConfirm()
        viewModelScope.launch(Dispatchers.IO) {
            val ip = _uiState.value.ipAddress
            val port = _uiState.value.port.ifEmpty { "8765" }
            val url = "http://$ip:$port/pc/lock"
            try {
                val body = ByteArray(0).toRequestBody(null)
                val request = buildPostRequest(url, body)
                httpClient.newCall(request).execute().use { response ->
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) showFeedback("🔒 PC bloqueada")
                        else showFeedback("❌ Error al bloquear PC")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { showFeedback("❌ Sin conexión al agente") }
            }
        }
    }

    fun logoutPc() {
        dismissLogoutPcConfirm()
        viewModelScope.launch(Dispatchers.IO) {
            val ip = _uiState.value.ipAddress
            val port = _uiState.value.port.ifEmpty { "8765" }
            val url = "http://$ip:$port/pc/logout"
            try {
                val body = ByteArray(0).toRequestBody(null)
                val request = buildPostRequest(url, body)
                httpClient.newCall(request).execute().use { response ->
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) showFeedback("👋 Sesión cerrada en PC")
                        else showFeedback("❌ Error al cerrar sesión")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { showFeedback("❌ Sin conexión al agente") }
            }
        }
    }

    fun suspendPc() {
        dismissSuspendConfirm()
        viewModelScope.launch(Dispatchers.IO) {
            val ip = _uiState.value.ipAddress
            val port = _uiState.value.port.ifEmpty { "8765" }
            val url = "http://$ip:$port/pc/suspend"
            try {
                val body = ByteArray(0).toRequestBody(null)
                val request = buildPostRequest(url, body)
                httpClient.newCall(request).execute().use { response ->
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) showFeedback("💤 PC suspendida")
                        else showFeedback("❌ Error al suspender PC")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { showFeedback("❌ Sin conexión al agente") }
            }
        }
    }

    fun setShowComputersScreen(show: Boolean) {
        _uiState.value = _uiState.value.copy(showComputersScreen = show)
    }

    fun setShowProcessesScreen(show: Boolean) {
        _uiState.value = _uiState.value.copy(showProcessesScreen = show)
    }

    fun setShowScriptsScreen(show: Boolean) {
        _uiState.value = _uiState.value.copy(showScriptsScreen = show)
    }

    fun addComputer(name: String, ip: String, port: String, user: String, pass: String, apiKey: String = "") {
        viewModelScope.launch {
            if (_uiState.value.computers.size >= 1) {
                showFeedback("❌ ${ "Límite de la versión básica alcanzado (máx. 1 PC)".tr() }")
                return@launch
            }
            val computer = ComputerEntity(
                name = name,
                ipAddress = ip,
                port = port,
                username = user,
                password = pass,
                ownerUsername = _uiState.value.username, // Link to current user
                apiKey = apiKey
            )
            val newId = repository.insertComputer(computer).toInt()
            
            // Auto-select if no computer is currently active
            if (_uiState.value.activeComputerId == null) {
                selectComputer(newId)
            }
        }
    }

    fun selectComputer(id: Int) {
        viewModelScope.launch {
            val computer = repository.getComputerById(id)
            if (computer != null) {
                val currentSettings = repository.getSettings(_uiState.value.username)
                val updatedSettings = currentSettings.copy(
                    ipAddress = computer.ipAddress,
                    port = computer.port,
                    activeComputerId = id,
                    apiKey = computer.apiKey // Import correct API Key for connection!
                )
                repository.saveSettings(updatedSettings)
                
                _uiState.value = _uiState.value.copy(
                    ipAddress = computer.ipAddress,
                    port = computer.port,
                    activeComputerId = id,
                    apiKey = computer.apiKey
                )
                testConnection()
                
                // If mirroring is active, restart it so it hooks to the new computer instantly!
                if (mirrorWebSocket != null) {
                    stopMirroring()
                    startMirroring()
                }
                
                startTelemetryLoop()
            }
        }
    }

    fun deleteComputer(id: Int) {
        viewModelScope.launch {
            val computer = repository.getComputerById(id)
            if (computer != null) {
                repository.deleteComputer(computer)

                if (_uiState.value.activeComputerId == id) {
                    val remaining = _uiState.value.computers.filter { it.id != id }
                    if (remaining.isNotEmpty()) {
                        selectComputer(remaining.first().id)
                    } else {
                        // No computers left: stop all network activity and clear state
                        telemetryJob?.cancel()
                        telemetryJob = null
                        stopMirroring()
                        stopWebSocketStream()

                        val currentSettings = repository.getSettings(_uiState.value.username)
                        repository.saveSettings(
                            currentSettings.copy(
                                activeComputerId = null,
                                ipAddress = "",
                                port = "8765",
                                apiKey = ""
                            )
                        )

                        _uiState.value = _uiState.value.copy(
                            activeComputerId = null,
                            ipAddress = "",
                            port = "8765",
                            isConnected = false,
                            apiKey = ""
                        )
                    }
                }
            }
        }
    }

    fun updateComputer(id: Int, name: String, ip: String, port: String, user: String, pass: String, apiKey: String = "") {
        viewModelScope.launch {
            val computer = ComputerEntity(
                id = id,
                name = name,
                ipAddress = ip,
                port = port,
                username = user,
                password = pass,
                ownerUsername = _uiState.value.username,
                apiKey = apiKey
            )
            repository.updateComputer(computer)
            
            if (_uiState.value.activeComputerId == id) {
                val currentSettings = repository.getSettings(_uiState.value.username)
                val updatedSettings = currentSettings.copy(
                    ipAddress = ip,
                    port = port,
                    apiKey = apiKey
                )
                repository.saveSettings(updatedSettings)
                
                _uiState.value = _uiState.value.copy(
                    ipAddress = ip,
                    port = port,
                    apiKey = apiKey
                )
                startTelemetryLoop()
            }
        }
    }

    private fun getErrorMessage(e: Exception): String {
        val msg = e.message ?: ""
        return when {
            e is java.net.SocketTimeoutException || msg.contains("timeout", ignoreCase = true) -> "Tiempo de espera agotado"
            e is java.net.ConnectException || msg.contains("refused", ignoreCase = true) -> "Conexión rechazada: Servidor inaccesible"
            e is java.net.UnknownHostException -> "Host desconocido / IP inválida"
            msg.contains("HTTP error", ignoreCase = true) -> "Error de servidor: ${msg}"
            else -> "Error de red: ${e.javaClass.simpleName}"
        }
    }

    private fun formatUptime(seconds: Long): String {
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60
        return if (h > 0) {
            "${h}h ${m}m ${s}s"
        } else if (m > 0) {
            "${m}m ${s}s"
        } else {
            "${s}s"
        }
    }
}
