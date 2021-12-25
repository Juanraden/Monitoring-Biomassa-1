package com.kedaireka.monitoring_biomassa.data.network

import androidx.annotation.Keep
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class FeedingDetailNetwork(
    val feeding_id: String,
    val ukuran_tebar: String,
    val jam_feeding: String,
    val pakan_id: String,
)
