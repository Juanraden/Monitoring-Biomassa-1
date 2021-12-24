package com.kedaireka.monitoring_biomassa.service

import com.kedaireka.monitoring_biomassa.data.network.container.*
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
    fun getKerambaListAsync(
        @Header("Authorization") token: String,
        @Query("user_id") userId: Int
    ): Deferred<Response<KerambaContainer>>

    @FormUrlEncoded
    @POST("keramba")
    fun addKerambaAsync(
        @Header("Authorization") token: String,
        @FieldMap data: Map<String, String>
    ): Deferred<Response<KerambaContainer>>

    @FormUrlEncoded
    @PUT("keramba")
    fun updateKerambaAsync(
        @Header("Authorization") token: String,
        @FieldMap data: Map<String, String>
    ): Deferred<Response<KerambaContainer>>

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "keramba", hasBody = true)
    fun deleteKerambaAsync(
        @Header("Authorization") token: String,
        @FieldMap data: Map<String, String>
    ): Deferred<Response<KerambaContainer>>

    // biota section
    @GET("biota")
    fun getBiotaListAsync(
        @Header("Authorization") token: String,
        @Query("user_id") userId: Int,
        @Query("keramba_id") kerambaId: Int
    ): Deferred<Response<BiotaContainer>>

    @GET("history")
    fun getHistoryBiotaListAsync(
        @Header("Authorization") token: String,
        @Query("user_id") userId: Int,
        @Query("keramba_id") kerambaId: Int
    ): Deferred<Response<BiotaContainer>>

    @FormUrlEncoded
    @POST("biota")
    fun addBiotaAsync(
        @Header("Authorization") token: String,
        @FieldMap data: Map<String, String>
    ): Deferred<Response<BiotaContainer>>

    @FormUrlEncoded
    @PUT("biota")
    fun updateBiotaAsync(
        @Header("Authorization") token: String,
        @FieldMap data: Map<String, String>
    ): Deferred<Response<BiotaContainer>>

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "biota", hasBody = true)
    fun deleteBiotaAsync(
        @Header("Authorization") token: String,
        @FieldMap data: Map<String, String>
    ): Deferred<Response<BiotaContainer>>

    // pakan section
    @GET("pakan")
    fun getPakanListAsync(
        @Header("Authorization") token: String,
        @Query("user_id") userId: Int
    ): Deferred<Response<PakanContainer>>

    @FormUrlEncoded
    @POST("pakan")
    fun addPakanAsync(
        @Header("Authorization") token: String,
        @FieldMap data: Map<String, String>
    ): Deferred<Response<PakanContainer>>

    @FormUrlEncoded
    @PUT("pakan")
    fun updatePakanAsync(
        @Header("Authorization") token: String,
        @FieldMap data: Map<String, String>
    ): Deferred<Response<PakanContainer>>

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "pakan", hasBody = true)
    fun deletePakanAsync(
        @Header("Authorization") token: String,
        @FieldMap data: Map<String, String>
    ): Deferred<Response<PakanContainer>>

    //pengukuran section
    @GET("pengukuran")
    fun getPengukuranListAsync(
        @Header("Authorization") token: String,
        @Query("user_id") userId: Int,
        @Query("biota_id") biotaId: Int
    ): Deferred<Response<PengukuranContainer>>

    @FormUrlEncoded
    @POST("pengukuran")
    fun addPengukuranAsync(
        @Header("Authorization") token: String,
        @FieldMap data: Map<String, String>
    ): Deferred<Response<PengukuranContainer>>

    @FormUrlEncoded
    @PUT("pengukuran")
    fun updatePengukuranAsync(
        @Header("Authorization") token: String,
        @FieldMap data: Map<String, String>
    ): Deferred<Response<PengukuranContainer>>

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "pengukuran", hasBody = true)
    fun deletePengukuranAsync(
        @Header("Authorization") token: String,
        @FieldMap data: Map<String, String>
    ): Deferred<Response<PengukuranContainer>>

    //feeding
    @GET("feeding")
    fun getFeedingListAsync(
        @Header("Authorization") token: String,
        @Query("user_id") userId: Int,
        @Query("keramba_id") kerambaId: Int
    ): Deferred<Response<FeedingContainer>>

    @FormUrlEncoded
    @POST("feeding")
    fun addFeedingAsync(
        @Header("Authorization") token: String,
        @FieldMap data: Map<String, String>
    ): Deferred<Response<FeedingContainer>>

    @FormUrlEncoded
    @PUT("feeding")
    fun updateFeedingAsync(
        @Header("Authorization") token: String,
        @FieldMap data: Map<String, String>
    ): Deferred<Response<FeedingContainer>>

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "feeding", hasBody = true)
    fun deleteFeedingAsync(
        @Header("Authorization") token: String,
        @FieldMap data: Map<String, String>
    ): Deferred<Response<FeedingContainer>>

    @GET("feeding/detail")
    fun getFeedingDetailListAsync(
        @Header("Authorization") token: String,
        @Query("user_id") userId: Int,
        @Query("keramba_id") kerambaId: Int,
        @Query("activity_id") activityId: Int
    ): Deferred<Response<FeedingDetailContainer>>

    @FormUrlEncoded
    @POST("feeding/detail")
    fun addFeedingDetailAsync(
        @Header("Authorization") token: String,
        @FieldMap data: Map<String, String>
    ): Deferred<Response<FeedingDetailContainer>>

    //panen
    @GET("panen")
    fun getPanenListAsync(
        @Header("Authorization") token: String,
        @Query("user_id") userId: Int,
        @Query("keramba_id") kerambaId: Int
    ): Deferred<Response<PanenContainer>>

    @FormUrlEncoded
    @POST("panen")
    fun addPanenAsync(
        @Header("Authorization") token: String,
        @FieldMap data: Map<String, String>
    ): Deferred<Response<PanenContainer>>
}