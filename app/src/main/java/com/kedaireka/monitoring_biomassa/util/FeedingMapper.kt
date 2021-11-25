package com.kedaireka.monitoring_biomassa.util

import com.kedaireka.monitoring_biomassa.data.domain.FeedingDomain
import com.kedaireka.monitoring_biomassa.database.entity.Feeding
import javax.inject.Inject

class FeedingMapper @Inject constructor(): EntityMapper<Feeding, FeedingDomain> {
    override fun mapFromEntity(entity: Feeding): FeedingDomain {
        return FeedingDomain(
            feeding_id = entity.feeding_id,
            keramba_id = entity.keramba_id,
            tanggal_feeding = entity.tanggal_feeding
        )
    }

    override fun mapToEntity(target: FeedingDomain): Feeding {
        return Feeding(
            feeding_id = target.feeding_id,
            keramba_id = target.keramba_id,
            tanggal_feeding = target.tanggal_feeding
        )
    }
}