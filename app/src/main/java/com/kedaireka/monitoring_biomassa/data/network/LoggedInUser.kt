package com.kedaireka.monitoring_biomassa.data.network

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
data class LoggedInUser(
    val token: String,
    val username: String,
    val user_id: String
)