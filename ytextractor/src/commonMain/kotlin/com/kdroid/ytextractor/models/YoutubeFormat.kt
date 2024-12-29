package com.kdroid.ytextractor.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// YouTube format: contains the direct URL if it exists
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