package com.kedaireka.monitoring_biomassa.data.domain

data class FeedingDetailDomain(
    val feeding_id: Int,
    val pakan_id: Int,
    val ukuran_tebar: Double,
    val waktu_feeding: Long
)
