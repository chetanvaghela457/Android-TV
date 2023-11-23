package com.strimm.application.retrofit

import com.strimm.application.model.CategoriesData
import com.strimm.application.model.ChannelData
import com.strimm.application.model.LoginResponse
import com.strimm.application.model.MeApiRes
import com.strimm.application.model.MeApiResponse
import com.strimm.application.model.SearchResponse
import com.strimm.application.model.VideoData
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface StrimmApi {

    @GET("users/{user_id}/channels/video")
    suspend fun getVideosByChannel(
        @Header("Authorization") authorization: String,
        @Path("user_id") user_id: String,
        @Query("channels_ids") channels_ids: String,
        @Query("dates") dates: String,
        @Query("page") page: Int,
        @Query("per_page") per_page: Int,
        @Query("all_videos") all_videos: Int,
        @Query("timezone") timezone: String
    ): Response<VideoData>

    @GET("users/{user_id}/channels")
    suspend fun getAllChannelsList(
        @Path("user_id") user_id: String,
        @Query("skip") skip: Int,
        @Query("take") take: Int,
        @Query("category_id") category_id: String
    ): Response<ChannelData>

    @GET("users/{user_id}/search")
    suspend fun searchItem(
        @Path("user_id") user_id: String,
        @Query("key_word") key_word: String,
        @Query("channel_skip") channel_skip: Int,
        @Query("channel_take") channel_take: Int,
        @Query("video_skip") video_skip: Int,
        @Query("video_take") video_take: Int,
        @Query("timezone") timezone: String
    ): Response<SearchResponse>

    @GET("users/{user_id}/categories")
    suspend fun getCategoriesList(
        @Header("Authorization") authorization: String,
        @Path("user_id") user_id: String,
        @Query("default") skip: Int
    ): Response<CategoriesData>

    @FormUrlEncoded
    @POST("auth/login")
    suspend fun loginAuth(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<LoginResponse>

    @GET("auth/me")
    suspend fun afterLoginMe(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String
    ): Response<MeApiResponse>

}