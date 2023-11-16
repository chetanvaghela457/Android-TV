package com.strimm.application.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.strimm.application.repository.StrimmRepository
import javax.inject.Inject

class MainViewModelFactory @Inject constructor(private val repository: StrimmRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(repository) as T
    }

}

class LoginViewModelFactory @Inject constructor(private val repository: StrimmRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LoginViewModel(repository) as T
    }

}