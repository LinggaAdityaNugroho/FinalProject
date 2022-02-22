package com.example.finalproject.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.crocodic.core.api.ApiCode
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.extension.toList
import com.example.finalproject.retrofit.ApiEndPoint
import com.example.finalproject.room.UserDao
import com.example.finalproject.room.UserEntity
import com.example.finalproject.viewmodel.BaseViewModel
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val apiEndPoint: ApiEndPoint,
    private val userDao: UserDao,
    private val gson: Gson
) : BaseViewModel(apiEndPoint) {
    //get data user
    val user = userDao.getUser()

    val friends = MutableLiveData<List<UserEntity>>()

    fun list() = viewModelScope.launch(Dispatchers.IO) {
        apiResponse.postValue(ApiResponse().responseLoading())
        val userId = userDao.getUserId()
        apiEndPoint.getListFriend(userId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribeWith(object : ApiObserver(true) {
                override fun onSuccess(t: String) {
                    val responseJson = JSONObject(t)

                    val apiStatus = responseJson.getInt(ApiCode.STATUS)
                    val apiMessage = responseJson.getString(ApiCode.MESSAGE)
                    android.util.Log.d("MainActivity", "FriendData : $t")

                    if (apiStatus == ApiCode.SUCCESS) {
                        val user = responseJson.getJSONArray(ApiCode.DATA).toList<UserEntity>(gson)
                        friends.postValue(user)
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

}