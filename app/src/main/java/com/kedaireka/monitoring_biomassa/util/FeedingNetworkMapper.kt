package com.kedaireka.monitoring_biomassa.util

import com.kedaireka.monitoring_biomassa.data.domain.FeedingDomain
import com.kedaireka.monitoring_biomassa.data.network.FeedingNetwork
import javax.inject.Inject

class FeedingNetworkMapper @Inject constructor(): EntityMapper<FeedingNetwork, FeedingDomain> {
    override fun mapFromEntity(entity: FeedingNetwork): FeedingDomain {
        return FeedingDomain(
            feeding_id = entity.feeding_id.toInt(),
            keramba_id = entity.keramba_id.toInt(),
            tanggal_feeding = convertStringToDateLong(entity.tanggal_feeding, "yyyy-MM-dd")
        )
    }

    override fun mapToEntity(target: FeedingDomain): FeedingNetwork {
        return FeedingNetwork(
            feeding_id = target.feeding_id.toString(),
            keramba_id = target.keramba_id.toString(),
            tanggal_feeding = convertLongToDateString(target.tanggal_feeding, "yyyy-MM-dd")
        )
    }
}