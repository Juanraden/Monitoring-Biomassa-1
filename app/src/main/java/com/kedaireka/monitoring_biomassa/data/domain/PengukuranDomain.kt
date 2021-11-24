package com.kedaireka.monitoring_biomassa.data.domain

data class PengukuranDomain(
    val pengukuran_id: Int,
    val panjang: Double,
    val bobot: Double,
    val tanggal_ukur: Long,
    val biota_id: Int
)