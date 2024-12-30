package com.kdroid.ytextractor.extractors.playlist

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents the partial result after parsing a playlist page:
 * - Title, description, author (if not null, otherwise it means we didn't find them or we're on a continuation page)
 * - The list of videos
 * - The continuation token, if there is one, to load the rest
 */
internal data class PlaylistPartialResult(
    val title: String?,
    val description: String?,
    val author: String?,
    val videos: List<PlaylistEntry>,
    val nextContinuation: String?
)

@Serializable
data class PlaylistInfo(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String? = null,
    @SerialName("author") val author: String? = null,
    @SerialName("videos") val videos: List<PlaylistEntry> = emptyList()
)

@Serializable
data class PlaylistEntry(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String,
    @SerialName("author") val author: String? = null,
    @SerialName("durationSeconds") val durationSeconds: Long? = null,
    @SerialName("thumbnails") val thumbnails: List<Thumbnail> = emptyList()
)

@Serializable
data class Thumbnail(
    @SerialName("url") val url: String,
    @SerialName("width") val width: Int,
    @SerialName("height") val height: Int
)