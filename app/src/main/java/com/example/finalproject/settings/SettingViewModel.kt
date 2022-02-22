package com.example.finalproject.settings

import com.example.finalproject.activity.BaseActivity
import com.example.finalproject.retrofit.ApiEndPoint
import com.example.finalproject.viewmodel.BaseViewModel
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val apiEndPoint: ApiEndPoint
): BaseViewModel(apiEndPoint) {
}