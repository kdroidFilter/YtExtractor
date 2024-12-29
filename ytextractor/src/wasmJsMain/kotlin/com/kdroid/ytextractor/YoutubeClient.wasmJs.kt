package com.kdroid.ytextractor

import io.ktor.client.engine.*
import io.ktor.client.engine.js.*

actual val httpClientEngine: HttpClientEngine
    get() = Js.create()