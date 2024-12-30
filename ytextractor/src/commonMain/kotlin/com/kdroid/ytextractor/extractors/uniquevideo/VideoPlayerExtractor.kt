package com.kdroid.ytextractor.extractors.uniquevideo

import com.kdroid.ytextractor.config.ClientType
import com.kdroid.ytextractor.config.json
import com.kdroid.ytextractor.config.getHttpClient
import com.kdroid.ytextractor.constant.API_BASE_URL
import com.kdroid.ytextractor.constant.YOUTUBE_BASE_URL
import com.kdroid.ytextractor.utils.buildClientContext
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject

/**
 * A client for interacting with YouTube to extract video information and
 * formats.
 *
 * @param client The HTTP client used to make requests. Defaults to an
 *    internal implementation.
 * @constructor Creates an instance of YouTubeClient with an optional HTTP
 *    client.
 */
class VideoPlayerExtractor(
    val client: HttpClient = getHttpClient(),
    private val clientType: ClientType = ClientType.IOS
) {
    /** Retrieve the list of video formats (with URL) from a YouTube URL */
    suspend fun getVideoFormats(youtubeUrl: String): VideoInfo? {
        println("[getVideoFormats] Start processing URL: $youtubeUrl")
        return try {
            // 1) Extract the ID
            val videoId = extractVideoID(youtubeUrl)
            println("[getVideoFormats] Extracted video ID: $videoId")  // or generate a message / log, etc.

            // 2) Fetch the response via Innertube
            val playerResponse = videoId?.let { getInnertubePlayerResponse(it) }
            println("[getVideoFormats] Retrieved player response: $playerResponse")

            // 3) Check the status
            val status = playerResponse?.playabilityStatus?.status ?: "ERROR"
            println("[getVideoFormats] Playability status: $status")
            if (status != "OK") {
                // Log the error or return null
                println("[getVideoFormats] Invalid playability status: $status")
                return null
            }

            // 4) Retrieve streamingData and videoDetails
            val streamingData = playerResponse?.streamingData ?: return null
            val videoDetails = playerResponse.videoDetails ?: return null
            println("[getVideoFormats] Retrieved streaming data and video details.")

            // 5) Concatenate formats
            val rawFormats = streamingData.formats + streamingData.adaptiveFormats
            println("[getVideoFormats] Concatenated formats: ${rawFormats.size} formats found.")

            // 6) Build the final structure
            val durationSeconds = (videoDetails.lengthSeconds ?: "0").toLongOrNull() ?: 0L
            println("[getVideoFormats] Video duration: $durationSeconds seconds.")

            (videoDetails.videoId ?: videoId).let {
                VideoInfo(
                    videoId = it,
                    title = videoDetails.title ?: "Unknown title",
                    author = videoDetails.author ?: "Unknown author",
                    durationSeconds = durationSeconds,
                    formats = rawFormats
                ).also { println("[getVideoFormats] Built VideoInfo object: $it") }
            }
        } catch (e: Exception) {
            println("[getVideoFormats] Exception occurred: ${e.message}")
            null
        }
    }


    /**
     * Retrieve the playerResponse via the Innertube API (you can also try
     * other endpoints). We send a JSON according to the Innertube protocol
     * (clientName, clientVersion, etc.). We also add headers similar to the Go
     * library, along with the CONSENT cookie.
     */
    suspend fun getInnertubePlayerResponse(videoId: String): YoutubePlayerResponse? {
        println("[getInnertubePlayerResponse] Start fetching player response for video ID: $videoId")
        try {
            val innertubeKey = clientType.apiKey
            val url = "${API_BASE_URL}/player?key=$innertubeKey"

            val requestBody = buildJsonObject {
                put("videoId", videoId)
                putJsonObject("context") {
                    putJsonObject("client") {
                        buildClientContext(clientType).forEach { (key, value) ->
                            put(key, value)
                        }
                    }
                }
            }

            println("[getInnertubePlayerResponse] Request payload: $requestBody")

            val response = client.post(url) {
                header(HttpHeaders.UserAgent, clientType.userAgent)
                header("Origin", YOUTUBE_BASE_URL)
                header("Sec-Fetch-Mode", "navigate")
                header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(requestBody.toString())
            }

            val responseBody = try {
                response.bodyAsText()
            } catch (e: Exception) {
                println("[getInnertubePlayerResponse] Content-Length mismatch or other error: ${e.message}")
                null
            }

            if (responseBody == null) {
                println("[getInnertubePlayerResponse] Failed to read response body.")
                return null
            }

            println("[getInnertubePlayerResponse] HTTP Response: $responseBody")

            if (!response.status.isSuccess()) {
                println("[getInnertubePlayerResponse] HTTP error: ${response.status.value}")
                throw IllegalStateException("HTTP error: ${response.status.value}")
            }

            return json.decodeFromString<YoutubePlayerResponse>(responseBody).also {
                println("[getInnertubePlayerResponse] Decoded response: $it")
            }
        } catch (e: Exception) {
            println("[getInnertubePlayerResponse] Exception occurred: ${e.message}")
            return null
        }
    }

    /**
     * Attempt to extract the video ID from a URL such as:
     * https://www.youtube.com/watch?v=ABCDEFGHIJK https://youtu.be/ABCDEFGHIJK
     * etc.
     */
    private fun extractVideoID(url: String): String? {
        println("[extractVideoID] Extracting video ID from URL: $url")
        val patterns = listOf(
            Regex("(?:v=|/)([0-9A-Za-z_-]{11})"),
            Regex("([0-9A-Za-z_-]{11})")
        )
        for (pattern in patterns) {
            val match = pattern.find(url) ?: continue
            if (match.groups.size > 1) {
                println("[extractVideoID] Match found: ${match.groupValues[1]}")
                return match.groupValues[1]
            }
        }
        println("[extractVideoID] No match found. Checking raw ID pattern.")
        return if (url.matches(Regex("^[0-9A-Za-z_-]{11}\$"))) url else null
    }

    fun close() {
        println("[close] Closing HTTP client.")
        client.close()
    }
}
