package sample.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import sample.app.ClickableUrl

@Composable
fun CorsErrorScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .widthIn(max = 600.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row {
            Text("CORS is not configured, fill the instruction in ")
            ClickableUrl("https://mitmachim.top/topic/78541/")

        }

    }
}