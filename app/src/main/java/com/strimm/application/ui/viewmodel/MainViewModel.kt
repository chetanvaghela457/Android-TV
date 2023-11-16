package com.strimm.application.ui.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.strimm.application.StrimmApplication
import com.strimm.application.model.CategoriesData
import com.strimm.application.model.ChannelItem
import com.strimm.application.model.VideoData
import com.strimm.application.prefstore.PrefStoreImpl
import com.strimm.application.prefstore.PreferenceKeys
import com.strimm.application.repository.StrimmRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(private val repository: StrimmRepository) : ViewModel() {

    val prefStore by lazy { PrefStoreImpl(StrimmApplication.getInstance().applicationContext) }
    val channelsListLiveData = MutableLiveData<ArrayList<ChannelItem>>()

    fun getChannelsList(category_id:String): LiveData<ArrayList<ChannelItem>> {
        viewModelScope.launch {
            repository.getAllChannelsList(category_id).observeForever {
                channelsListLiveData.postValue(it)
            }
        }
        return channelsListLiveData
    }


    val videosDataLiveData = MutableLiveData<VideoData>()

    @RequiresApi(Build.VERSION_CODES.N)
    fun getLanguages(channelId: String, date: String): LiveData<VideoData> {
        viewModelScope.launch {

            val authToken =
                prefStore.getString(PreferenceKeys.API_AUTH_TOKEN).firstOrNull() ?: ""

            repository.getVideosFromChannel(channelId, date,authToken)
                .observeForever {
                    videosDataLiveData.postValue(it)
                }

        }
        return videosDataLiveData
    }


    val categoryLiveData = MutableLiveData<CategoriesData>()

    fun getCategories(): LiveData<CategoriesData> {
        viewModelScope.launch {

            val authToken =
                prefStore.getString(PreferenceKeys.API_AUTH_TOKEN).firstOrNull() ?: ""

            repository.getCategoriesList(authToken).observeForever {
                    categoryLiveData.postValue(it)
                }
        }
        return categoryLiveData
    }
}