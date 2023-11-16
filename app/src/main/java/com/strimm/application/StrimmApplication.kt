package com.strimm.application

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

class StrimmApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        AndroidThreeTen.init(applicationContext);
        mInstance = this

    }

    companion object {
        private var mInstance: StrimmApplication? = null

        @Synchronized
        @JvmStatic
        fun getInstance(): StrimmApplication {
            return mInstance ?: StrimmApplication().also { mInstance = it }
        }
    }
}