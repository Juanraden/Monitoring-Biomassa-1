package com.kedaireka.monitoring_biomassa.util

import com.kedaireka.monitoring_biomassa.data.domain.FeedingDetailDomain
import com.kedaireka.monitoring_biomassa.data.network.FeedingDetailNetwork
import javax.inject.Inject

class FeedingDetailNetworkMapper @Inject constructor(): EntityMapper<FeedingDetailNetwork, FeedingDetailDomain> {
    override fun mapFromEntity(entity: FeedingDetailNetwork): FeedingDetailDomain {
        return FeedingDetailDomain(
            detail_id = entity.detail_id.toInt(),
            feeding_id = entity.feeding_id.toInt(),
            ukuran_tebar = entity.ukuran_tebar.toDouble(),
            waktu_feeding = convertStringToDateLong(entity.jam_feeding,"H:m"),
            pakan_id = entity.pakan_id.toInt()
        )
    }

    override fun mapToEntity(target: FeedingDetailDomain): FeedingDetailNetwork {
        return FeedingDetailNetwork(
            detail_id = target.detail_id.toString(),
            feeding_id = target.feeding_id.toString(),
            ukuran_tebar = target.ukuran_tebar.toString(),
            jam_feeding = convertLongToDateString(target.waktu_feeding, "H:m"),
            pakan_id = target.pakan_id.toString()
        )
    }
}