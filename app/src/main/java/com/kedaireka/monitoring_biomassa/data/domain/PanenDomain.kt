package com.kedaireka.monitoring_biomassa.data.domain

data class PanenDomain(
    val activity_id: Int,
    val tanggal_panen: Long,
    val panjang: Double,
    val bobot: Double,
    val jumlah_hidup: Int,
    val jumlah_mati: Int,
    val biota_id: Int,
    val keramba_id: Int
)
