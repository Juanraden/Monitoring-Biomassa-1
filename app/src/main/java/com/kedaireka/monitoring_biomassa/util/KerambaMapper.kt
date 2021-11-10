package com.kedaireka.monitoring_biomassa.util

import com.kedaireka.monitoring_biomassa.data.domain.KerambaDomain
import com.kedaireka.monitoring_biomassa.database.entity.Keramba
import javax.inject.Inject

class KerambaMapper @Inject constructor(): EntityMapper<Keramba, KerambaDomain> {
    override fun mapFromEntity(entity: Keramba): KerambaDomain {
        return KerambaDomain(
            kerambaid = entity.kerambaid,
            nama_keramba = entity.nama_keramba,
            ukuran = entity.ukuran,
            tanggal_install = entity.tanggal_install
        )
    }

    override fun mapToEntity(target: KerambaDomain): Keramba {
        return Keramba(
            kerambaid = target.kerambaid,
            nama_keramba = target.nama_keramba,
            ukuran = target.ukuran,
            tanggal_install = target.tanggal_install
        )
    }

    fun mapFromList(entities: List<Keramba>): List<KerambaDomain>{
        return entities.map{ mapFromEntity(it)}
    }
}