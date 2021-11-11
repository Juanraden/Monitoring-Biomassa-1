package com.kedaireka.monitoring_biomassa.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "keramba")
data class Keramba constructor(
    @PrimaryKey(autoGenerate = true)
    val kerambaid: Int = 0,
    val nama_keramba: String,
    val ukuran: Double,
    val tanggal_install: Long,
)