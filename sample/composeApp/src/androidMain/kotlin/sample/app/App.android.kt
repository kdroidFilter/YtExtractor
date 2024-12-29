package sample.app

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import android.net.Uri
import androidx.compose.ui.text.style.TextDecoration

@Composable
actual fun ClickableUrl(url: String?) {
    val context = LocalContext.current
    val formattedUrl = url ?: "Unavailable"

    if (url != null) {
        Text(
            text = "URL: $formattedUrl",
            modifier = Modifier.clickable {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
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
