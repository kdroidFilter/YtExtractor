import com.kdroid.ytextractor.YouTubeClient


suspend fun main() {
    val client = YouTubeClient()

    val testUrl = "https://www.youtube.com/watch?v=C4CzVdlkqHU"

    try {
        val info = client.getVideoFormats(testUrl)
        println("VideoID: ${info.videoId}")
        println("Title:   ${info.title}")
        println("Author:  ${info.author}")
        println("Duration (sec): ${info.durationSeconds}")

        // Parcourir les formats
        info.formats.forEachIndexed { index, format ->
            println("Format $index:")
            println("  itag:         ${format.itag}")
            println("  mimeType:     ${format.mimeType}")
            println("  qualityLabel: ${format.qualityLabel}")
            println("  final URL:    ${format.url ?: "???"}")
        }

    } catch (e: Exception) {
        println("Erreur: ${e.message}")
    } finally {
        client.close()
    }
}