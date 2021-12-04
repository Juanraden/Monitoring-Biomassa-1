package com.kedaireka.monitoring_biomassa.data.network

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FeedingDetailNetwork(
    val feeding_id: Int,
    val ukuran_tebar: Double,
    val jam_feeding: Long,
    val pakan_id: Int,
)
