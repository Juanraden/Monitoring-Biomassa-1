package com.kedaireka.monitoring_biomassa.data.network.container

import androidx.annotation.Keep
import com.kedaireka.monitoring_biomassa.data.network.PengukuranNetwork
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class PengukuranContainer(
    override val status: Boolean,
    override val message: String,
    override val data: List<PengukuranNetwork>
): BaseContainer<PengukuranNetwork>()

