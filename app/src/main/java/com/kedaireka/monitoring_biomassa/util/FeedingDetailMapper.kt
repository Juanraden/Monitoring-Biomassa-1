package com.kedaireka.monitoring_biomassa.util

import com.kedaireka.monitoring_biomassa.data.domain.FeedingDetailDomain
import com.kedaireka.monitoring_biomassa.database.entity.FeedingDetail
import javax.inject.Inject

class FeedingDetailMapper @Inject constructor(): EntityMapper<FeedingDetail, FeedingDetailDomain> {
    override fun mapFromEntity(entity: FeedingDetail): FeedingDetailDomain {
        return FeedingDetailDomain(
            feeding_id = entity.feeding_id,
            pakan_id = entity.pakan_id,
            ukuran_tebar = entity.ukuran_tebar,
            waktu_feeding = entity.waktu_feeding,
        )
    }

    override fun mapToEntity(target: FeedingDetailDomain): FeedingDetail {
        return FeedingDetail(
            feeding_id = target.feeding_id,
            pakan_id = target.pakan_id,
            ukuran_tebar = target.ukuran_tebar,
            waktu_feeding = target.waktu_feeding,
        )
    }
}