package com.strimm.application.ui.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strimm.application.StrimmApplication
import com.strimm.application.model.ThemeData
import com.strimm.application.prefstore.PrefStoreImpl
import com.strimm.application.prefstore.PreferenceKeys
import com.strimm.application.repository.StrimmRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginViewModel @Inject constructor(private val repository: StrimmRepository) : ViewModel() {


    val prefStore by lazy { PrefStoreImpl(StrimmApplication.getInstance().applicationContext) }

    private var _showLoading = MutableLiveData<Boolean>()
    val showLoading: LiveData<Boolean> = _showLoading

    private var _isLoginSuccess = MutableLiveData<Boolean>()
    val isLoginSuccess: LiveData<Boolean> = _isLoginSuccess

    fun login(email: String, password: String) {

        _showLoading.postValue(true)

        viewModelScope.launch {
            repository.loginAuth(email, password).observeForever {

                _showLoading.postValue(false)
                if (it.data.access_token.toString().isNotEmpty()) {

                    viewModelScope.launch {
                        prefStore.savePreference(
                            PreferenceKeys.API_AUTH_TOKEN,
                            it.data.access_token
                        )

                      /*  prefStore.savePreference(
                            PreferenceKeys.IS_LOGGED_IN,
                            true
                        )*/

                        _isLoginSuccess.postValue(true)
                    }


                }

            }

        }

    }

    val themeData = MutableLiveData<ThemeData>()

    fun getThemeData(context: Context): LiveData<ThemeData> {
        viewModelScope.launch {

            repository.getThemeData(context).observeForever {
                themeData.postValue(it)
            }
        }
        return themeData
    }

}