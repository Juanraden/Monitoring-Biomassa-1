package com.kedaireka.monitoring_biomassa.util

import android.annotation.SuppressLint
import com.kedaireka.monitoring_biomassa.data.domain.KerambaDomain
import com.kedaireka.monitoring_biomassa.data.network.KerambaNetwork
import java.text.SimpleDateFormat
import javax.inject.Inject

class KerambaNetworkMapper @Inject constructor(): EntityMapper<KerambaNetwork, KerambaDomain> {
    @SuppressLint("SimpleDateFormat")
    override fun mapFromEntity(entity: KerambaNetwork): KerambaDomain {
        return KerambaDomain(
            keramba_id = entity.keramba_id.toInt(),
            nama_keramba = entity.nama,
            ukuran = entity.ukuran.toDouble(),
            tanggal_install = SimpleDateFormat("yyyy-MM-dd").parse(entity.tanggal_install)!!.time
        )
    }

    @SuppressLint("SimpleDateFormat")
    override fun mapToEntity(target: KerambaDomain): KerambaNetwork {
        return KerambaNetwork(
            keramba_id = target.keramba_id.toString(),
            nama = target.nama_keramba,
            ukuran = target.ukuran.toString(),
            tanggal_install = SimpleDateFormat("yyyy-MM-dd").format(target.tanggal_install).toString()
        )
    }
}