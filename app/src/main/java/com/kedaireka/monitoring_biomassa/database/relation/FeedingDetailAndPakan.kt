package com.kedaireka.monitoring_biomassa.database.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.kedaireka.monitoring_biomassa.database.entity.FeedingDetail
import com.kedaireka.monitoring_biomassa.database.entity.Pakan

data class FeedingDetailAndPakan(
    @Embedded val feedingDetail: FeedingDetail,

    @Relation(
        parentColumn = "pakan_id",
        entityColumn = "pakan_id"
    )
    val pakan: Pakan
)