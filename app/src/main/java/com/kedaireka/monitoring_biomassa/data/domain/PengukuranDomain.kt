package com.kedaireka.monitoring_biomassa.data.domain

data class PengukuranDomain(
    val pengukuranid: Int,
    val panjang: Double,
    val bobot: Double,
    val tanggal_ukur: Long,
    val biotaid: Int
)