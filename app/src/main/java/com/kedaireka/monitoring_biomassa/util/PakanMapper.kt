package com.kedaireka.monitoring_biomassa.util

import com.kedaireka.monitoring_biomassa.data.domain.PakanDomain
import com.kedaireka.monitoring_biomassa.database.entity.Pakan
import javax.inject.Inject

class PakanMapper @Inject constructor(): EntityMapper<Pakan, PakanDomain> {
    override fun mapFromEntity(entity: Pakan): PakanDomain {
        return PakanDomain(
            pakanid = entity.pakanid,
            jenis_pakan = entity.jenis_pakan
        )
    }

    override fun mapToEntity(target: PakanDomain): Pakan {
        return Pakan(
            pakanid = target.pakanid,
            jenis_pakan = target.jenis_pakan
        )
    }

    fun mapFromList(entities: List<Pakan>): List<PakanDomain>{
        return entities.map { mapFromEntity(it) }
    }
}