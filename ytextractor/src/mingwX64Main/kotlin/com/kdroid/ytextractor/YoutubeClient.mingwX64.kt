package com.kdroid.ytextractor

import io.ktor.client.*
import io.ktor.client.engine.winhttp.*


actual fun getHttpClient(): HttpClient = HttpClient(WinHttp) {

}