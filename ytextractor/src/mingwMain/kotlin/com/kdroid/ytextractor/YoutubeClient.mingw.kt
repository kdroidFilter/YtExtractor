package com.kdroid.ytextractor

import io.ktor.client.engine.*
import io.ktor.client.engine.winhttp.*

actual val httpClientEngine: HttpClientEngine
    get() = WinHttp.create()