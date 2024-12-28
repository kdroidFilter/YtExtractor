package com.kdroid.ytextractor

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject

class YouTubeClient {

    private val json =  Json { ignoreUnknownKeys = true }

    // ------------------------------------------------------------------
    // Create a Ktor client to perform HTTP calls.
    // ------------------------------------------------------------------
    private val httpClient = HttpClient {
        install(HttpTimeout) {
            requestTimeoutMillis = 60_000
            connectTimeoutMillis = 60_000
            socketTimeoutMillis = 60_000
        }
    }

    /**
     * Retrieve the list of video formats (with URL) from a YouTube URL
     */
    suspend fun getVideoFormats(youtubeUrl: String): VideoInfo {
        // 1) Extract the video ID
        val videoId = extractVideoID(youtubeUrl)
            ?: throw IllegalArgumentException("Unable to extract the video ID from $youtubeUrl")

        // 2) Call the Innertube (youtubei) endpoint to get the player response
        val playerResponse = getInnertubePlayerResponse(videoId)

        // 3) Check the validity of the "playabilityStatus"
        val status = playerResponse.playabilityStatus?.status ?: "ERROR"
        if (status != "OK") {
            val reason = playerResponse.playabilityStatus?.reason ?: "Video not downloadable"
            throw IllegalStateException("Video status: $status -> $reason")
        }

        // 4) Retrieve streamingData + videoDetails
        val streamingData = playerResponse.streamingData
            ?: throw IllegalStateException("No streamingData")
        val videoDetails = playerResponse.videoDetails
            ?: throw IllegalStateException("No videoDetails")

        // 5) Concatenate the list of formats: (formats + adaptiveFormats)
        val rawFormats = streamingData.formats + streamingData.adaptiveFormats

        // 6) Decipher the URL if needed (case of signatureCipher/cipher)
        val finalFormats = rawFormats.map { decipherUrlIfNeeded(it, videoId) }

        // 7) Create our final structure "VideoInfo"
        val durationSeconds = (videoDetails.lengthSeconds ?: "0").toLongOrNull() ?: 0L
        return VideoInfo(
            videoId = videoDetails.videoId ?: videoId,
            title = videoDetails.title ?: "Unknown title",
            author = videoDetails.author ?: "Unknown author",
            durationSeconds = durationSeconds,
            formats = finalFormats
        )
    }

    /**
     * Retrieve the playerResponse via the Innertube API (you can also try other endpoints).
     * We send a JSON according to the Innertube protocol (clientName, clientVersion, etc.).
     * We also add headers similar to the Go library, along with the CONSENT cookie.
     */
    private suspend fun getInnertubePlayerResponse(videoId: String): YoutubePlayerResponse {
        val innertubeKey = "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8"
        val url = "https://www.youtube.com/youtubei/v1/player?key=$innertubeKey"

        val requestBody = buildJsonObject {
            put("videoId", videoId)
            putJsonObject("context") {
                putJsonObject("client") {
                    put("clientName", "ANDROID")
                    put("clientVersion", "18.11.34")
                    put("androidSdkVersion", 30)
                    put("userAgent", "com.google.android.youtube/18.11.34 (Linux; U; Android 11) gzip")
                }
            }
        }

        val response = httpClient.post(url) {
            header(HttpHeaders.UserAgent, "com.google.android.youtube/18.11.34 (Linux; U; Android 11) gzip")
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
    }

    /**
     * Decipher the URL if the "signatureCipher" or "cipher" property is present.
     * Otherwise, return the format as is.
     */
    private fun decipherUrlIfNeeded(fmt: YoutubeFormat, videoId: String): YoutubeFormat {
        val fullCipher = fmt.signatureCipher ?: fmt.cipher ?: return fmt

        // "signatureCipher" is a URL-encoded string, e.g.:
        // "s=...&url=https://...&sp=sig..."
        val decipheredUrl = extractAndDecipherURL(fullCipher, videoId)
        return fmt.copy(url = decipheredUrl)
    }

    /**
     * Extract the URL and "s" (signature) parameters from signatureCipher,
     * then "decipher" them if necessary. Return the final URL.
     */
    private fun extractAndDecipherURL(signatureCipher: String, videoId: String): String {
        // signatureCipher typically looks like:
        // s=<value>&url=<https://...>&sp=sig
        val params = parseQueryString(signatureCipher)

        val url = params["url"]
            ?: throw IllegalStateException("Unable to find the 'url' field in signatureCipher.")

        // "s" may exist, it is the "signature" to manipulate.
        val sValue = params["s"]
        val spValue = params["sp"] ?: "signature"

        // 1) If sValue does not exist, then the signature is probably already in the URL
        if (sValue.isNullOrEmpty()) {
            return url
        }

        // 2) Decipher the "sValue" -> "sigValue"
        val sigValue = applyDecipherAlgorithm(sValue, videoId)

        // 3) Append to the spValue parameter (e.g., "sig" or "signature")
        val finalUrl = buildString {
            append(url)
            if (!url.contains("&")) append('?') else append('&')
            append(spValue)
            append('=')
            append(sigValue)
        }

        // Optional: decode the 'n' parameter for unthrottling (often present in the URL).
        // Cf. "n param" on YouTube. We omit it here for the demo.
        return finalUrl
    }

    /**
     * Very simplified example of possible "deciphering",
     * usually we retrieve a "base.js" script on YouTube, analyze it via Regex, etc.
     * Here, we simulate with 2-3 operations (reverse + slice + swap).
     *
     * The "videoId" is sometimes used as a cache key, etc.
     * In a real case, we must parse the YouTube JS code to know the routine.
     */
    private fun applyDecipherAlgorithm(signature: String, videoId: String): String {
        // Dummy version: reverse the string and swap 1 or 2 characters.
        var arr = signature.toCharArray().toMutableList()

        // Perform just 3-4 basic operations for the demo.
        arr.reverse()                       // example
        arr = arr.drop(2).toMutableList()   // "splice" by removing 2 chars
        swap(arr, 0, arr.size / 2)          // swap
        return arr.joinToString("")
    }

    private fun swap(list: MutableList<Char>, i: Int, j: Int) {
        if (i in list.indices && j in list.indices) {
            val tmp = list[i]
            list[i] = list[j]
            list[j] = tmp
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                     Various utilities
    ////////////////////////////////////////////////////////////////////////////////////////////////////

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

    /**
     * parseQueryString("s=xxx&url=https://...&sp=sig") -> mapOf("s" to "xxx", "url" to "...", "sp" to "sig")
     */
    private fun parseQueryString(query: String): Map<String, String> {
        return query.split("&").associate { part ->
            val idx = part.indexOf("=")
            if (idx < 0) {
                part to ""
            } else {
                val key = part.substring(0, idx)
                val value = part.substring(idx + 1)
                key to urlDecode(value)
            }
        }
    }

    /**
     * Minimal URL decoding
     */
    private fun urlDecode(str: String): String {
        return str.replace("+", " ")
            .replace("%[0-9a-fA-F]{2}".toRegex()) {
                val hex = it.value.substring(1)
                val decimal = hex.toInt(16)
                decimal.toChar().toString()
            }
    }

    fun close() {
        httpClient.close()
    }
}
