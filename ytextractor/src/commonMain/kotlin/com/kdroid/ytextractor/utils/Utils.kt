package com.kdroid.ytextractor.utils

import com.kdroid.ytextractor.config.ClientType
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

fun buildClientContext(clientType: ClientType): JsonObject {
    return buildJsonObject {
        put("clientName", clientType.clientName)
        put("clientVersion", clientType.clientVersion)
        clientType.androidSdkVersion?.let { put("androidSdkVersion", it) }
        clientType.deviceModel?.let { put("deviceModel", it) }
        clientType.userAgent.let { put("userAgent", it) }
    }
}