package com.mobile.storyapp.view.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.mobile.storyapp.data.pref.UserModel
import com.mobile.storyapp.databinding.ActivityLoginBinding
import com.mobile.storyapp.view.ViewModelFactory
import com.mobile.storyapp.view.main.MainActivity

class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()

        val emailEditText = binding.edLoginEmail
        val emailInputLayout = binding.emailEditTextLayout
        val passwordEditText = binding.edLoginPassword
        val passwordInputLayout = binding.passwordEditTextLayout

        emailInputLayout.setEditText(emailEditText)
        passwordInputLayout.setEditText(passwordEditText)
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            viewModel.login(email, password)

            viewModel.isLoading.observe(this) { isLoading ->
                showLoading(isLoading)
            }

            viewModel.loginResult.observe(this) { response ->
                if (!response.error!!) {
                    val token = response.loginResult?.token
                    if (token != null) {
                        val userModel = UserModel(email, token, true)
                        viewModel.saveSession(userModel)

                        AlertDialog.Builder(this).apply {
                            setTitle("Yeah!")
                            setMessage("Success")
                            setPositiveButton("Lanjut") { _, _ ->
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                finish()
                            }
                            create()
                            show()
                        }
                    }
                } else {
                    AlertDialog.Builder(this).apply {
                        setTitle("Error")
                        setMessage("Login failed")
                        setPositiveButton("OK") { _, _ -> }
                        create()
                        show()
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}