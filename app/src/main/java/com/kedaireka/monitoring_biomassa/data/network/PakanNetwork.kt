package com.kedaireka.monitoring_biomassa.data.network

import androidx.annotation.Keep
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class PakanNetwork(
    val pakan_id: String,
    val jenis_pakan: String,
    val deskripsi: String
)
