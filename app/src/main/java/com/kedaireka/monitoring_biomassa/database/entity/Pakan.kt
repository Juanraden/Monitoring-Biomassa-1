package com.kedaireka.monitoring_biomassa.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pakan")
data class Pakan(
    @PrimaryKey(autoGenerate = true)
    val pakanid: Int,

    val jenis_pakan: String
)
