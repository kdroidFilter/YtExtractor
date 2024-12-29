package sample.app

import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration

fun openInNewTab(url: String) {
    js("window.open($url, '_blank')")
}

@Composable
actual fun ClickableUrl(url: String?) {
    val formattedUrl = url ?: "Unavailable"

    if (url != null) {
        Text(
            text = "URL: $formattedUrl",
            modifier = Modifier.clickable {
                openInNewTab(url)
            },
            style = TextStyle(
                color = Color.Blue,
                textDecoration = TextDecoration.Underline
            )
        )
    } else {
        Text("URL: Unavailable")
    }
}
