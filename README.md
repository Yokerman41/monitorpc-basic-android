# MonitorPC Basic 📱💻

<div align="center">
  <img src="https://ai.google.dev/static/site-assets/images/share-ais-513315318.png" width="100%" alt="MonitorPC Banner" style="border-radius: 8px;" />
</div>

---

**MonitorPC Basic** es una aplicación móvil minimalista e intuitiva para Android que te permite monitorear las métricas de rendimiento y hardware de tu computadora en tiempo real desde tu dispositivo móvil. 

Esta aplicación ha sido diseñada para ser ligera, rápida y proporcionar una visualización de datos fluida e interactiva.

---

## 🚀 Características Principales

*   **⚡ Monitoreo de CPU**: Carga general, velocidad de reloj actual, voltaje, temperatura por núcleo y gráficos de fluctuación histórica en tiempo real.
*   **🎮 Telemetría de GPU**: Visualización del modelo de GPU, carga 3D, temperatura, uso de VRAM (usado vs. total) y estado de los encoders/decoders de video (compatible con sistemas NVIDIA y emulación dinámica).
*   **💾 Memoria RAM**: Uso detallado en Gigabytes con historial gráfico de ocupación.
*   **📶 Conectividad y Red**: Medición de latencia de red, velocidad de subida/bajada actual y estado de la red local.
*   **📁 Almacenamiento y Diagnóstico S.M.A.R.T.**: Estado de vida útil de tus unidades de almacenamiento (SSD, NVMe, HDD), temperatura de los discos, espacio ocupado/libre y datos S.M.A.R.T. detallados.
*   **⚙️ Control de Procesos Activos**: Búsqueda e inspección de procesos del sistema remoto con su respectivo consumo de CPU y memoria.
*   **📜 Ejecución de Scripts**: Lanzamiento de comandos y scripts automatizados de forma remota en tu computadora.
*   **🖥️ Mirroring de Pantalla**: Transmisión rápida de la pantalla de tu PC a la app móvil por WebSocket para un control visual rápido.

---

## 🤝 Ecosistema y Funcionamiento con el Agente

Esta aplicación de Android actúa como la interfaz cliente y **requiere del agente de escritorio** para recibir los datos de hardware. 

El agente es un servidor local seguro escrito en Python que recopila la telemetría del sistema y la expone mediante servicios HTTP y WebSockets locales (con soporte opcional para descubrimiento automático NSD/mDNS).

🔗 **Enlace al repositorio del Agente**:
Puedes descargar, inspeccionar y clonar el agente de escritorio desde su repositorio oficial en GitHub:
👉 **[monitorpc-agent](https://github.com/Yokerman41/monitorpc-agent)**

---

## 🤖 Colaboración con Agente de IA

> [!NOTE]
> Este proyecto, su arquitectura, optimización de hilos y depuración de la conexión mediante WebSockets y REST han sido desarrollados en colaboración directa con **Antigravity**, un agente avanzado de inteligencia artificial diseñado por el equipo de **Google DeepMind**.

---

## 🛠️ Requisitos e Instalación

### Requisitos Previos:
*   [Android Studio](https://developer.android.com/studio) (Bumblebee o superior).
*   Un dispositivo Android físico o emulador.
*   El agente **monitorpc-agent** ejecutándose en tu PC en la misma red local.

### Configuración del Proyecto:

1.  **Clonar e Importar**:
    Abre Android Studio, selecciona **Open** y elige la carpeta de este proyecto.
2.  **Configurar Variables de Entorno**:
    Crea un archivo llamado `.env` en el directorio raíz de este proyecto y define las claves necesarias (ver `.env.example` como referencia):
    ```env
    GEMINI_API_KEY=tu_clave_de_gemini_aqui
    ```
3.  **Ejecutar la App**:
    Conecta tu dispositivo y haz clic en **Run** en Android Studio.

---

## 📡 Establecer Conexión

Una vez instalada la aplicación:
1.  Inicia el agente **monitorpc-agent** en tu computadora.
2.  Abre la aplicación de Android.
3.  Introduce la dirección IP local de tu computadora y el puerto configurado en el agente (por defecto `8765`), o bien utiliza la función de descubrimiento automático mDNS en la pantalla de selección de equipos.
4.  ¡Listo! Comenzarás a recibir métricas de rendimiento en tiempo real.
