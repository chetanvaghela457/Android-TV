package com.strimm.application

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.strimm.application.utils.SharedPrefsManager

class PlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(
            SharedPrefsManager.newInstance(this).getInt("AppTheme", R.style.Theme_EStrimm20)
        )
        setContentView(R.layout.activity_player)
    }
}