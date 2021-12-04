package com.kedaireka.monitoring_biomassa.data.network.container

import com.kedaireka.monitoring_biomassa.data.network.FeedingDetailNetwork
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FeedingDetailContainer(
    override val status: Boolean,
    override val message: String,
    override val data: List<FeedingDetailNetwork>
): BaseContainer<FeedingDetailNetwork>()