package sample.app.utils

import kotlinx.browser.window

enum class OperatingSystem {
    WINDOWS,
    MAC,
    LINUX,
    UNKNOWN
}

fun osCheck(): OperatingSystem {
    val platform = window.navigator.platform.lowercase()
    return when {
        platform.contains("win") -> OperatingSystem.WINDOWS
        platform.contains("mac") -> OperatingSystem.MAC
        platform.contains("linux") -> OperatingSystem.LINUX
        else -> OperatingSystem.UNKNOWN
    }
}


