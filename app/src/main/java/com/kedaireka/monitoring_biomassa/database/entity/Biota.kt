package com.kedaireka.monitoring_biomassa.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "biota",
    foreignKeys= [ForeignKey(
        entity = Keramba::class,
        parentColumns = arrayOf("kerambaid"),
        childColumns = arrayOf("kerambaid"),
        onDelete = CASCADE,
        onUpdate = CASCADE
    )],
    indices = [Index("kerambaid")]
)
data class Biota constructor(
    @PrimaryKey
    val biotaid: Int,
    val jenis_biota: String,
    val bobot: Double,
    val panjang: Double,
    val jumlah_bibit: Int,
    val tanggal_tebar: Long,
    val tanggal_panen: Long,
    val kerambaid: Int,
)
