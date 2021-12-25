package com.kedaireka.monitoring_biomassa.data.network

import androidx.annotation.Keep
import com.squareup.moshi.JsonClass

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
@Keep
@JsonClass(generateAdapter = true)
data class LoggedInUser(
    val token: String,
    val username: String,
    val user_id: String
)