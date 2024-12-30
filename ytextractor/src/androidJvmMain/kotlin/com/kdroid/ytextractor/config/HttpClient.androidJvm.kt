package com.kdroid.ytextractor.config

import io.ktor.client.*
import io.ktor.client.engine.cio.*

actual fun getHttpClient(): HttpClient = HttpClient(CIO) {
    engine {
//        proxy = ProxyBuilder.http("http://1.1.1.1")
    }
}