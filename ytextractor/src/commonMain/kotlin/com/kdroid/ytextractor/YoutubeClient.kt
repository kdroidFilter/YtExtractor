package com.kdroid.ytextractor

import com.kdroid.ytextractor.models.VideoInfo
import com.kdroid.ytextractor.models.YoutubePlayerResponse
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject


expect fun getHttpClient(): HttpClient

/**
 * A client for interacting with YouTube to extract video information and formats.
 *
 * @constructor Creates an instance of YouTubeClient with an optional HTTP client.
 * @param client The HTTP client used to make requests. Defaults to an internal implementation.
 */
class YouTubeClient(
    val client : HttpClient = getHttpClient(),
    val clientType: ClientType = ClientType.ANDROID
) {
    private val json = Json { ignoreUnknownKeys = true }
    /**
     * Retrieve the list of video formats (with URL) from a YouTube URL
     */
    suspend fun getVideoFormats(youtubeUrl: String): VideoInfo? {
        return try {
            // 1) Extract the ID
            val videoId = extractVideoID(youtubeUrl)
                ?: return null  // or generate a message / log, etc.

            // 2) Fetch the response via Innertube
            val playerResponse = getInnertubePlayerResponse(videoId)

            // 3) Check the status
            val status = playerResponse?.playabilityStatus?.status ?: "ERROR"
            if (status != "OK") {
                // Log the error or return null
                return null
            }

            // 4) Retrieve streamingData and videoDetails
            val streamingData = playerResponse?.streamingData ?: return null
            val videoDetails = playerResponse.videoDetails ?: return null

            // 5) Concatenate formats
            val rawFormats = streamingData.formats + streamingData.adaptiveFormats

            // 6) Build the final structure
            val durationSeconds = (videoDetails.lengthSeconds ?: "0").toLongOrNull() ?: 0L
            VideoInfo(
                videoId = videoDetails.videoId ?: videoId,
                title = videoDetails.title ?: "Unknown title",
                author = videoDetails.author ?: "Unknown author",
                durationSeconds = durationSeconds,
                formats = rawFormats
            )
        } catch (e: Exception) {
            // In case of an exception (bad URL, network issue, etc.), return null
            null
        }
    }

    /**
     * Retrieve the playerResponse via the Innertube API (you can also try other endpoints).
     * We send a JSON according to the Innertube protocol (clientName, clientVersion, etc.).
     * We also add headers similar to the Go library, along with the CONSENT cookie.
     */
    suspend fun getInnertubePlayerResponse(videoId: String): YoutubePlayerResponse? {
        try {
            val innertubeKey = "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8"
            val url = "https://www.youtube.com/youtubei/v1/player?key=$innertubeKey"

            val requestBody = buildJsonObject {
                put("videoId", videoId)
                putJsonObject("context") {
                    putJsonObject("client") {
                        put("clientName", clientType.clientName)
                        put("clientVersion", clientType.clientVersion)
                        clientType.androidSdkVersion?.let { put("androidSdkVersion", it) }
                        clientType.deviceModel?.let { put("deviceModel", it) }
                        put("userAgent", clientType.userAgent)
                    }
                }
            }

            val response = client.post(url) {
                header(HttpHeaders.UserAgent, clientType.userAgent)
                header("Origin", "https://youtube.com")
                header("Sec-Fetch-Mode", "navigate")
                header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(requestBody.toString())
            }

            if (!response.status.isSuccess()) {
                throw IllegalStateException("HTTP error: ${response.status.value}")
            }

            val text = response.bodyAsText()
            return json.decodeFromString<YoutubePlayerResponse>(text)
        } catch (e: Exception) {
            return null
        }
    }

    /**
     * Attempt to extract the video ID from a URL such as:
     *   https://www.youtube.com/watch?v=ABCDEFGHIJK
     *   https://youtu.be/ABCDEFGHIJK
     *   etc.
     */
    private fun extractVideoID(url: String): String? {
        // Simplified regular expression to find patterns of 11 characters (YT ID).
        val patterns = listOf(
            Regex("(?:v=|/)([0-9A-Za-z_-]{11})"),
            Regex("([0-9A-Za-z_-]{11})")
        )
        for (pattern in patterns) {
            val match = pattern.find(url) ?: continue
            if (match.groups.size > 1) {
                return match.groupValues[1]
            }
        }
        // If not found, check if it's already a raw 11-char ID
        return if (url.matches(Regex("^[0-9A-Za-z_-]{11}\$"))) url else null
    }

    fun close() {
        client.close()
    }
}
