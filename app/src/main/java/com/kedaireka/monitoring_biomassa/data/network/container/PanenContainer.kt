package com.kedaireka.monitoring_biomassa.data.network.container

import com.kedaireka.monitoring_biomassa.data.network.PanenNetwork

data class PanenContainer(
    override val status: Boolean,
    override val message: String,
    override val data: List<PanenNetwork>
): BaseContainer<PanenNetwork>()
