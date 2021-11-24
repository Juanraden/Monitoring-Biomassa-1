package com.kedaireka.monitoring_biomassa.data.network

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class KerambaNetwork(
    val keramba_id: Int,
    val nama: String,
    val ukuran: String,
    val tanggal_install: String
)
