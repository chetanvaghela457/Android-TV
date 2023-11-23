package com.strimm.application

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.strimm.application.databinding.ActivityHomeBinding
import com.strimm.application.di.ApplicationComponent
import com.strimm.application.di.DaggerApplicationComponent
import com.strimm.application.ui.viewmodel.MainViewModel
import com.strimm.application.ui.viewmodel.MainViewModelFactory
import com.strimm.application.utils.SharedPrefsManager
import javax.inject.Inject

class HomeActivity : AppCompatActivity() {

    lateinit var mainViewModel: MainViewModel

    lateinit var applicationComponent: ApplicationComponent

    @Inject
    lateinit var mainViewModelFactory: MainViewModelFactory

    private var _binding: ActivityHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(
            SharedPrefsManager.newInstance(this).getInt("AppTheme", R.style.Theme_EStrimm20)
        )
        _binding = ActivityHomeBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)

        applicationComponent = DaggerApplicationComponent.builder().build()
        applicationComponent.injectHome(this)

        mainViewModel = ViewModelProvider(this, mainViewModelFactory).get(MainViewModel::class.java)

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_browse_fragment, EpgFragment())
                .commitNow()
        }
    }
}