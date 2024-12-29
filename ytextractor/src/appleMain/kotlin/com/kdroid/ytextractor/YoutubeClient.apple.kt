package com.kdroid.ytextractor

import io.ktor.client.*

import io.ktor.client.engine.darwin.*

actual fun getHttpClient(): HttpClient = HttpClient(Darwin) {
    engine {
        configureRequest {
            setAllowsCellularAccess(true)
        }
    }
}