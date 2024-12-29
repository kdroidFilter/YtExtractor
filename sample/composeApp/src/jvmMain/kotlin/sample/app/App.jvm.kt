package sample.app

import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration

@Composable
actual fun ClickableUrl(url: String?) {
    val formattedUrl = url ?: "Unavailable"

    if (url != null) {
        Text(
            text = "URL: $formattedUrl",
            modifier = Modifier.clickable {
                try {
                    val desktop = java.awt.Desktop.getDesktop()
                    if (desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
                        desktop.browse(java.net.URI(url))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            color = Color.Blue,
            style = androidx.compose.ui.text.TextStyle(
                textDecoration = TextDecoration.Underline
            )
        )
    } else {
        Text("URL: Unavailable")
    }
}
