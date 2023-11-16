package com.strimm.application

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.strimm.application.databinding.ActivitySplashBinding
import com.strimm.application.di.ApplicationComponent
import com.strimm.application.di.DaggerApplicationComponent
import com.strimm.application.extension.observeByLambda
import com.strimm.application.ui.viewmodel.LoginViewModel
import com.strimm.application.ui.viewmodel.LoginViewModelFactory
import com.strimm.application.utils.mainThemeData
import java.io.IOException
import javax.inject.Inject

class SplashActivity : AppCompatActivity() {

    lateinit var mainViewModel: LoginViewModel

    lateinit var applicationComponent: ApplicationComponent

    @Inject
    lateinit var mainViewModelFactory: LoginViewModelFactory

    private var _binding: ActivitySplashBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPrefs = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val appLabel = sharedPrefs.getString(
            "appLabel",
            resources.getString(R.string.app_name)
        ) // Provide a default label if needed

        setTitle(appLabel)
        _binding = ActivitySplashBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)

        applicationComponent = DaggerApplicationComponent.builder().build()
        applicationComponent.injectSplash(this)

        mainViewModel =
            ViewModelProvider(this, mainViewModelFactory).get(LoginViewModel::class.java)



        mainViewModel.getThemeData(this).observeByLambda(this) {

            mainThemeData = it

            mainViewModel.login(mainThemeData.username, mainThemeData.password)

            Glide.with(this).load(R.raw.logo_image).into(binding.mainLogo)

            val sharedPrefs = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
            val editor = sharedPrefs.edit()
            editor.putString("appLabel", mainThemeData.appName)
            editor.apply()
        }

        mainViewModel.isLoginSuccess.observeByLambda(this) {

            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()

        }

        /*Handler(Looper.getMainLooper()).postDelayed({

            lifecycleScope.launch {

                val prefStore = PrefStoreImpl(this@SplashActivity)
                val isLogin =
                    prefStore.getBoolean(PreferenceKeys.IS_LOGGED_IN).firstOrNull() ?: false

                if (!isLogin) {

                    val intent = Intent(this@SplashActivity, SelectionTypeActivity::class.java)
                    startActivity(intent)
                    finish()

                } else {
                    val intent = Intent(this@SplashActivity, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

        }, 3000)*/
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