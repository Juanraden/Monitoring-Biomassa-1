package com.kedaireka.monitoring_biomassa.data.network

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class FeedingNetwork(
    @Json(name = "activity_id") val feeding_id: String,
    val keramba_id: String,
    val tanggal_feeding: String
)