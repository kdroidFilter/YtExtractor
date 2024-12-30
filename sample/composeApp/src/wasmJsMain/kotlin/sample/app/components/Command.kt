package sample.app.components

import sample.app.utils.OperatingSystem

data class Command(
    val operatingSystem: OperatingSystem,
    val command: String,
)

internal const val WINDOWS_CMD = "\"C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe\" --user-data-dir=\"C:\\chrome-dev-disabled-security\" --disable-web-security  https://kdroidfilter.github.io/YtExtractor/"
internal const val WINDOWS_POWERSHELL = "& \"C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe\" --user-data-dir=\"C:\\chrome-dev-disabled-security\" --disable-web-security  https://kdroidfilter.github.io/YtExtractor/"
internal const val MAC = "open /Applications/Google\\ Chrome.app --args --user-data-dir=\"/var/tmp/chrome-dev-disabled-security\" --disable-web-security https://kdroidfilter.github.io/YtExtractor/"
internal const val LINUX = "google-chrome --user-data-dir=\"~/chrome-dev-disabled-security\" --disable-web-security https://kdroidfilter.github.io/YtExtractor/ & "

internal val commands = listOf(
    Command(OperatingSystem.WINDOWS, WINDOWS_CMD),
    Command(OperatingSystem.WINDOWS, WINDOWS_POWERSHELL),
    Command(OperatingSystem.MAC, MAC),
    Command(OperatingSystem.LINUX, LINUX)
)