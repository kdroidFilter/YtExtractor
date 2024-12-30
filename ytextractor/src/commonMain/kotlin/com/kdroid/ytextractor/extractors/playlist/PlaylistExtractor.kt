package com.kdroid.ytextractor.extractors.playlist

import com.kdroid.ytextractor.config.ClientType
import com.kdroid.ytextractor.config.json
import com.kdroid.ytextractor.config.getHttpClient
import com.kdroid.ytextractor.constant.API_BASE_URL
import com.kdroid.ytextractor.utils.buildClientContext
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.json.*

class PlaylistExtractor(
    private val client: HttpClient = getHttpClient(),
    private val clientType: ClientType = ClientType.WEB
) {

    /**
     * Gets all the information of a Youtube playlist, including
     * all the elements (videos) of the playlist, even if it is long.
     * @return a [PlaylistInfo] object or null on error.
     */
    suspend fun getPlaylistInfo(playlistUrl: String): PlaylistInfo? {
        println("[getPlaylistInfo] Start processing URL: $playlistUrl")

        val playlistId = extractPlaylistID(playlistUrl)
        if (playlistId == null) {
            println("[getPlaylistInfo] Invalid playlist URL or ID: $playlistUrl")
            return null
        }
        println("[getPlaylistInfo] Extracted Playlist ID: $playlistId")

        val allVideos = mutableListOf<PlaylistEntry>()
        var playlistTitle: String? = null
        var playlistDescription: String? = null
        var playlistAuthor: String? = null

        var continuationToken: String? = null
        do {
            // Build the JSON query for Innertube
            val context = buildJsonObject {
                put("client", buildClientContext(clientType))
            }


            val requestBody = buildJsonObject {
                if (continuationToken.isNullOrEmpty()) {
                    put("browseId", "VL$playlistId")
                } else {
                    // In case of pagination, we use `continuation`
                    put("continuation", continuationToken)
                }
                put("context", context)
            }


            val url = "$API_BASE_URL/browse?key=${clientType.apiKey}"
            println("[getPlaylistInfo] Sending request to YouTube API: $url, continuation=$continuationToken")

            val response = client.post(url) {
                setBody(requestBody.toString())
                header("Content-Type", "application/json")
                header("User-Agent", clientType.userAgent)
            }

            val responseBody = response.bodyAsText()
            println("[getPlaylistInfo] Response: $responseBody")

            // We parse this JSON "page" and possibly retrieve the following continuation token
            val parseResult = parsePlaylistPage(responseBody, playlistId)
            if (parseResult == null) {
                // Error or unrecognized structure: stop
                break
            } else {
                // Retrieving / updating information
                if (playlistTitle == null) {
                    playlistTitle = parseResult.title
                }
                if (playlistDescription == null) {
                    playlistDescription = parseResult.description
                }
                if (playlistAuthor == null) {
                    playlistAuthor = parseResult.author
                }
                // We add the videos found on this page
                allVideos.addAll(parseResult.videos)

                // Next token (if the playlist has multiple pages)
                continuationToken = parseResult.nextContinuation
            }
        } while (!continuationToken.isNullOrEmpty())

        if (allVideos.isEmpty() && playlistTitle == null) {
            println("[getPlaylistInfo] No videos or titles found. Abandoning.")
            return null
        }

        // We return the final playlist
        return PlaylistInfo(
            id = playlistId,
            title = playlistTitle ?: "Titre inconnu",
            description = playlistDescription,
            author = playlistAuthor,
            videos = allVideos
        )
    }




    /**
     * Parses a page of results returned by Innertube.
     * @return a [PlaylistPartialResult] object containing the title, description,
     * author, list of parsed videos, and continuation token if it exists.
     */
    private fun parsePlaylistPage(responseBody: String, playlistId: String): PlaylistPartialResult? {
        println("[parsePlaylistPage] Parsing response page...")

        val jsonResponse = try {
            json.parseToJsonElement(responseBody).jsonObject
        } catch (e: Exception) {
            println("[parsePlaylistPage] JSON parsing error: ${e.message}")
            return null
        }

        // Error handling
        val error = jsonResponse["error"]?.jsonObject
        if (error != null) {
            val errorMessage = error["message"]?.jsonPrimitive?.content
            println("[parsePlaylistPage] Error in response: $errorMessage")
            return null
        }

        // We possibly check if there is an error alert
        val alerts = jsonResponse["alerts"]?.jsonArray
        if (!alerts.isNullOrEmpty()) {
            val alertRenderer = alerts[0].jsonObject["alertRenderer"]?.jsonObject
            if (alertRenderer != null) {
                val alertType = alertRenderer["type"]?.jsonPrimitive?.content
                if (alertType == "ERROR") {
                    val message = alertRenderer["text"]
                        ?.jsonObject?.get("runs")
                        ?.jsonArray?.getOrNull(0)
                        ?.jsonObject?.get("text")
                        ?.jsonPrimitive?.content
                    println("[parsePlaylistPage] API returns error: $message")
                    return null
                }
            }
        }

        // Retrieving title/description/author (first time only)
        val metadata = jsonResponse["metadata"]?.jsonObject
            ?: jsonResponse["header"]?.jsonObject

        var playlistTitle: String? = null
        var playlistDescription: String? = null
        var playlistAuthor: String? = null

        if (metadata != null) {
            val renderer = metadata["playlistMetadataRenderer"]?.jsonObject
                ?: metadata["playlistHeaderRenderer"]?.jsonObject

            playlistTitle = renderer?.get("title")?.jsonPrimitive?.contentOrNull
            playlistDescription = renderer?.get("description")?.jsonPrimitive?.contentOrNull

            // We also try to retrieve the author via the sidebar
            playlistAuthor = jsonResponse["sidebar"]
                ?.jsonObject?.get("playlistSidebarRenderer")
                ?.jsonObject?.get("items")
                ?.jsonArray?.getOrNull(1)
                ?.jsonObject?.get("playlistSidebarSecondaryInfoRenderer")
                ?.jsonObject?.get("videoOwner")
                ?.jsonObject?.get("videoOwnerRenderer")
                ?.jsonObject?.get("title")
                ?.jsonObject?.get("runs")
                ?.jsonArray?.getOrNull(0)
                ?.jsonObject?.get("text")
                ?.jsonPrimitive?.contentOrNull
        }

        // Retrieving the main block containing the videos
        // → first option: Web-type structure
        val contents = jsonResponse["contents"]
            ?.jsonObject?.get("twoColumnBrowseResultsRenderer")
            ?.jsonObject?.get("tabs")
            ?.jsonArray?.getOrNull(0)
            ?.jsonObject?.get("tabRenderer")
            ?.jsonObject?.get("content")
            ?.jsonObject?.get("sectionListRenderer")
            ?.jsonObject?.get("contents")
            ?.jsonArray?.getOrNull(0)
            ?.jsonObject?.get("itemSectionRenderer")
            ?.jsonObject?.get("contents")
            ?.jsonArray?.getOrNull(0)
            ?.jsonObject?.get("playlistVideoListRenderer")
            ?.jsonObject?.get("contents")

        // In some cases, if the playlist is long and we are on "next" pages,
        // we can retrieve the JSON at the root "onResponseReceivedActions" or "continuationContents".
        val continuationItems = jsonResponse["onResponseReceivedActions"]
            ?.jsonArray?.getOrNull(0)
            ?.jsonObject?.get("appendContinuationItemsAction")
            ?.jsonObject?.get("continuationItems")
            ?: jsonResponse["continuationContents"]
                ?.jsonObject?.get("playlistVideoListContinuation")
                ?.jsonObject?.get("contents")

        // We try to extract the videos either in `contents` or in `continuationItems`
        val videoItems = contents ?: continuationItems
        if (videoItems == null || videoItems.jsonArray.isEmpty()) {
            println("[parsePlaylistPage] No videos found in expected structure.")
            return PlaylistPartialResult(
                title = playlistTitle,
                description = playlistDescription,
                author = playlistAuthor,
                videos = emptyList(),
                nextContinuation = null
            )
        }

        val videos = mutableListOf<PlaylistEntry>()
        var nextContinuation: String? = null

        videoItems.jsonArray.forEach { item ->
            val itemObj = item.jsonObject

            // Check if it is a "Video Renderer playlist" item
            val videoRenderer = itemObj["playlistVideoRenderer"]?.jsonObject
            if (videoRenderer != null) {
                val videoId = videoRenderer["videoId"]?.jsonPrimitive?.contentOrNull
                val videoTitle = videoRenderer["title"]?.jsonObject
                    ?.get("runs")?.jsonArray?.getOrNull(0)?.jsonObject
                    ?.get("text")?.jsonPrimitive?.contentOrNull
                val videoAuthor = videoRenderer["shortBylineText"]?.jsonObject
                    ?.get("runs")?.jsonArray?.getOrNull(0)?.jsonObject
                    ?.get("text")?.jsonPrimitive?.contentOrNull
                val videoDuration = videoRenderer["lengthSeconds"]?.jsonPrimitive?.longOrNull

                val thumbnails = videoRenderer["thumbnail"]?.jsonObject
                    ?.get("thumbnails")?.jsonArray?.mapNotNull {
                        val thumbUrl = it.jsonObject["url"]?.jsonPrimitive?.contentOrNull
                        val width = it.jsonObject["width"]?.jsonPrimitive?.intOrNull
                        val height = it.jsonObject["height"]?.jsonPrimitive?.intOrNull
                        if (thumbUrl != null && width != null && height != null) {
                            Thumbnail(thumbUrl, width, height)
                        } else null
                    } ?: emptyList()

                if (!videoId.isNullOrEmpty() && !videoTitle.isNullOrEmpty()) {
                    videos.add(
                        PlaylistEntry(
                            id = videoId,
                            title = videoTitle,
                            author = videoAuthor,
                            durationSeconds = videoDuration,
                            thumbnails = thumbnails
                        )
                    )
                    println("[parsePlaylistPage] + Video: $videoTitle ($videoId)")
                }
            }

            // Otherwise it may be a "continuationItemRenderer" item
            val continuationRenderer = itemObj["continuationItemRenderer"]?.jsonObject
            if (continuationRenderer != null) {
                val token = continuationRenderer["continuationEndpoint"]?.jsonObject
                    ?.get("continuationCommand")?.jsonObject
                    ?.get("token")?.jsonPrimitive?.contentOrNull

                if (!token.isNullOrEmpty()) {
                    nextContinuation = token
                    println("[parsePlaylistPage] Found next continuation token: $token")
                }
            }
        }

        println("[parsePlaylistPage] ${videos.size} extracted videos. nextContinuation=$nextContinuation")

        return PlaylistPartialResult(
            title = playlistTitle,
            description = playlistDescription,
            author = playlistAuthor,
            videos = videos,
            nextContinuation = nextContinuation
        )
    }

    /**
     * Extracts the playlist ID from the URL (or raw string).
     * Compatible with URLs like:
     * - https://www.youtube.com/playlist?list=PL...
     * - &list=...
     * - ...
     */
    private fun extractPlaylistID(url: String): String? {
        println("[extractPlaylistID] Extracting playlist ID from URL: $url")
        val regex = Regex("[&?]list=([A-Za-z0-9_-]{13,42})")
        val match = regex.find(url)?.groups?.get(1)?.value
        println("[extractPlaylistID] Extracted ID: $match")
        return match
    }
}



