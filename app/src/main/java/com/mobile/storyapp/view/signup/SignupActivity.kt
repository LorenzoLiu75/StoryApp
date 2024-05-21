package com.mobile.storyapp.view.signup

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.mobile.storyapp.R
import com.mobile.storyapp.data.AuthRepository
import com.mobile.storyapp.data.api.AuthApiConfig
import com.mobile.storyapp.data.pref.UserPreference
import com.mobile.storyapp.databinding.ActivitySignupBinding
import com.mobile.storyapp.view.AuthViewModelFactory

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private val signupViewModel: SignupViewModel by viewModels {
        AuthViewModelFactory(
            AuthRepository(AuthApiConfig.getApiService(getUserToken()), UserPreference.getInstance(this))
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()

        val emailEditText = binding.edRegisterEmail
        val emailInputLayout = binding.emailEditTextLayout
        val passwordEditText = binding.edRegisterPassword
        val passwordInputLayout = binding.passwordEditTextLayout

        emailInputLayout.setEditText(emailEditText)
        passwordInputLayout.setEditText(passwordEditText)

        binding.signupButton.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()

            signupViewModel.register(name, email, password)
        }

        signupViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        signupViewModel.registrationStatus.observe(this) { status ->
            when (status) {
                is SignupViewModel.RegistrationStatus.Loading -> {
                }
                is SignupViewModel.RegistrationStatus.Success -> {
                    showDialog(status.message)
                }
                is SignupViewModel.RegistrationStatus.Error -> {
                    showDialog(status.message)
                }
            }
        }
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

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showDialog(message: String) {
        AlertDialog.Builder(this).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton(getString(R.string.lanjut)) { _, _ ->
                finish()
            }
            create()
            show()
        }
    }

    private fun getUserToken(): String {
        val sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_USER_TOKEN, "") ?: ""
    }

    companion object {
        const val PREF_NAME = "UserPreferences"
        const val KEY_USER_TOKEN = "userToken"
    }
}