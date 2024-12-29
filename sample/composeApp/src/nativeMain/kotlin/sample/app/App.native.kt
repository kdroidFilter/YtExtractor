package sample.app

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
actual fun ClickableUrl(url: String?) {
    if (url != null) {
        Text( url)
    }
}