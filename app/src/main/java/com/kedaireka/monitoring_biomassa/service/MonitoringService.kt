package com.kedaireka.monitoring_biomassa.service

import com.kedaireka.monitoring_biomassa.data.network.KerambaContainer
import kotlinx.coroutines.Deferred
import retrofit2.http.GET

interface MonitoringService {

    @GET("keramba")
    fun getKerambaListAsync(): Deferred<KerambaContainer>
}