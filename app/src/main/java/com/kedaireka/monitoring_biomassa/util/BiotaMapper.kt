package com.kedaireka.monitoring_biomassa.util

import com.kedaireka.monitoring_biomassa.data.domain.BiotaDomain
import com.kedaireka.monitoring_biomassa.database.entity.Biota
import javax.inject.Inject

class BiotaMapper @Inject constructor(): EntityMapper<Biota, BiotaDomain> {
    override fun mapFromEntity(entity: Biota): BiotaDomain {
        return BiotaDomain(
            biota_id = entity.biota_id,
            jenis_biota = entity.jenis_biota,
            bobot = entity.bobot,
            panjang = entity.panjang,
            jumlah_bibit = entity.jumlah_bibit,
            tanggal_tebar = entity.tanggal_tebar,
            tanggal_panen = entity.tanggal_panen,
            keramba_id = entity.keramba_id
        )
    }

    override fun mapToEntity(target: BiotaDomain): Biota {
        return Biota(
            biota_id = target.biota_id,
            jenis_biota = target.jenis_biota,
            bobot = target.bobot,
            panjang = target.panjang,
            jumlah_bibit = target.jumlah_bibit,
            tanggal_tebar = target.tanggal_tebar,
            tanggal_panen = target.tanggal_panen,
            keramba_id = target.keramba_id
        )
    }
}