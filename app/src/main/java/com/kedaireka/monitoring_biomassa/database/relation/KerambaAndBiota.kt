package com.kedaireka.monitoring_biomassa.database.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.kedaireka.monitoring_biomassa.database.entity.Biota
import com.kedaireka.monitoring_biomassa.database.entity.Keramba

data class KerambaAndBiota(
    @Embedded val keramba: Keramba,
    @Relation(
        parentColumn = "keramba_id",
        entityColumn = "keramba_id"
    )
    val biotaList: List<Biota>
    )
