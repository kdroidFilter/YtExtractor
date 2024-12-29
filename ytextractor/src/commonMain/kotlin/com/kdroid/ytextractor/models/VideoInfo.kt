package com.kdroid.ytextractor.models

// Simplified representation of video data
data class VideoInfo(
    val videoId: String,
    val title: String,
    val author: String,
    val durationSeconds: Long,
    val formats: List<YoutubeFormat> // These are the direct formats, URLs potentially already "decrypted"
)



