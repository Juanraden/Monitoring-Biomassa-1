package com.kedaireka.monitoring_biomassa.data.network

import androidx.annotation.Keep
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class KerambaNetwork(
    val keramba_id: String,
    val nama: String,
    val ukuran: String,
    val tanggal_install: String
)
