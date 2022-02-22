package com.example.finalproject.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.crocodic.core.api.ApiCode
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.data.CoreSession
import com.crocodic.core.extension.toObject
import com.example.finalproject.model.LikeResponse
import com.example.finalproject.retrofit.ApiEndPoint
import com.example.finalproject.room.UserDao
import com.example.finalproject.viewmodel.BaseViewModel
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class DetailFriendViewModel @Inject constructor(
    private val apiEndPoint: ApiEndPoint,
    private val userDao: UserDao,
    private val gson: Gson,
    private val session: CoreSession
) : BaseViewModel(apiEndPoint) {
    val userAccount = userDao.getUser()
    val likeData = MutableLiveData<LikeResponse>()

    fun like(id: Int?, id_i_like: Int?) = viewModelScope.launch {
        android.util.Log.d("MainActivity", "cekId: $id, cekIdiLike: $id_i_like")
        apiResponse.postValue(ApiResponse().responseLoading("Like"))
        apiEndPoint.postLike(id, id_i_like)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribeWith(object : ApiObserver(true) {
                override fun onSuccess(t: String) {
                    val responseJson = JSONObject(t)

                    val apiStatus = responseJson.getInt(ApiCode.STATUS)
                    val apiMessage = responseJson.getString(ApiCode.MESSAGE)
                    android.util.Log.d("MainActivity", "Like : $t")

                    if (apiStatus == ApiCode.SUCCESS) {

                        val like = responseJson.toObject<LikeResponse>(gson)
                        likeData.postValue(like)

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