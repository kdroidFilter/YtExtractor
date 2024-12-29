package com.kdroid.ytextractor.models

import kotlinx.serialization.Serializable

@Serializable
data class PlayabilityStatus(
    val status: String? = null,
    val reason: String? = null
)