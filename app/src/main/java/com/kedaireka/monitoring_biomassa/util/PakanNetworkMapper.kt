package com.kedaireka.monitoring_biomassa.util

import com.kedaireka.monitoring_biomassa.data.domain.PakanDomain
import com.kedaireka.monitoring_biomassa.data.network.PakanNetwork
import javax.inject.Inject

class PakanNetworkMapper @Inject constructor(): EntityMapper<PakanNetwork, PakanDomain> {
    override fun mapFromEntity(entity: PakanNetwork): PakanDomain {
        return PakanDomain(
            pakan_id = entity.pakan_id.toInt(),
            jenis_pakan = entity.jenis_pakan,
            deskripsi = entity.deskripsi
        )
    }

    override fun mapToEntity(target: PakanDomain): PakanNetwork {
        return PakanNetwork(
            pakan_id = target.pakan_id.toString(),
            jenis_pakan = target.jenis_pakan,
            deskripsi = target.deskripsi
        )
    }
}