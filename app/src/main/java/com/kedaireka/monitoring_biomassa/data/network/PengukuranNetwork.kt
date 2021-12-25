package com.kedaireka.monitoring_biomassa.data.network

import androidx.annotation.Keep
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class PengukuranNetwork(
    val pengukuran_id: String,
    val panjang: String,
    val bobot: String,
    val tanggal_ukur: String,
    val biota_id: String,
)
