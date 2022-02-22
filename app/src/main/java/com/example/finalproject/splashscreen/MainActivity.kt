package com.example.finalproject.splashscreen

import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.crocodic.core.extension.openActivity
import com.example.finalproject.home.HomeActivity
import com.example.finalproject.R
import com.example.finalproject.welcome.WelcomeActivity
import com.example.finalproject.activity.BaseActivity
import com.example.finalproject.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLayoutRes(R.layout.activity_main)

        //fullscreen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        //transparent status bar
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        //cek login// isLogin diambil dari userDao
        Handler(mainLooper).postDelayed({
            viewModel.checkLogin { isLogin ->
                if (isLogin){
                    openActivity<HomeActivity>()
                }else{
                    openActivity<WelcomeActivity>()
                }
                finish()
            }
        }, 3000)
    }
}