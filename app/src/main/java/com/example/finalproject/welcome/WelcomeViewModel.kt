package com.example.finalproject.welcome

import com.example.finalproject.retrofit.ApiEndPoint
import com.example.finalproject.room.UserDao
import com.example.finalproject.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    apiEndPoint: ApiEndPoint,
    private val userDao: UserDao
) : BaseViewModel(apiEndPoint){
}