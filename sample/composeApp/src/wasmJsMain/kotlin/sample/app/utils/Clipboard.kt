package sample.app.utils

import kotlinx.browser.window

fun copyToClipboard(text: String) {
    val clipboard = window.navigator.clipboard
    clipboard.writeText(text)
}