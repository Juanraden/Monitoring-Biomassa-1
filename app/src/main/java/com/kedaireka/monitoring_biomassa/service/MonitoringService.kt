package com.kedaireka.monitoring_biomassa.service

import com.kedaireka.monitoring_biomassa.data.network.KerambaContainer
import com.kedaireka.monitoring_biomassa.data.network.LoginContainer
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface MonitoringService {

    @GET("keramba")
    fun getKerambaListAsync(): Deferred<Response<KerambaContainer>>

    @FormUrlEncoded
    @POST("auth/login")
    fun loginUser(@Field("username") username: String, @Field("password") password: String)
    : Deferred<Response<LoginContainer>>
}