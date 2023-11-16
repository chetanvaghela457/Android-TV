package com.strimm.application.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.strimm.application.R
import com.strimm.application.model.CategoriesData
import com.strimm.application.model.ChannelItem
import com.strimm.application.model.LoginResponse
import com.strimm.application.model.ThemeData
import com.strimm.application.model.VideoData
import com.strimm.application.retrofit.StrimmApi
import com.strimm.application.utils.toJsonObject
import java.io.IOException
import java.util.TimeZone
import javax.inject.Inject

class StrimmRepository @Inject constructor(private val api: StrimmApi) {

    private val _channelsList = MutableLiveData<ArrayList<ChannelItem>>()
    val channelsList: LiveData<ArrayList<ChannelItem>>
        get() = _channelsList

    suspend fun getAllChannelsList(category_id: String): LiveData<ArrayList<ChannelItem>> {

        val result = api.getAllChannelsList(0, 10, category_id)

        if (result.isSuccessful && result.body() != null) {
            _channelsList.postValue(result.body()!!.data)
        }

        return _channelsList
    }


    private val _videosData = MutableLiveData<VideoData>()
    val videosData: LiveData<VideoData>
        get() = _videosData

    suspend fun getVideosFromChannel(
        channelId: String,
        date: String,
        token: String
    ): LiveData<VideoData> {

        val result = api.getVideosByChannel(
            "Bearer $token",
            channelId,
            date, 1, 10000, 1, TimeZone.getDefault().id
        )

        if (result.isSuccessful && result.body() != null) {
            Log.e("TAG", "getAllChannelsList: " + result.body().toJsonObject())
            _videosData.postValue(result.body())
        }

        return _videosData
    }


    private val _categoriesData = MutableLiveData<CategoriesData>()
    val categoriesData: LiveData<CategoriesData>
        get() = _categoriesData

    suspend fun getCategoriesList(token: String): LiveData<CategoriesData> {

        val result = api.getCategoriesList(
            "Bearer $token",
            1
        )

        if (result.isSuccessful && result.body() != null) {
            Log.e("TAG", "getAllChannelsList: " + result.body().toJsonObject())
            _categoriesData.postValue(result.body())
        }

        return _categoriesData
    }


    private val _loginResData = MutableLiveData<LoginResponse>()
    val loginResData: LiveData<LoginResponse>
        get() = _loginResData

    suspend fun loginAuth(email: String, password: String): LiveData<LoginResponse> {

        val result = api.loginAuth(
            email, password
        )

        if (result.isSuccessful && result.body() != null) {
            Log.e("TAG", "getAllChannelsList: " + result.body().toJsonObject())
            _loginResData.postValue(result.body())
        }

        return _loginResData
    }


    private val _themeItem = MutableLiveData<ThemeData>()
    val themeItem: LiveData<ThemeData>
        get() = _themeItem

    fun getThemeData(context: Context): LiveData<ThemeData> {

        val jsonFileString = getJsonDataFromRaw(context, R.raw.theme_data)
        val type = object : TypeToken<ThemeData>() {}.type
        _themeItem.postValue(Gson().fromJson(jsonFileString, type))

        return _themeItem
    }

    fun getJsonDataFromRaw(context: Context, fileName: Int): String {
        val jsonString: String
        try {
            jsonString =
                context.resources.openRawResource(fileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return "null"
        }
        return jsonString
    }
}