package com.kedaireka.monitoring_biomassa.service

import com.kedaireka.monitoring_biomassa.data.network.KerambaContainer
import com.kedaireka.monitoring_biomassa.data.network.LoginContainer
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.*

interface MonitoringService {

    @GET("keramba")
    fun getKerambaListAsync(@Header("api-key") token: String, @Query("user_id") userId: Int): Deferred<Response<KerambaContainer>>

    @FormUrlEncoded
    @POST("keramba")
    fun addKerambaAsync(@Header("api-key") token: String, @FieldMap data: Map<String, String>): Deferred<Response<KerambaContainer>>

    @FormUrlEncoded
    @POST("auth/login")
    fun loginUserAsync(@Field("username") username: String, @Field("password") password: String)
    : Deferred<Response<LoginContainer>>
}