package com.example.finalproject.welcome

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.crocodic.core.extension.openActivity
import com.example.finalproject.R
import com.example.finalproject.activity.BaseActivity
import com.example.finalproject.databinding.ActivityWelcomeBinding
import com.example.finalproject.login.LoginActivity
import com.example.finalproject.register.RegisterActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_welcome.*

@AndroidEntryPoint
class WelcomeActivity : BaseActivity<ActivityWelcomeBinding, WelcomeViewModel>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLayoutRes(R.layout.activity_welcome)

        //transparent status bar
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

    }

    override fun onClick(v: View?) {
        when(v){
            binding.btnSignIn -> openActivity<LoginActivity>()
            binding.btnSingUp -> openActivity<RegisterActivity>()
        }
        super.onClick(v)
    }
}