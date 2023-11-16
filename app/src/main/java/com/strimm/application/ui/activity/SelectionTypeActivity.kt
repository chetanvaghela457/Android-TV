package com.strimm.application.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.strimm.application.R
import com.strimm.application.databinding.ActivitySelectionTypeBinding
import com.strimm.application.utils.SharedPrefsManager

class SelectionTypeActivity : AppCompatActivity() {

    private var _binding: ActivitySelectionTypeBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(
            SharedPrefsManager.newInstance(this).getInt("AppTheme", R.style.Theme_EStrimm20)
        )
        _binding = ActivitySelectionTypeBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)

        binding.apply {

            buttonSignup.setOnClickListener {

                val intent = Intent(this@SelectionTypeActivity, LoginActivity::class.java)
                startActivity(intent)

            }


            buttonLogIn.setOnClickListener {

                val intent = Intent(this@SelectionTypeActivity, LoginActivity::class.java)
                startActivity(intent)

            }

        }
    }
}