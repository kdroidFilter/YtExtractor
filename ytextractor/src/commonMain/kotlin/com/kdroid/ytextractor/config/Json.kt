package com.kdroid.ytextractor.config

import kotlinx.serialization.json.Json

internal val json = Json {
    ignoreUnknownKeys = true
    isLenient = true
    encodeDefaults = true
}
