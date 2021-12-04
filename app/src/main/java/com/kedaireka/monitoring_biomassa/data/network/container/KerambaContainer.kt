package com.kedaireka.monitoring_biomassa.data.network.container

import com.kedaireka.monitoring_biomassa.data.network.KerambaNetwork
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class KerambaContainer(
    override val status: Boolean,
    override val message: String,
    override val data: List<KerambaNetwork>,
): BaseContainer<KerambaNetwork>()