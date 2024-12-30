# YouTube Extractor for KMP

A Kotlin Multiplatform Library for extracting YouTube video and playlist information.

## 📖 Overview
YouTube Extractor is a library designed for developers who need to retrieve metadata from YouTube videos and playlists. It provides a simple API to extract video formats, playlist details, and other relevant data. The library is available on Maven Central for easy integration.

## ✨ Features
- Extract detailed video information, including available formats, duration, and author.
- Retrieve playlist metadata and video details.
- Multiplatform support for Android, iOS, and other Kotlin-supported platforms.

## ⚙️ Installation
Add the dependency to your `build.gradle.kts` file:

```kotlin
dependencies {
    implementation("io.github.kdroid.ytextractor:<latest_version>")
}
```

Replace `<latest_version>` with the latest version available on [Maven Central](https://search.maven.org/).

## 🚀 Usage

### 🎵 Extract Playlist Information
To extract playlist details, use the `PlaylistExtractor` class:

```kotlin
val client = YoutubePlaylistExtractor()
val playlistInfo = client.getPlaylistInfo("<playlist_url>")

if (playlistInfo != null) {
    println("Playlist ID: ${playlistInfo.id}")
    println("Title: ${playlistInfo.title}")
    println("Author: ${playlistInfo.author}")
    println("Description: ${playlistInfo.description}")
    playlistInfo.videos.forEach { video ->
        println("Video ID: ${video.id}")
        println("Title: ${video.title}")
        println("Duration: ${video.durationSeconds} seconds")
    }
} else {
    println("Unable to retrieve playlist information.")
}
```

### 🎥 Extract Video Information
To extract video details and formats, use the `VideoPlayerExtractor` class:

```kotlin
val client = YoutubeVideoPlayerExtractor()
val videoInfo = client.getVideoFormats("<video_url>")

if (videoInfo != null) {
    println("Video ID: ${videoInfo.videoId}")
    println("Title: ${videoInfo.title}")
    println("Author: ${videoInfo.author}")
    println("Duration: ${videoInfo.durationSeconds} seconds")
    videoInfo.formats.forEach { format ->
        println("Itag: ${format.itag}")
        println("MimeType: ${format.mimeType}")
        println("QualityLabel: ${format.qualityLabel}")
        println("URL: ${format.url}")
    }
} else {
    println("Unable to retrieve video information.")
}
```

### 🖼️ Compose UI Integration
Refer to the example implementations in the `sample.app.screens` package for how to integrate the library with Jetpack Compose to create user interfaces for extracting video and playlist information.

## ⚠️ Disclaimer
This library is intended for educational and non-commercial purposes only.

- **Legal Compliance**: Extracting data from YouTube may violate YouTube's Terms of Service or local copyright laws. It is the user's responsibility to ensure compliance with all applicable laws and regulations in their jurisdiction.

- **No Liability**: The developers and maintainers of this library are not responsible for any misuse or legal consequences arising from the use of this software. Use it at your own risk.

By using this library, you acknowledge and accept these terms.

## 📜 License
This project is licensed under the [MIT License](LICENSE).

---

For more information, refer to the documentation or open an issue on the GitHub repository.
