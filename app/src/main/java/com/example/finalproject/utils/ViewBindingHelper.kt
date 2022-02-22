package com.example.finalproject.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.finalproject.R
import com.google.gson.annotations.SerializedName

class ViewBindingHelper {
    companion object {
        @JvmStatic
        @BindingAdapter(value = ["imageUrl"], requireAll = false)
        fun loadImage(view: ImageView, imageUrl: String?) {

            imageUrl?.let {

                Glide
                    .with(view.context)
                    .load(imageUrl)
                    .override(322, 322)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(view)
            }

        }
    }
}