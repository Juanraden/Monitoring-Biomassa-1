package com.kedaireka.monitoring_biomassa.service

import com.kedaireka.monitoring_biomassa.data.network.container.BiotaContainer
import com.kedaireka.monitoring_biomassa.data.network.container.KerambaContainer
import com.kedaireka.monitoring_biomassa.data.network.container.LoginContainer
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.*

interface MonitoringService {

    //login
    @FormUrlEncoded
    @POST("auth/login")
    fun loginUserAsync(@Field("username") username: String, @Field("password") password: String)
            : Deferred<Response<LoginContainer>>

    // keramba section
    @GET("keramba")
    fun getKerambaListAsync(@Header("api-key") token: String, @Query("user_id") userId: Int): Deferred<Response<KerambaContainer>>

    @FormUrlEncoded
    @POST("keramba")
    fun addKerambaAsync(@Header("api-key") token: String, @FieldMap data: Map<String, String>): Deferred<Response<KerambaContainer>>

    @FormUrlEncoded
    @POST("keramba/update")
    fun updateKerambaAsync(@Header("api-key") token: String, @FieldMap data: Map<String, String>): Deferred<Response<KerambaContainer>>

    @FormUrlEncoded
    @POST("keramba/delete")
    fun deleteKerambaAsync(@Header("api-key") token: String, @Query("user_id") userId: Int, @Query("keramba_id") kerambaId: Int): Deferred<Response<KerambaContainer>>

    // biota section
    @GET("biota")
    fun getBiotaListAsync(@Header("api-key") token: String, @Query("user_id") userId: Int): Deferred<Response<BiotaContainer>>

    @GET("history")
    fun getHistoryBiotaListAsync(@Header("api-key") token: String, @Query("user_id") userId: Int, @Query("keramba_id") kerambaId: Int): Deferred<Response<BiotaContainer>>

    @FormUrlEncoded
    @POST("biota")
    fun addBiotaAsync(@Header("api-key") token: String, @FieldMap data: Map<String, String>): Deferred<Response<BiotaContainer>>

    @FormUrlEncoded
    @POST("biota/update")
    fun updateBiotaAsync(@Header("api-key") token: String, @FieldMap data: Map<String, String>): Deferred<Response<BiotaContainer>>

    @FormUrlEncoded
    @POST("biota/delete")
    fun deleteBiotaAsync(@Header("api-key") token: String, @Query("user_id") userId: Int, @Query("biota_id") biotaId: Int): Deferred<Response<BiotaContainer>>
}