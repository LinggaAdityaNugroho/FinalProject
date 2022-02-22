package com.example.finalproject.settings

import androidx.biometric.BiometricManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.crocodic.core.extension.openActivity
import com.crocodic.core.extension.tos
import com.example.finalproject.R
import com.example.finalproject.activity.BaseActivity
import com.example.finalproject.constant.Const
import com.example.finalproject.databinding.ActivitySettingsBinding
import com.example.finalproject.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : BaseActivity<ActivitySettingsBinding, SettingViewModel>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLayoutRes(R.layout.activity_settings)

        //set toolbar
        setSupportActionBar(binding.toolbarSetting)

        //check Biometric
        if (hasBiometricCapability() == true) {
            tos(R.string.support_biometric)
        } else {
            tos(R.string.maaf_smartphone_anda_kentang, false)
        }

        binding.hasBiometric = hasBiometricCapability()
        binding.enableBiometric = session.getBoolean(Const.SESSION.BIOMETRIC)

        binding.switchBiometric.setOnCheckedChangeListener { compoundButton, b ->
            session.setValue(Const.SESSION.BIOMETRIC, b)
        }

    }

    //check biometric function
    private fun hasBiometricCapability(): Boolean? {
        val biometricManager = BiometricManager.from(this)
        return biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS
    }

    override fun onClick(v: View?) {

        when (v) {
            binding.btnLogOut -> {
                authLogoutSuccess()
                openActivity<LoginActivity>()
                finishAffinity()
            }
        }

        super.onClick(v)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
