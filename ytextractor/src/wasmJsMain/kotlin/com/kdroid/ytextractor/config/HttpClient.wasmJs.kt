package com.kdroid.ytextractor.config

import io.ktor.client.*
import io.ktor.client.engine.js.*

actual fun getHttpClient() = HttpClient(Js) {
}
