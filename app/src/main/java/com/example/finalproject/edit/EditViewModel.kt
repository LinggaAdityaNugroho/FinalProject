package com.example.finalproject.edit

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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import javax.inject.Inject

@HiltViewModel
class EditViewModel @Inject constructor(
    private val apiEndPoint: ApiEndPoint,
    private val userDao: UserDao,
    private val gson: Gson,
    private val session: CoreSession
) : BaseViewModel(apiEndPoint) {

    val user = userDao.getUser()

    // TODO: revisi bang ini
    fun updateAdaGambare(name: String?, school: String?, description: String?, photo: File) =
        viewModelScope.launch(Dispatchers.IO) {

            apiResponse.postValue(ApiResponse().responseLoading("Updating..."))
            val id = userDao.getUserId()
            val fileBody = photo.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("photo", photo.name, fileBody)
            apiEndPoint.postUpdate(id, name, school, description, filePart)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(object : ApiObserver(true) {
                    override fun onSuccess(t: String) {
                        val responseJson = JSONObject(t)

                        val apiStatus = responseJson.getInt(ApiCode.STATUS)
                        val apiMessage = responseJson.getString(ApiCode.MESSAGE)

                        if (apiStatus == ApiCode.SUCCESS) {
                            //login
                            loginAgain()
                            android.util.Log.d("CekResponse", "UserUpdating : $t")
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


    fun updateNoImage(name: String?, school: String?, description: String?) =
        viewModelScope.launch(Dispatchers.IO) {

            apiResponse.postValue(ApiResponse().responseLoading("Updating..."))
            val id = userDao.getUserId()
            apiEndPoint.postUpdateNoImage(id, name, school, description)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(object : ApiObserver(true) {
                    override fun onSuccess(t: String) {
                        val responseJson = JSONObject(t)

                        val apiStatus = responseJson.getInt(ApiCode.STATUS)
                        val apiMessage = responseJson.getString(ApiCode.MESSAGE)

                        if (apiStatus == ApiCode.SUCCESS) {
                            //login
                            loginAgain()
                            android.util.Log.d("CekResponse", "UserUpdating : $t")
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

    private fun loginAgain() {
        viewModelScope.launch {
            val phone = session.getString(Const.LOGIN.PHONE)
            val password = session.getString(Const.LOGIN.PASSWORD)
            apiEndPoint.postLogin(phone, password)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(object : ApiObserver(true) {
                    override fun onSuccess(t: String) {
                        val responseJson = JSONObject(t)

                        val apiStatus = responseJson.getInt(ApiCode.STATUS)
                        val apiMessage = responseJson.getString(ApiCode.MESSAGE)

                        if (apiStatus == ApiCode.SUCCESS) {
                            val user = responseJson.getJSONObject(ApiCode.DATA).toObject<UserEntity>(gson)
                            android.util.Log.d("CheckResponse", "UserLogin : $t")
                            //save user
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
    }

    //saved function
    private fun saveUser(userEntity: UserEntity) = viewModelScope.launch(Dispatchers.IO) {
        userDao.updateUser(userEntity.copy(idRoom = 1))
    }
}