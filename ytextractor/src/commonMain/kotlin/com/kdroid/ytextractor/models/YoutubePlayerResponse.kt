package com.kdroid.ytextractor.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class YoutubePlayerResponse(
    @SerialName("playabilityStatus") val playabilityStatus: PlayabilityStatus? = null,
    @SerialName("streamingData") val streamingData: StreamingData? = null,
    @SerialName("videoDetails") val videoDetails: VideoDetails? = null
)