package sample.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kdroid.ytextractor.extractors.playlist.YoutubePlaylistExtractor
import com.kdroid.ytextractor.extractors.playlist.PlaylistInfo
import kotlinx.coroutines.launch

@Composable
fun PlaylistExtractorUI() {
    var playlistUrl by remember { mutableStateOf("") }
    var playlistInfo by remember { mutableStateOf<PlaylistInfo?>(null) }
    var errorMessage by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val client = remember { YoutubePlaylistExtractor() }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .widthIn(max = 800.dp)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "YouTube Playlist Info Extractor",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        OutlinedTextField(
            value = playlistUrl,
            onValueChange = { playlistUrl = it },
            label = { Text("Enter Playlist URL") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                coroutineScope.launch {
                    errorMessage = ""
                    val info = client.getPlaylistInfo(playlistUrl)
                    if (info == null) {
                        errorMessage = "Unable to retrieve playlist information."
                        playlistInfo = null
                    } else {
                        playlistInfo = info
                    }
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Get Playlist Info")
        }

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        playlistInfo?.let { info ->
            Text(
                text = "Playlist Info:",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text("Playlist ID: ${info.id}")
            Text("Title: ${info.title}")
            Text("Author: ${info.author}")
            Text("Description: ${info.description}")
            Text("Videos:")

            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(info.videos) { video ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Text("Video ID: ${video.id}")
                        Text("Title: ${video.title}")
                        Text("Author: ${video.author}")
                        Text("Duration: ${video.durationSeconds ?: 0} sec")
                        if (video.thumbnails.isNotEmpty()) {
                            Text("Thumbnails:")
                            video.thumbnails.forEach { thumbnail ->
                                Text("- ${thumbnail.url} (${thumbnail.width}x${thumbnail.height})")
                            }
                        }
                    }
                }
            }
        }
    }
}
