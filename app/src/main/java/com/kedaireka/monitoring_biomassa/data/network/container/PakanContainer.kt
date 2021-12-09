package com.kedaireka.monitoring_biomassa.data.network.container

import com.kedaireka.monitoring_biomassa.data.network.PakanNetwork
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PakanContainer(
    override val status: Boolean,
    override val message: String,
    override val data: List<PakanNetwork>
): BaseContainer<PakanNetwork>()