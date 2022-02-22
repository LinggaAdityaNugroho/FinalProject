package com.example.finalproject.retrofit

import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.http.*

interface ApiEndPoint {
    @FormUrlEncoded
    @POST("login")
    fun postLogin(
        @Field("phone") phone: String?,
        @Field("password") password: String?
    ): Single<String>

    @FormUrlEncoded
    @POST("register")
    fun postRegister(
        @Field("phone") phone: String?,
        @Field("password") password: String?,
        @Field("name") name: String?
    ): Single<String>

    @Multipart
    @POST("update-profile")
    fun postUpdate(
        @Query("id") id: Int?,
        @Query("name") name: String?,
        @Query("school") school: String?,
        @Query("description") description: String?,
        @Part photo: MultipartBody.Part?
    ): Single<String>

    @FormUrlEncoded
    @POST("get-list-friends")
    fun getListFriend(
        @Field("users_id") id: Int?
    ): Single<String>

    @FormUrlEncoded
    @POST("like")
    fun postLike(
        @Field("users_id") id: Int?,
        @Field("user_id_i_like") user_id_i_like: Int?
    ): Single<String>

    @FormUrlEncoded
    @POST("update-profile")
    fun postUpdateNoImage(
        @Field("id") id: Int?,
        @Field("name") name: String?,
        @Field("school") school: String?,
        @Field("description") description: String?
    ): Single<String>

}