package com.kedaireka.monitoring_biomassa.util

import com.kedaireka.monitoring_biomassa.data.domain.PengukuranDomain
import com.kedaireka.monitoring_biomassa.data.network.PengukuranNetwork

class PengukuranNetworkMapper: EntityMapper<PengukuranNetwork, PengukuranDomain> {
    override fun mapFromEntity(entity: PengukuranNetwork): PengukuranDomain {
        return PengukuranDomain(
            pengukuran_id = entity.pengukuran_id.toInt(),
            panjang = entity.panjang.toDouble(),
            bobot = entity.bobot.toDouble(),
            tanggal_ukur = convertStringToDateLong(entity.tanggal_ukur, "yyyy-MM-dd"),
            biota_id = entity.biota_id.toInt()
        )
    }

    override fun mapToEntity(target: PengukuranDomain): PengukuranNetwork {
        return PengukuranNetwork(
            pengukuran_id = target.pengukuran_id.toString(),
            panjang = target.panjang.toString(),
            bobot = target.bobot.toString(),
            tanggal_ukur = convertLongToDateString(target.tanggal_ukur, "yyyy-MM-dd"),
            biota_id = target.biota_id.toString()
        )
    }
}