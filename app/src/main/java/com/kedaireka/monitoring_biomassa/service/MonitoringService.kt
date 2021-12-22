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
        @Header("api-key") token: String,
        @Query("user_id") userId: Int
    ): Deferred<Response<KerambaContainer>>

    @FormUrlEncoded
    @POST("keramba")
    fun addKerambaAsync(
        @Header("api-key") token: String,
        @FieldMap data: Map<String, String>
    ): Deferred<Response<KerambaContainer>>

    @FormUrlEncoded
    @POST("keramba/update")
    fun updateKerambaAsync(
        @Header("api-key") token: String,
        @FieldMap data: Map<String, String>
    ): Deferred<Response<KerambaContainer>>

    @FormUrlEncoded
    @POST("keramba/delete")
    fun deleteKerambaAsync(
        @Header("api-key") token: String,
        @FieldMap data: Map<String, String>
    ): Deferred<Response<KerambaContainer>>

    // biota section
    @GET("biota")
    fun getBiotaListAsync(
        @Header("api-key") token: String,
        @Query("user_id") userId: Int,
        @Query("keramba_id") kerambaId: Int
    ): Deferred<Response<BiotaContainer>>

    @GET("history")
    fun getHistoryBiotaListAsync(
        @Header("api-key") token: String,
        @Query("user_id") userId: Int,
        @Query("keramba_id") kerambaId: Int
    ): Deferred<Response<BiotaContainer>>

    @FormUrlEncoded
    @POST("biota")
    fun addBiotaAsync(
        @Header("api-key") token: String,
        @FieldMap data: Map<String, String>
    ): Deferred<Response<BiotaContainer>>

    @FormUrlEncoded
    @POST("biota/update")
    fun updateBiotaAsync(
        @Header("api-key") token: String,
        @FieldMap data: Map<String, String>
    ): Deferred<Response<BiotaContainer>>

    @FormUrlEncoded
    @POST("biota/delete")
    fun deleteBiotaAsync(
        @Header("api-key") token: String,
        @FieldMap data: Map<String, String>
    ): Deferred<Response<BiotaContainer>>

    // pakan section
    @GET("pakan")
    fun getPakanListAsync(
        @Header("api-key") token: String,
        @Query("user_id") userId: Int
    ): Deferred<Response<PakanContainer>>

    @FormUrlEncoded
    @POST("pakan")
    fun addPakanAsync(
        @Header("api-key") token: String,
        @FieldMap data: Map<String, String>
    ): Deferred<Response<PakanContainer>>

    @FormUrlEncoded
    @POST("pakan/update")
    fun updatePakanAsync(
        @Header("api-key") token: String,
        @FieldMap data: Map<String, String>
    ): Deferred<Response<PakanContainer>>

    @FormUrlEncoded
    @POST("pakan/delete")
    fun deletePakanAsync(
        @Header("api-key") token: String,
        @FieldMap data: Map<String, String>
    ): Deferred<Response<PakanContainer>>

    //pengukuran section
    @GET("pengukuran")
    fun getPengukuranListAsync(
        @Header("api-key") token: String,
        @Query("user_id") userId: Int,
        @Query("biota_id") biotaId: Int
    ): Deferred<Response<PengukuranContainer>>

    @FormUrlEncoded
    @POST("pengukuran")
    fun addPengukuranAsync(
        @Header("api-key") token: String,
        @FieldMap data: Map<String, String>
    ): Deferred<Response<PengukuranContainer>>

    @FormUrlEncoded
    @POST("pengukuran/update")
    fun updatePengukuranAsync(
        @Header("api-key") token: String,
        @FieldMap data: Map<String, String>
    ): Deferred<Response<PengukuranContainer>>

    @FormUrlEncoded
    @POST("pengukuran/delete")
    fun deletePengukuranAsync(
        @Header("api-key") token: String,
        @FieldMap data: Map<String, String>
    ): Deferred<Response<PengukuranContainer>>

    //feeding
    @GET("feeding")
    fun getFeedingListAsync(
        @Header("api-key") token: String,
        @Query("user_id") userId: Int,
        @Query("keramba_id") kerambaId: Int
    ): Deferred<Response<FeedingContainer>>

    @FormUrlEncoded
    @POST("feeding")
    fun addFeedingAsync(
        @Header("api-key") token: String,
        @FieldMap data: Map<String, String>
    ): Deferred<Response<FeedingContainer>>

    @FormUrlEncoded
    @POST("feeding/update")
    fun updateFeedingAsync(
        @Header("api-key") token: String,
        @FieldMap data: Map<String, String>
    ): Deferred<Response<FeedingContainer>>

    @FormUrlEncoded
    @POST("feeding/delete")
    fun deleteFeedingAsync(
        @Header("api-key") token: String,
        @FieldMap data: Map<String, String>
    ): Deferred<Response<FeedingContainer>>

    @GET("feeding/detail")
    fun getFeedingDetailListAsync(
        @Header("api-key") token: String,
        @Query("user_id") userId: Int,
        @Query("keramba_id") kerambaId: Int,
        @Query("activity_id") activityId: Int
    ): Deferred<Response<FeedingDetailContainer>>

    @FormUrlEncoded
    @POST("feeding/detail")
    fun addFeedingDetailAsync(
        @Header("api-key") token: String,
        @FieldMap data: Map<String, String>
    ): Deferred<Response<FeedingDetailContainer>>

    //panen
    @GET("panen")
    fun getPanenListAsync(
        @Header("api-key") token: String,
        @Query("user_id") userId: Int,
        @Query("keramba_id") kerambaId: Int
    ): Deferred<Response<PanenContainer>>

    @FormUrlEncoded
    @POST("panen")
    fun addPanenAsync(
        @Header("api-key") token: String,
        @FieldMap data: Map<String, String>
    ): Deferred<Response<PanenContainer>>
}