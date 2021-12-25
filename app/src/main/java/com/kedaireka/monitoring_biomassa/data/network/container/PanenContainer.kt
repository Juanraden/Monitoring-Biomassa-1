package com.kedaireka.monitoring_biomassa.data.network.container

import androidx.annotation.Keep
import com.kedaireka.monitoring_biomassa.data.network.PanenNetwork

@Keep
data class PanenContainer(
    override val status: Boolean,
    override val message: String,
    override val data: List<PanenNetwork>
): BaseContainer<PanenNetwork>()
