package com.example.finalproject.room

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class UserEntity(
    @PrimaryKey
    @Expose
    @SerializedName("id_room")
    val idRoom: Int?,
    @Expose
    @SerializedName("id")
    val id: Int?,
    @Expose
    @SerializedName("name")
    val name: String?,
    @Expose
    @SerializedName("school")
    val school: String?,
    @Expose
    @SerializedName("photo")
    val photo: String?,
    @Expose
    @SerializedName("description")
    val description: String?,
    @Expose
    @SerializedName("likes")
    val likes: Int?,
    @Expose
    @SerializedName("like_by_you")
    val likeByYou: Boolean?,
    @Expose
    @SerializedName("total_like")
    val totalLike: Int?
): Parcelable
