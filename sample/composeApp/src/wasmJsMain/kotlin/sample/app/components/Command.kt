package sample.app.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.browser.window

@Composable
fun CommandCopyScreen(command : String) {
    Text(
        text = command,
        fontSize = 18.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(bottom = 16.dp)
    )

    Button(onClick = {
        copyToClipboard(command)
    }) {
        Text("")
    }
}

fun copyToClipboard(text: String) {
    val clipboard = window.navigator.clipboard
    clipboard.writeText(text)
}