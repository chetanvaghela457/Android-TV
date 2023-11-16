package com.strimm.application.extension


import android.app.Activity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
inline fun <reified VM : ViewModel> viewModelFactory(crossinline function: () -> VM) =
    object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T = function() as T
    }


//Registering Observer to get Non-null data
fun <DATA> LiveData<DATA>.observeByLambda(
    lifecycleOwner: LifecycleOwner,
    observeFun: (DATA) -> Unit
) { observe(lifecycleOwner) { data ->
        data?.let { observeFun.invoke(it) }
    }
}

inline fun <reified T> Activity.getExtra(key: String): T? {
    return intent.extras?.get(key) as? T?
}