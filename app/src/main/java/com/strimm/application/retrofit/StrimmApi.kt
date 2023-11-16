package com.strimm.application.retrofit

import com.strimm.application.model.CategoriesData
import com.strimm.application.model.ChannelData
import com.strimm.application.model.LoginResponse
import com.strimm.application.model.VideoData
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface StrimmApi {

    @GET("users/all/channels/video")
    suspend fun getVideosByChannel(
        @Header("Authorization") authorization: String,
        @Query("channels_ids") channels_ids: String,
        @Query("dates") dates: String,
        @Query("page") page: Int,
        @Query("per_page") per_page: Int,
        @Query("all_videos") all_videos: Int,
        @Query("timezone") timezone: String
    ): Response<VideoData>

    @GET("users/all/channels")
    suspend fun getAllChannelsList(
        @Query("skip") skip: Int,
        @Query("take") take: Int,
        @Query("category_id") category_id: String
    ): Response<ChannelData>

    @GET("users/all/categories")
    suspend fun getCategoriesList(
        @Header("Authorization") authorization: String,
        @Query("default") skip: Int
    ): Response<CategoriesData>

    @FormUrlEncoded
    @POST("auth/login")
    suspend fun loginAuth(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<LoginResponse>

}