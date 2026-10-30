# Autosugestión FunTaDeHu ✨

> *"Para Ser Eficaces y Felices"*

Aplicación de autosugestión diseñada para ejecutarse en el teléfono tanto como **Web Progresiva (PWA Offline)** como en formato **Nativo Android (APK)**, con notificaciones aleatorias periódicas para reprogramar la mente subconsciente.

---

## 🌟 Características Principales

- **🔔 Notificaciones Aleatorias:** Envío de frases durante el día a intervalos configurables o aleatorios.
- **⏸️ Modo Descanso:** Pausa y desactiva las notificaciones cuando desees desconectarte o dormir.
- **▶️ Reactivar:** Reanuda el ciclo de notificaciones aleatorias al instante.
- **➕ Gestión de Frases:** Agrega nuevas frases personalizadas del libro o elimina las que ya dominas.
- **📶 100% Offline:** Funciona completamente sin internet ni consumo de datos móviles una vez instalada.
- **🔕 Interfaz Limpia:** Sin publicidad ni dependencias externas.

---

## 📱 Dos Formas de Usarla en tu Teléfono

### 1. Aplicación Web / PWA (Recomendada - Multiplataforma)
* **Enlace Oficial:** **[https://jdroger93.github.io/tadehu/](https://jdroger93.github.io/tadehu/)**
* Abre el enlace en tu navegador móvil (Chrome en Android o Safari en iPhone).
* Pulsa **"Instalar en Pantalla de Inicio"** (o *"Agregar al inicio"*).
* Funciona en cualquier teléfono y se actualiza sola.

### 2. Aplicación Nativa Android (APK)
* El archivo instalable listo para usar está en la raíz: [`Autosugestion_FunTaDeHu.apk`](Autosugestion_FunTaDeHu.apk).
* El código fuente en Kotlin y Jetpack Compose está en la carpeta [`/android`](./android).

---

## 📂 Estructura del Repositorio

```text
tadehu/
├── index.html                   # Interfaz PWA publicada por GitHub Pages
├── app.js                       # Lógica de la app, temporizadores y sonido
├── sw.js                        # Service Worker para funcionamiento 100% offline
├── manifest.json                # Manifiesto de instalación PWA
├── logo.png, icon-*.png         # Iconos de la aplicación y notificaciones
├── Autosugestion_FunTaDeHu.apk  # Archivo instalable de Android
└── android/                     # Proyecto nativo en Kotlin y Jetpack Compose
```
