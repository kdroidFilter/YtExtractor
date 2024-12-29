package com.kdroid.ytextractor.models

import kotlinx.serialization.Serializable

@Serializable
data class StreamingData(
    val formats: List<YoutubeFormat> = emptyList(),
    val adaptiveFormats: List<YoutubeFormat> = emptyList()
)