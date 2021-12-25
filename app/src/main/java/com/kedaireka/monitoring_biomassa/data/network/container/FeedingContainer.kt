package com.kedaireka.monitoring_biomassa.data.network.container

import androidx.annotation.Keep
import com.kedaireka.monitoring_biomassa.data.network.FeedingNetwork
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class FeedingContainer(
    override val status: Boolean,
    override val message: String,
    override val data: List<FeedingNetwork>
): BaseContainer<FeedingNetwork>()