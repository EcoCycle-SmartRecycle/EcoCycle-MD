package com.paw.ecocycle.view.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.paw.ecocycle.databinding.ActivityLoginBinding
import com.paw.ecocycle.model.local.datastore.UserModel
import com.paw.ecocycle.utils.ResultState
import com.paw.ecocycle.utils.showToast
import com.paw.ecocycle.view.viewmodel.LoginViewModel
import com.paw.ecocycle.view.viewmodel.ViewModelFactory

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var factory: ViewModelFactory
    private val viewmodel: LoginViewModel by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        playAnimation()
        setupAction()
        setupViewModel()
    }

    private fun setupViewModel() {
        factory = ViewModelFactory.getInstance(this)
    }

    private fun setupAction() {
        binding.btnLogin.setOnClickListener {
            with(binding) {
                val email = edLoginEmail.text.toString()
                val password = edLoginPassword.text.toString()

                viewmodel.login(email, password).observe(this@LoginActivity) { response ->
                    when (response) {
                        ResultState.Loading -> {
                            pbLogin.isVisible = true
                        }

                        is ResultState.Error -> {
                            pbLogin.isVisible = false
                            showToast(response.error)
                        }

                        is ResultState.Success -> {
                            pbLogin.isVisible = false
                            viewmodel.saveSession(
                                UserModel(
                                    response.data.loginResult?.name.toString(),
                                    email,
                                    response.data.loginResult?.token.toString(),
                                    true
                                )
                            )
                            AlertDialog.Builder(this@LoginActivity).apply {
                                setTitle("Berhasil")
                                setMessage("Anda Berhasil Login")
                                setPositiveButton("Lanjut") { _, _ ->
                                    val toMain =
                                        Intent(this@LoginActivity, MainActivity::class.java)
                                    toMain.flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(toMain)
                                }
                                create()
                                show()
                            }
                        }

                        else -> {}
                    }
                }
            }
        }
    }
    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imgLogin, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val email =
            ObjectAnimator.ofFloat(binding.tlLoginEmail, View.ALPHA, 1f).setDuration(100)
        val password =
            ObjectAnimator.ofFloat(binding.tlLoginPassword, View.ALPHA, 1f).setDuration(100)
        val login = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(100)


        AnimatorSet().apply {
            playSequentially(
                email,
                password,
                login
            )
            startDelay = 100
        }.start()
    }
}