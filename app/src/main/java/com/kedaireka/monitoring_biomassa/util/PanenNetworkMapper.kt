package com.kedaireka.monitoring_biomassa.util

import com.kedaireka.monitoring_biomassa.data.domain.PanenDomain
import com.kedaireka.monitoring_biomassa.data.network.PanenNetwork
import javax.inject.Inject

class PanenNetworkMapper @Inject constructor(): EntityMapper<PanenNetwork, PanenDomain> {
    override fun mapFromEntity(entity: PanenNetwork): PanenDomain {
        return PanenDomain(
            activity_id = entity.activity_id.toInt(),
            tanggal_panen = convertStringToDateLong(entity.tanggal_panen, "yyyy-MM-dd"),
            panjang = entity.panjang.toDouble(),
            bobot = entity.bobot.toDouble(),
            jumlah_hidup = entity.jumlah_hidup.toInt(),
            jumlah_mati = entity.jumlah_mati.toInt(),
            biota_id = entity.biota_id.toInt(),
            keramba_id = entity.keramba_id.toInt()
        )
    }

    override fun mapToEntity(target: PanenDomain): PanenNetwork {
        return PanenNetwork(
            activity_id = target.activity_id.toString(),
            tanggal_panen = convertLongToDateString(target.tanggal_panen, "yyyy-MM-dd"),
            panjang = target.panjang.toString(),
            bobot = target.bobot.toString(),
            jumlah_hidup = target.jumlah_hidup.toString(),
            jumlah_mati = target.jumlah_mati.toString(),
            biota_id = target.biota_id.toString(),
            keramba_id = target.keramba_id.toString()
        )
    }
}