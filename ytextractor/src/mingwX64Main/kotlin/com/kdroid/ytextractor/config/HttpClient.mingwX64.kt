package com.kdroid.ytextractor.config

import io.ktor.client.*
import io.ktor.client.engine.winhttp.*


actual fun getHttpClient(): HttpClient = HttpClient(WinHttp) {

}