package com.example.finalproject.login

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.isEmptyRequired
import com.crocodic.core.extension.openActivity
import com.crocodic.core.extension.textOf
import com.crocodic.core.extension.tos
import com.example.finalproject.R
import com.example.finalproject.activity.BaseActivity
import com.example.finalproject.constant.Const
import com.example.finalproject.databinding.ActivityLoginBinding
import com.example.finalproject.home.HomeActivity
import com.example.finalproject.register.RegisterActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding, LoginViewModel>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLayoutRes(R.layout.activity_login)

        //set toolbaar
        setSupportActionBar(binding.toolbaarLogin)

        //transparent status bar
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        //get has biometric
        binding.hasBiometric = session.getBoolean(Const.SESSION.BIOMETRIC)

        observe()

    }

    private fun observe() {
        viewModel.apiResponse.observe(this) {
            when (it.status) {
                ApiStatus.LOADING -> {
                    it.message?.let { msg -> loadingDialog.show(msg) }
                }
                ApiStatus.SUCCESS -> {
                    loadingDialog.dismiss()
                    openActivity<HomeActivity>()
                    finish()
                }
                ApiStatus.WRONG -> {
                    it.message?.let { msg -> loadingDialog.setResponse(msg) }
                }
                else -> {
                    loadingDialog.dismiss()
                    tos(R.string.error)
                }
            }
        }
    }

    private fun validateForm() {
        if (listOf(binding.edPhoneLogin, binding.edPasswordLogin)
                .isEmptyRequired(R.string.form_tidak_boleh_kosong)
        ) else {
            loadingDialog.show(R.string.logging_in)
            viewModel.login(binding.edPhoneLogin.textOf(), binding.edPasswordLogin.textOf())
        }
    }

    private fun showBiometricPromt() {
        val builder = BiometricPrompt.PromptInfo.Builder()
            .setTitle(R.string.biometric_authetication.toString())
            .setSubtitle(R.string.enter_biometric_credentials.toString())
            .setDescription(R.string.input_your_fingerprint.toString())

        builder.apply {
            setNegativeButtonText(R.string.cancel.toString())
        }

        val promtInfo = builder.build()

        val biometricPrompt = initBiometricPromt { succes ->

            if (succes) {
                loadingDialog.show(R.string.logging_in)
                viewModel.login(
                    session.getString(Const.LOGIN.PHONE),
                    session.getString(Const.LOGIN.PASSWORD)
                )
            } else {
                tos(R.string.biometric_tidak_sesuai)
            }
        }

        biometricPrompt.authenticate(promtInfo)

    }

    private fun initBiometricPromt(listener: (succes: Boolean) -> Unit): BiometricPrompt {
        val executor = ContextCompat.getMainExecutor(this)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                listener(false)
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                listener(true)
            }
        }

        return BiometricPrompt(this, executor, callback)
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.btnSignIn -> validateForm()
            binding.tvRegister -> openActivity<RegisterActivity>()
            binding.btnBiometricLogin -> showBiometricPromt()
        }
        super.onClick(v)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

}