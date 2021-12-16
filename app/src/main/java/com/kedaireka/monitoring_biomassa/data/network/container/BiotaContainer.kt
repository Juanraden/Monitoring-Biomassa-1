package com.kedaireka.monitoring_biomassa.data.network.container

import com.kedaireka.monitoring_biomassa.data.network.BiotaNetwork
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BiotaContainer(
    override val status: Boolean,
    override val message: String,
    override val data: List<BiotaNetwork>
): BaseContainer<BiotaNetwork>()