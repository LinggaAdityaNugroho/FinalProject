package com.example.finalproject.login

import androidx.lifecycle.viewModelScope
import com.crocodic.core.api.ApiCode
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.data.CoreSession
import com.crocodic.core.extension.toObject
import com.example.finalproject.constant.Const
import com.example.finalproject.retrofit.ApiEndPoint
import com.example.finalproject.room.UserDao
import com.example.finalproject.room.UserEntity
import com.example.finalproject.viewmodel.BaseViewModel
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val apiEndPoint: ApiEndPoint,
    private val userDao: UserDao,
    private val gson: Gson,
    private val session: CoreSession
) : BaseViewModel(apiEndPoint) {

    fun login(phone: String?, password: String?) = viewModelScope.launch {
        apiResponse.postValue(ApiResponse().responseLoading("Logging in..."))
        apiEndPoint.postLogin(phone, password)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribeWith(object : ApiObserver(true) {
                override fun onSuccess(t: String) {
                    val responseJson = JSONObject(t)

                    val apiStatus = responseJson.getInt(ApiCode.STATUS)
                    val apiMessage = responseJson.getString(ApiCode.MESSAGE)

                    if (apiStatus == ApiCode.SUCCESS) {
                        val user =
                            responseJson.getJSONObject(ApiCode.DATA).toObject<UserEntity>(gson)
                        android.util.Log.d("CheckResponse", "UserLogin : $t")

                        //save user
                        phone?.let { session.setValue(Const.LOGIN.PHONE, it) }
                        password?.let { session.setValue(Const.LOGIN.PASSWORD, it) }
                        saveUser(user)

                        apiResponse.postValue(ApiResponse().responseSuccess(apiMessage))
                    } else {
                        apiResponse.postValue(ApiResponse().responseWrong(apiMessage))
                    }

                }

                override fun onError(e: Throwable) {
                    apiResponse.postValue(ApiResponse().responseError(e))
                }
            })
    }

    //sharedPreference(save user)
    private fun saveUser(userEntity: UserEntity) = viewModelScope.launch {
        //userDao.insert(userEntity.copy(idRoom = 1))
        userDao.insertUser(userEntity.copy(idRoom = 1))
    }

}