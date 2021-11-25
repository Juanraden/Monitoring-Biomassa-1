package com.kedaireka.monitoring_biomassa.data.network

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class KerambaContainer(
    override val status: Boolean,
    override val message: String,
    override val data: List<KerambaNetwork>,
): BaseContainer<KerambaNetwork>()