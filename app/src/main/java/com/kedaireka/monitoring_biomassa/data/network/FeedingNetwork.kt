package com.kedaireka.monitoring_biomassa.data.network

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FeedingNetwork(
    val feeding_id: String,
    val keramba_id: String,
    val tanggal_feeding: String
)