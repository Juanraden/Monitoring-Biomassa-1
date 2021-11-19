package com.kedaireka.monitoring_biomassa.util

import com.kedaireka.monitoring_biomassa.data.domain.PengukuranDomain
import com.kedaireka.monitoring_biomassa.database.entity.Pengukuran
import javax.inject.Inject

class PengukuranMapper @Inject constructor(): EntityMapper<Pengukuran, PengukuranDomain> {
    override fun mapFromEntity(entity: Pengukuran): PengukuranDomain {
        return PengukuranDomain(
            pengukuranid = entity.pengukuranid,
            panjang = entity.panjang,
            bobot = entity.bobot,
            tanggal_ukur = entity.tanggal_ukur,
            biotaid = entity.biotaid
        )
    }

    override fun mapToEntity(target: PengukuranDomain): Pengukuran {
        return Pengukuran(
            pengukuranid = target.pengukuranid,
            panjang = target.panjang,
            bobot = target.bobot,
            tanggal_ukur = target.tanggal_ukur,
            biotaid = target.biotaid
        )
    }
}