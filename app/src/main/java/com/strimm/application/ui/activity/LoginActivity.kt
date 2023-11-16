package com.strimm.application.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.strimm.application.HomeActivity
import com.strimm.application.R
import com.strimm.application.databinding.ActivityLoginBinding
import com.strimm.application.di.ApplicationComponent
import com.strimm.application.di.DaggerApplicationComponent
import com.strimm.application.extension.observeByLambda
import com.strimm.application.ui.viewmodel.LoginViewModel
import com.strimm.application.ui.viewmodel.LoginViewModelFactory
import com.strimm.application.utils.ProgressDialog
import com.strimm.application.utils.SharedPrefsManager
import javax.inject.Inject

class LoginActivity : AppCompatActivity() {

    lateinit var mainViewModel: LoginViewModel

    lateinit var applicationComponent: ApplicationComponent

    private val progressBar by lazy { ProgressDialog(this) }

    @Inject
    lateinit var mainViewModelFactory: LoginViewModelFactory

    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(
            SharedPrefsManager.newInstance(this).getInt("AppTheme", R.style.Theme_EStrimm20)
        )
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)

        applicationComponent = DaggerApplicationComponent.builder().build()
        applicationComponent.injectLogin(this)

        mainViewModel =
            ViewModelProvider(this, mainViewModelFactory).get(LoginViewModel::class.java)

        binding.apply {

            buttonSignIn.setOnClickListener {

                mainViewModel.login(editTextEmail.text.toString(), editText2.text.toString())

            }

        }

        mainViewModel.isLoginSuccess.observeByLambda(this) {

            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()

        }

        mainViewModel.showLoading.observeByLambda(this) { show ->
            if (show)
                progressBar.show()
            else
                progressBar.dismiss()
        }

    }
}