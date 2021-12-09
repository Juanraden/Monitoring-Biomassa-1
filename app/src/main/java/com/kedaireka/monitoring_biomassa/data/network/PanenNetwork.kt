package com.kedaireka.monitoring_biomassa.data.network

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PanenNetwork(
    val activity_id: String,
    val tanggal_panen: String,
    val panjang: String,
    val bobot: String,
    val jumlah_hidup: String,
    val jumlah_mati: String,
    val biota_id: String,
    val keramba_id: String
)