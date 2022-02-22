package com.example.finalproject.viewmodel

import androidx.lifecycle.viewModelScope
import com.crocodic.core.base.viewmodel.CoreViewModel
import com.example.finalproject.retrofit.ApiEndPoint
import kotlinx.coroutines.launch

open class BaseViewModel(private val apiEnPoint: ApiEndPoint) : CoreViewModel() {

    override fun apiLogout() = viewModelScope.launch { }

    override fun apiRenewToken() = viewModelScope.launch { }

}