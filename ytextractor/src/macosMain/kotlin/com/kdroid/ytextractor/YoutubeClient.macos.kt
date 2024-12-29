package com.kdroid.ytextractor

import io.ktor.client.engine.*
import io.ktor.client.engine.darwin.*

actual val httpClientEngine: HttpClientEngine
    get() = Darwin.create()