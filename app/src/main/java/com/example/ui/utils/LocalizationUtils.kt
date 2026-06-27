package com.example.ui.utils

import java.util.Locale

// Simple translation extension on String
fun String.tr(): String {
    val lang = Locale.getDefault().language
    if (lang == "es") {
        return this
    }
    return translations[this] ?: this
}

// Temperature converter helper
fun formatTemp(celsius: Int, unit: String): String {
    return if (unit == "F") {
        val fahrenheit = Math.round((celsius * 9.0 / 5.0) + 32.0).toInt()
        "$fahrenheit°F"
    } else {
        "$celsius°C"
    }
}

// Translations mapping Spanish source strings to English
val translations = mapOf(
    // Common / Tabs
    "Dashboard" to "Dashboard",
    "Detalle" to "Detail",
    "Storage" to "Storage",
    "Mirror" to "Mirror",
    "Settings" to "Settings",
    "DISPOSITIVO" to "DEVICE",
    "En línea" to "Online",
    "Desconectado" to "Offline",
    
    // Dashboard CPU
    "PROCESADOR (CPU)" to "PROCESSOR (CPU)",
    "Temperatura" to "Temperature",
    "Velocidad" to "Speed",
    "Voltaje" to "Voltage",
    "Carga CPU" to "CPU Load",
    "Carga de CPU en tiempo real" to "Real-time CPU Load",
    "Historial 60s" to "60s History",
    
    // Dashboard GPU
    "TARJETA GRÁFICA (GPU)" to "GRAPHICS CARD (GPU)",
    "GRÁFICOS (GPU)" to "GRAPHICS (GPU)",
    "Uso GPU" to "GPU Usage",
    "Carga de Núcleo" to "Core Load",
    "Memoria VRAM" to "VRAM Memory",
    
    // Dashboard RAM
    "MEMORIA RAM" to "RAM MEMORY",
    "Uso RAM" to "RAM Usage",
    "Total instalado" to "Total installed",
    "EN USO" to "IN USE",
    
    // Dashboard Network
    "RED Y CONECTIVIDAD" to "NETWORK & CONNECTIVITY",
    "CONECTIVIDAD" to "CONNECTIVITY",
    "Latencia" to "Latency",
    "Descarga" to "Download",
    "Subida" to "Upload",
    "Frecuencia" to "Frequency",
    "Señal Fuerte" to "Strong Signal",
    "Señal Débil" to "Weak Signal",
    
    // Dashboard Fans
    "CONFIGURACIÓN DE VENTILADORES" to "FAN CONFIGURATION",
    "Control de Ventiladores" to "Fan Control",
    "Velocidad Actual" to "Current Speed",
    "Encender" to "Turn On",
    "Apagar" to "Turn Off",
    "Automático" to "Automatic",
    
    // Storage Screen
    "ALMACENAMIENTO GLOBAL" to "GLOBAL STORAGE",
    "Espacio Utilizado" to "Used Space",
    "Espacio Disponible" to "Available Space",
    "ESTADO DE LAS UNIDADES" to "DRIVES HEALTH STATUS",
    "Salud" to "Health",
    "Excelente" to "Excellent",
    "Atención" to "Attention",
    "Estable" to "Stable",
    "Métricas SMART Detalladas" to "Detailed SMART Metrics",
    
    // Detail Screen (Cores & Processes)
    "Detalle CPU" to "CPU Detail",
    "Procesos" to "Processes",
    "Detalle de CPU" to "CPU Detail",
    "MONITOR DE RENDIMIENTO" to "PERFORMANCE MONITOR",
    "Carga (Load)" to "Load",
    "Ahora" to "Now",
    "MÉTRICAS (60M)" to "METRICS (60M)",
    "Frecuencia Máx." to "Max Freq.",
    "Promedio" to "Average",
    "Temperatura Máx." to "Max Temp.",
    "Mínimo" to "Minimum",
    "Voltaje CPU" to "CPU Voltage",
    "CURVA DE VENTILADORES" to "FAN CURVE",
    "Monitoreo de Procesos" to "Process Monitoring",
    "TOTAL PROCESOS" to "TOTAL PROCESSES",
    "CARGA CPU" to "CPU LOAD",
    "HILOS ACTIVOS" to "ACTIVE THREADS",
    "Filtrar procesos..." to "Filter processes...",
    "NOMBRE DEL PROCESO" to "PROCESS NAME",
    "MEMORIA" to "MEMORY",
    "Sin procesos concordantes." to "No matching processes.",
    "Mostrando" to "Showing",
    "de" to "of",
    "procesos" to "processes",
    "Anterior" to "Previous",
    "Siguiente" to "Next",
    "Visualizador de Carga" to "Load Visualizer",
    "Topología de procesos en tiempo real" to "Real-time process topology",
    "Alertas de Sistema" to "System Alerts",
    "render_engine.exe está utilizando más del 90% de los recursos de E/S de disco." to "render_engine.exe is using more than 90% of disk I/O resources.",
    "Optimizar Ahora" to "Optimize Now",
    "Monitoreo Detallado de Núcleos" to "Detailed Core Monitoring",
    "ESTADÍSTICAS DEL RELOJ" to "CLOCK STATISTICS",
    "Frecuencia Máxima" to "Maximum Frequency",
    "Frecuencia Promedio" to "Average Frequency",
    "ESTADÍSTICAS TÉRMICAS" to "THERMAL STATISTICS",
    "Temperatura Máxima" to "Maximum Temperature",
    "Temperatura Mínima" to "Minimum Temperature",
    "HISTORIAL DE TEMPERATURA" to "TEMPERATURE HISTORY",
    "Historial de temperatura en tiempo real" to "Real-time temperature history",
    "Uso por Núcleo" to "Usage per Core",
    "Núcleo" to "Core",
    "ADMINISTRADOR DE PROCESOS" to "PROCESS MANAGER",
    "PROCESOS ACTIVOS" to "ACTIVE PROCESSES",
    "Buscar proceso..." to "Search process...",
    "Optimizar Rendimiento" to "Optimize Performance",
    "Motor Invisible" to "Invisible Engine",
    "Análisis de flujo térmico en tiempo real habilitado." to "Real-time thermal flow analysis enabled.",
    
    // Mirror Screen
    "ESPEJO DE PANTALLA" to "SCREEN MIRRORING",
    "TRANSMISIÓN EN TIEMPO REAL" to "REAL-TIME STREAMING",
    "Iniciar Espejo" to "Start Mirror",
    "Detener Espejo" to "Stop Mirror",
    "Conectando..." to "Connecting...",
    "No se puede conectar al agente para transmitir pantalla." to "Cannot connect to the agent for screen streaming.",
    "Asegúrate de que el agente esté ejecutándose." to "Make sure the agent is running.",
    
    // Settings Screen
    "Configuración de Conexión" to "Connection Settings",
    "AJUSTES DE RED DEL MOTOR DE MONITOREO" to "NETWORK SETTINGS OF MONITORING ENGINE",
    "GESTIONAR EQUIPOS / SERVIDORES" to "MANAGE COMPUTERS / SERVERS",
    "Modo de Conexión" to "Connection Mode",
    "Define el canal de comunicación principal" to "Define the primary communication channel",
    "Cableado (ADB)" to "Wired (ADB)",
    "Inalámbrico (WiFi)" to "Wireless (WiFi)",
    "HOST DEL SERVIDOR" to "SERVER HOST",
    "Dirección IP / Hostname" to "IP Address / Hostname",
    "Puerto" to "Port",
    "SEGURIDAD" to "SECURITY",
    "Usuario (Basic Auth)" to "Username (Basic Auth)",
    "Contraseña" to "Password",
    "Auto-shutdown panel" to "Auto-shutdown panel",
    "Apagar pantalla cuando el PC esté bloqueado" to "Turn off display when PC is locked",
    "GUARDAR CONFIGURACIÓN" to "SAVE CONFIGURATION",
    "PROBANDO CONEXIÓN..." to "TESTING CONNECTION...",
    "TEST CONNECTION" to "TEST CONNECTION",
    "CERRAR SESIÓN" to "LOG OUT",
    "Configuración guardada en base de datos" to "Configuration saved to database",
    "Conexión exitosa" to "Successful connection",
    "Fallo de conexión" to "Connection failed",
    
    // Temperature Unit settings
    "Unidad de Temperatura" to "Temperature Unit",
    "Selecciona Celsius o Fahrenheit" to "Select Celsius or Fahrenheit",
    "UMBRALES DE ALERTA" to "ALERT THRESHOLDS",
    "Configura los límites para notificaciones visuales" to "Configure thresholds for visual notifications",
    "Temp. Máxima CPU" to "Max CPU Temp.",
    "Carga Máxima CPU" to "Max CPU Load",
    "Temp. Máxima GPU" to "Max GPU Temp.",
    "Carga Máxima GPU" to "Max GPU Load",
    "LECTURA" to "READ",
    "ESCRITURA" to "WRITE",
    "Uptime" to "Uptime",
    "Alerta" to "Alert",
    
    // Computers List / Switcher
    "MIS COMPUTADORES" to "MY COMPUTERS",
    "Computadores Guardados" to "Saved Computers",
    "Agregar Computador" to "Add Computer",
    "Editar Computador" to "Edit Computer",
    "Nombre del Computador" to "Computer Name",
    "Guardar" to "Save",
    "Cancelar" to "Cancel",
    "Eliminar" to "Delete",
    "Agregar" to "Add",

    // PIN Pairing
    "Vincular con PIN" to "Pair with PIN",
    "Ingresa el PIN de 6 dígitos que aparece en tu PC" to "Enter the 6-digit PIN shown on your PC",
    "VINCULAR" to "PAIR",
    "VINCULANDO..." to "PAIRING...",
    "PIN del Agente" to "Agent PIN",
    "API Key del Agente" to "Agent API Key",
    "Ver en ventana del agente" to "Check Agent window",
    "El PIN debe ser de 6 dígitos" to "PIN must be 6 digits",
    "PIN incorrecto o expirado" to "Incorrect or expired PIN",
    "No se pudo conectar con el agente" to "Could not connect to agent",
    "Dispositivo vinculado con éxito" to "Device paired successfully",
    "⚠️ Sin API Key: el agente rechazará las conexiones" to "⚠️ No API Key: agent will reject connections",
    "Pega aquí la API Key del agente" to "Paste Agent API Key here",
    
    // Login Screen
    "INICIAR SESIÓN" to "LOG IN",
    "REGISTRARSE" to "REGISTER",
    "Bienvenido a monitorPC" to "Welcome to monitorPC",
    "Por favor, inicia sesión para continuar" to "Please log in to continue",
    "Crea una cuenta en la aplicación" to "Create an account in the app",
    "Ingresa tu usuario" to "Enter your username",
    "Ingresa tu contraseña" to "Enter your password",
    "Ingresar" to "Log In",
    "Crear Cuenta" to "Create Account",
    "Credenciales incorrectas" to "Incorrect credentials",
    
    // Tutorial
    "BIENVENIDO A MONITORPC" to "WELCOME TO MONITORPC",
    "Esta guía rápida te ayudará a comenzar:" to "This quick guide will help you get started:",
    "1. Ejecuta el Agente en tu PC." to "1. Run the Agent on your PC.",
    "2. Configura la IP y puerto en Settings." to "2. Configure the IP and port in Settings.",
    "3. ¡Comienza a monitorear en tiempo real!" to "3. Start monitoring in real-time!",
    "Entendido" to "Got it"
)
