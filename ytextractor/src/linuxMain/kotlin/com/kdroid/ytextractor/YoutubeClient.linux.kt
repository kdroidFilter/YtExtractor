package com.kdroid.ytextractor

import io.ktor.client.engine.*
import io.ktor.client.engine.curl.*

actual val httpClientEngine: HttpClientEngine
    get() = Curl.create()