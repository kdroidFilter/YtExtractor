package sample.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import sample.app.ClickableUrl
import sample.app.components.DisplayCommand
import sample.app.components.WINDOWS_CMD
import sample.app.components.WINDOWS_POWERSHELL
import sample.app.components.commands
import sample.app.utils.OperatingSystem
import sample.app.utils.copyToClipboard
import sample.app.utils.osCheck

@Composable
fun CorsErrorScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .widthIn(max = 600.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "YT Extractor Wasm Demo", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(42.dp))

        Row {
            Text(
                text = "CORS is not configured, execute this command to execute chrome with Cors disabled (click to copy):",
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
            )
        }

        val currentOS = osCheck()
        val osCommands = commands.filter { it.operatingSystem == currentOS }

        osCommands.forEach { command ->
            if (command.operatingSystem == OperatingSystem.WINDOWS) {
                Row {
                    if (command.command == WINDOWS_POWERSHELL) Text(text = "Powershell Command :")
                    if (command.command == WINDOWS_CMD) Text(text = "Cmd Command :")
                }
            }
            Row {
                DisplayCommand(command.command) { copyToClipboard(command.command) }
            }
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(modifier = Modifier.widthIn(max = 600.dp))
        }
    }
}