package com.kdroid.ytextractor.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlayabilityStatus(
    val status: String? = null,
    val reason: String? = null
)

@Serializable
data class StreamingData(
    val formats: List<YoutubeFormat> = emptyList(),
    @SerialName("adaptiveFormats") val adaptiveFormats: List<YoutubeFormat> = emptyList()
)

@Serializable
data class VideoDetails(
    @SerialName("videoId") val videoId: String? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("lengthSeconds") val lengthSeconds: String? = null,
    @SerialName("author") val author: String? = null
)

@Serializable
data class VideoInfo(
    @SerialName("videoId") val videoId: String,
    @SerialName("title") val title: String,
    @SerialName("author") val author: String,
    @SerialName("durationSeconds") val durationSeconds: Long,
    @SerialName("formats") val formats: List<YoutubeFormat> // These are the direct formats, URLs potentially already "decrypted"
)

@Serializable
data class YoutubeFormat(
    @SerialName("itag") val itag: Int,
    @SerialName("url") val url: String? = null,
    @SerialName("mimeType") val mimeType: String? = null,
    @SerialName("qualityLabel") val qualityLabel: String? = null,
    @SerialName("bitrate") val bitrate: Long? = null,
    @SerialName("width") val width: Int? = null,
    @SerialName("height") val height: Int? = null,
    @SerialName("signatureCipher") val signatureCipher: String? = null,
    @SerialName("cipher") val cipher: String? = null
)

@Serializable
data class YoutubePlayerResponse(
    @SerialName("playabilityStatus") val playabilityStatus: PlayabilityStatus? = null,
    @SerialName("streamingData") val streamingData: StreamingData? = null,
    @SerialName("videoDetails") val videoDetails: VideoDetails? = null
)
