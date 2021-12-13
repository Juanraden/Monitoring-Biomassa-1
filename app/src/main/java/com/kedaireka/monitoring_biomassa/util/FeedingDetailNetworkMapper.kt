package com.kedaireka.monitoring_biomassa.util

import com.kedaireka.monitoring_biomassa.data.domain.FeedingDetailDomain
import com.kedaireka.monitoring_biomassa.data.network.FeedingDetailNetwork

class FeedingDetailNetworkMapper: EntityMapper<FeedingDetailNetwork, FeedingDetailDomain> {
    override fun mapFromEntity(entity: FeedingDetailNetwork): FeedingDetailDomain {
        return FeedingDetailDomain(
            feeding_id = entity.feeding_id.toInt(),
            ukuran_tebar = entity.ukuran_tebar.toDouble(),
            waktu_feeding = convertStringToDateLong(entity.jam_feeding,"h:m:s"),
            pakan_id = entity.pakan_id.toInt()
        )
    }

    override fun mapToEntity(target: FeedingDetailDomain): FeedingDetailNetwork {
        return FeedingDetailNetwork(
            feeding_id = target.feeding_id.toString(),
            ukuran_tebar = target.ukuran_tebar.toString(),
            jam_feeding = convertLongToDateString(target.waktu_feeding, "h:m:s"),
            pakan_id = target.pakan_id.toString()
        )
    }
}