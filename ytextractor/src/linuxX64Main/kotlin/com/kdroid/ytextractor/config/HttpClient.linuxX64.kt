package com.kdroid.ytextractor.config

import io.ktor.client.*
import io.ktor.client.engine.curl.*


actual fun getHttpClient() = HttpClient(Curl) {

}