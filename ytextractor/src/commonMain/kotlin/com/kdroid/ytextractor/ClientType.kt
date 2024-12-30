package com.kdroid.ytextractor

private val webKey = "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8"
private val appKey = "AIzaSyA8eiZmM1FaDVjRy-df2KTyQ_vz_yYM39w"

enum class ClientType(
    val clientName: String,
    val clientVersion: String,
    val userAgent: String,
    val apiKey: String,
    val androidSdkVersion: Int? = null,
    val deviceModel: String? = null
) {
    ANDROID(
        clientName = "ANDROID",
        clientVersion = "18.11.34",
        userAgent = "com.google.android.youtube/18.11.34 (Linux; U; Android 11) gzip",
        apiKey = appKey,
        androidSdkVersion = 30
    ),
    WEB(
        clientName = "WEB",
        clientVersion = "2.20220801.00.00",
        userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36",
        apiKey =  webKey,
    ),
    IOS(
        clientName = "IOS",
        clientVersion = "19.45.4",
        userAgent = "com.google.ios.youtube/19.45.4 (iPhone16,2; U; CPU iOS 18_1_0 like Mac OS X;)",
        deviceModel = "iPhone16,2",
        apiKey = appKey
    ),
    WEB_EMBEDDED(
        clientName = "WEB_EMBEDDED_PLAYER",
        clientVersion = "1.19700101",
        userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36",
        apiKey = webKey
    )
}
