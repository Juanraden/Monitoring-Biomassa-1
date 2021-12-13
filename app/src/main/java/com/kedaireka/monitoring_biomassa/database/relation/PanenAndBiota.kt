package com.kedaireka.monitoring_biomassa.database.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.kedaireka.monitoring_biomassa.database.entity.Biota
import com.kedaireka.monitoring_biomassa.database.entity.Panen

data class PanenAndBiota(
    @Embedded val panen: Panen,
    @Relation(
        parentColumn = "biota_id",
        entityColumn = "biota_id"
    )
    val biota: Biota
)