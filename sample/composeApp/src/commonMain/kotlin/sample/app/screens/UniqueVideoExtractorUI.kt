package sample.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kdroid.ytextractor.extractors.uniquevideo.VideoPlayerExtractor
import com.kdroid.ytextractor.extractors.uniquevideo.VideoInfo
import kotlinx.coroutines.launch
import sample.app.ClickableUrl

@Composable
fun UniqueVideoExtractorUI() {
    var youtubeUrl by remember { mutableStateOf("") }
    var videoInfo by remember { mutableStateOf<VideoInfo?>(null) }
    var errorMessage by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val client = remember { VideoPlayerExtractor() }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .widthIn(max = 800.dp)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "YouTube Video Info Extractor",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        OutlinedTextField(
            value = youtubeUrl,
            onValueChange = { youtubeUrl = it },
            label = { Text("Enter YouTube URL") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                coroutineScope.launch {
                    errorMessage = ""
                    val info = client.getVideoFormats(youtubeUrl)
                    if (info == null) {
                        errorMessage = "Unable to retrieve video information."
                        videoInfo = null
                    } else {
                        videoInfo = info
                    }
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Get Video Info")
        }

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        videoInfo?.let { info ->
            Text(
                text = "Video Info:",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text("Video ID: ${info.videoId}")
            Text("Title: ${info.title}")
            Text("Author: ${info.author}")
            Text("Duration: ${info.durationSeconds} sec")
            Text("Formats:")

            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                info.formats.forEachIndexed { index, format ->
                    item {
                        Text("Itag: ${format.itag}")
                    }
                    item {
                        Text("MimeType: ${format.mimeType}")
                    }
                    item {
                        Text("QualityLabel: ${format.qualityLabel}")
                    }
                    item {
                        ClickableUrl(format.url)
                    }
                }
            }
        }
    }
}