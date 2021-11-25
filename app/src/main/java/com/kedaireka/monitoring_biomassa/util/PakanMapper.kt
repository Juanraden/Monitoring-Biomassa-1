package com.kedaireka.monitoring_biomassa.util

import com.kedaireka.monitoring_biomassa.data.domain.PakanDomain
import com.kedaireka.monitoring_biomassa.database.entity.Pakan
import javax.inject.Inject

class PakanMapper @Inject constructor(): EntityMapper<Pakan, PakanDomain> {
    override fun mapFromEntity(entity: Pakan): PakanDomain {
        return PakanDomain(
            pakan_id = entity.pakan_id,
            jenis_pakan = entity.jenis_pakan,
            deskripsi = entity.deskripsi
        )
    }

    override fun mapToEntity(target: PakanDomain): Pakan {
        return Pakan(
            pakan_id = target.pakan_id,
            jenis_pakan = target.jenis_pakan,
            deskripsi = target.deskripsi
        )
    }
}