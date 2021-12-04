package com.kedaireka.monitoring_biomassa.data.network.container

import com.kedaireka.monitoring_biomassa.data.network.PengukuranNetwork
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PengukuranContainer(
    override val status: Boolean,
    override val message: String,
    override val data: List<PengukuranNetwork>
): BaseContainer<PengukuranNetwork>()

