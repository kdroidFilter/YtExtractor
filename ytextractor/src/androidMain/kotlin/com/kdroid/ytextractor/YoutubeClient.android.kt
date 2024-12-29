package com.kdroid.ytextractor

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*


actual val httpClientEngine: HttpClientEngine
    get() = CIO.create()