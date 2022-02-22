package com.example.finalproject.register

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.isEmptyRequired
import com.crocodic.core.extension.openActivity
import com.crocodic.core.extension.textOf
import com.crocodic.core.extension.tos
import com.example.finalproject.R
import com.example.finalproject.activity.BaseActivity
import com.example.finalproject.databinding.ActivityRegisterBinding
import com.example.finalproject.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : BaseActivity<ActivityRegisterBinding, RegisterViewModel>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLayoutRes(R.layout.activity_register)

        //transparent status bar
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

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
                    finishAffinity()
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
        if (listOf(binding.edNameRegister, binding.edPhoneRegister, binding.edPasswordRegister)
                .isEmptyRequired(R.string.form_tidak_boleh_kosong)
        ) else {
            loadingDialog.show(R.string.registering)
            viewModel.register(
                binding.edPhoneRegister.textOf(),
                binding.edPasswordRegister.textOf(),
                binding.edNameRegister.textOf()
            )
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.btnRegister -> validateForm()
        }
        super.onClick(v)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}