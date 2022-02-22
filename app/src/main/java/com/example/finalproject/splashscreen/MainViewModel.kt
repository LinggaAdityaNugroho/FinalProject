package com.example.finalproject.splashscreen

import androidx.lifecycle.viewModelScope
import com.example.finalproject.retrofit.ApiEndPoint
import com.example.finalproject.room.UserDao
import com.example.finalproject.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    apiEndPoint: ApiEndPoint,
    private val userDao: UserDao
) : BaseViewModel(apiEndPoint) {

    fun checkLogin(result: (isLogin: Boolean) -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        result(userDao.isLogin())
    }

}