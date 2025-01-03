import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.kdroid.ytextractor.extractors.uniquevideo.YoutubeVideoPlayerExtractor
import kotlinx.browser.document
import sample.app.App
import sample.app.screens.CorsErrorScreen
import sample.app.screens.LoadingScreen

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val body = document.body ?: return
    ComposeViewport(body) {
        LoadAppContent()
    }
}


external fun appLoaded()

@Composable
private fun LoadAppContent() {
    val isCorsConfigured = remember { mutableStateOf(false) }
    val isLoading = remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        isCorsConfigured.value = try {
            YoutubeVideoPlayerExtractor().getInnertubePlayerResponse("1oYBnj0cQVk")?.let {
                true
            } ?: false
        } catch (e: Throwable) {
            false
        } finally {
            isLoading.value = false
            appLoaded()
        }
    }

    if (isLoading.value) {
        LoadingScreen()
    } else if (isCorsConfigured.value) {
        App()
    } else {
        CorsErrorScreen()
    }
}




