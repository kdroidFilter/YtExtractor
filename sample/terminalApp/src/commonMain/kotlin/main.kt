import com.kdroid.ytextractor.models.VideoInfo
import com.kdroid.ytextractor.YouTubeClient
import kotlinx.coroutines.runBlocking

fun main() {
    val client = YouTubeClient()

    println("YouTube Video Info Extractor")
    print("Enter YouTube URL: ")
    val youtubeUrl = readlnOrNull()

    if (youtubeUrl.isNullOrEmpty()) {
        println("Invalid URL. Please provide a valid YouTube link.")
        return
    }

    runBlocking {
        try {
            val videoInfo: VideoInfo? = client.getVideoFormats(youtubeUrl)
            if (videoInfo == null) {
                println("Unable to retrieve video information. Please check the URL.")
            } else {
                displayVideoInfo(videoInfo)
            }
        } catch (e: Exception) {
            println("Error occurred: ${e.message}")
        }
    }
}

fun displayVideoInfo(videoInfo: VideoInfo) {
    println("\nVideo Info:")
    println("Video ID: ${videoInfo.videoId}")
    println("Title: ${videoInfo.title}")
    println("Author: ${videoInfo.author}")
    println("Duration: ${videoInfo.durationSeconds} sec")
    println("Formats:")

    videoInfo.formats.forEach { format ->
        println("- Itag: ${format.itag}")
        println("  MimeType: ${format.mimeType}")
        println("  QualityLabel: ${format.qualityLabel}")
        println("  URL: ${format.url}")
    }
}
