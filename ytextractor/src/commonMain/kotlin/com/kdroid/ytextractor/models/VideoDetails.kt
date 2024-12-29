package com.kdroid.ytextractor.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VideoDetails(
    @SerialName("videoId") val videoId: String? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("lengthSeconds") val lengthSeconds: String? = null,
    @SerialName("author") val author: String? = null
)