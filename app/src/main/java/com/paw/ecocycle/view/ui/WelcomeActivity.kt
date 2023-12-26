package com.paw.ecocycle.view.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.paw.ecocycle.R
import com.paw.ecocycle.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        setupAction()
    }

    private fun setupAction() {
        binding.btnLoginActivity.setOnClickListener(this)
        binding.btnSignupActivity.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btn_login_activity -> {
                startActivity(Intent(this, LoginActivity::class.java))
            }
            R.id.btn_signup_activity -> {
                startActivity(Intent(this, SignupActivity::class.java))
            }
        }
    }
}