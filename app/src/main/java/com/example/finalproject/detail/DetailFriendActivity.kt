package com.example.finalproject.detail

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.tos
import com.example.finalproject.R
import com.example.finalproject.activity.BaseActivity
import com.example.finalproject.constant.Const
import com.example.finalproject.databinding.ActivityDetailFriendBinding
import com.example.finalproject.room.UserEntity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFriendActivity : BaseActivity<ActivityDetailFriendBinding, DetailFriendViewModel>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLayoutRes(R.layout.activity_detail_friend)

        //get parcelable
        val friend: UserEntity? = intent.getParcelableExtra(Const.DFRIEND.DFriend)
        //set in data
        binding.friend = friend

        observe()

    }

    private fun observe() {
        viewModel.apiResponse.observe(this) {
            when (it.status) {
                ApiStatus.LOADING -> {
                    //it.message?.let { msg -> loadingDialog.show(msg) }
                }
                ApiStatus.SUCCESS -> {
                    //loadingDialog.dismiss()
                    viewModel.likeData.observe(this) { liked ->
                        Log.d("checkResponse", "LikedResponse : $liked")
                        when (liked.liked) {
                            true -> {
                                binding.btnFavBorder.setImageResource(R.drawable.ic_baseline_favorite)
                                tos("Berhasil Favorite")
                                setResult(4)
                            }
                            false -> {
                                binding.btnFavBorder.setImageResource(R.drawable.ic_baseline_favorite_border)
                                tos("Favorite dihapus")
                                setResult(4)
                            }
                            else -> {}
                        }
                    }
                }
                ApiStatus.WRONG -> {
                    //it.message?.let { msg -> loadingDialog.setResponse(msg) }
                }
                else -> {
                    //loadingDialog.dismiss()
                    tos(R.string.error)
                }
            }
        }
    }

    override fun onClick(v: View?) {

        when (v) {
            binding.btnFavBorder -> {
                viewModel.userAccount.observe(this) {
                    val friend: UserEntity? = intent.getParcelableExtra(Const.DFRIEND.DFriend)
                    viewModel.like(it?.id, friend?.id)
                }
            }
        }

        super.onClick(v)
    }

}
