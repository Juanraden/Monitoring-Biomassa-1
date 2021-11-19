package com.kedaireka.monitoring_biomassa.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "pengukuran",
    foreignKeys = [ForeignKey(
        entity = Biota::class,
        parentColumns = arrayOf("biotaid"),
        childColumns = arrayOf("biotaid"),
        onDelete = CASCADE,
        onUpdate = CASCADE
    )],
    indices = [Index("biotaid")]
)
data class Pengukuran(
    @PrimaryKey(autoGenerate = true)
    val pengukuranid: Int = 0,

    val panjang: Double,

    val bobot: Double,

    val tanggal_ukur: Long,

    val biotaid: Int
)
