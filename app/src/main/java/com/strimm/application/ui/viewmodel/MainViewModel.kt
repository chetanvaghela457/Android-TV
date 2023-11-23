package com.strimm.application.ui.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.strimm.application.StrimmApplication
import com.strimm.application.model.CategoriesData
import com.strimm.application.model.ChannelItem
import com.strimm.application.model.SearchedData
import com.strimm.application.model.VideoData
import com.strimm.application.prefstore.PrefStoreImpl
import com.strimm.application.prefstore.PreferenceKeys
import com.strimm.application.repository.StrimmRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(private val repository: StrimmRepository) : ViewModel() {

    val prefStore by lazy { PrefStoreImpl(StrimmApplication.getInstance().applicationContext) }

    val searchLiveData = MutableLiveData<ArrayList<SearchedData>>()

    fun getSearchedList(keyword: String): LiveData<ArrayList<SearchedData>> {
        viewModelScope.launch {

            val userId =
                prefStore.getInt(PreferenceKeys.USER_ID).firstOrNull() ?: ""

            repository.getSearchedItem(keyword, userId.toString()).observeForever {
                searchLiveData.postValue(it as ArrayList<SearchedData>?)
            }
        }
        return searchLiveData
    }


    val channelsListLiveData = MutableLiveData<ArrayList<ChannelItem>>()

    fun getChannelsList(category_id: String): LiveData<ArrayList<ChannelItem>> {
        viewModelScope.launch {

            val userId =
                prefStore.getInt(PreferenceKeys.USER_ID).firstOrNull() ?: ""

            repository.getAllChannelsList(category_id, userId.toString()).observeForever {
                channelsListLiveData.postValue(it)
            }
        }
        return channelsListLiveData
    }


    val videosDataLiveData = MutableLiveData<VideoData>()

    @RequiresApi(Build.VERSION_CODES.N)
    fun getLanguages(channelId: String, date: String): LiveData<VideoData> {
        viewModelScope.launch {
            val userId =
                prefStore.getInt(PreferenceKeys.USER_ID).firstOrNull() ?: ""

            val authToken =
                prefStore.getString(PreferenceKeys.API_AUTH_TOKEN).firstOrNull() ?: ""

            repository.getVideosFromChannel(channelId, date, authToken, userId.toString())
                .observeForever {
                    videosDataLiveData.postValue(it)
                }

        }
        return videosDataLiveData
    }


    val categoryLiveData = MutableLiveData<CategoriesData>()

    fun getCategories(): LiveData<CategoriesData> {
        viewModelScope.launch {

            val userId =
                prefStore.getInt(PreferenceKeys.USER_ID).firstOrNull() ?: ""

            val authToken =
                prefStore.getString(PreferenceKeys.API_AUTH_TOKEN).firstOrNull() ?: ""

            repository.getCategoriesList(authToken, userId.toString()).observeForever {
                categoryLiveData.postValue(it)
            }
        }
        return categoryLiveData
    }


    fun getFavouriteData(): ArrayList<String> {

        var jobCardItem = ArrayList<String>()
        viewModelScope.launch {
            val json = prefStore.getString(PreferenceKeys.FAVOURITE_DATA).firstOrNull() ?: ""
            if (json.isNotEmpty()) {
                val type = object : TypeToken<ArrayList<String>>() {}.type
                jobCardItem = Gson().fromJson(json, type)
            }
        }
        return jobCardItem

    }
}