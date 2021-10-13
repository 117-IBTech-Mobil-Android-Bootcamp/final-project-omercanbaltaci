package com.example.finalproject.util

import android.net.Uri
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

@BindingAdapter("image")
fun setImageUrl(imageView: ImageView, url: String?) {
    Glide.with(imageView.context)
        .load(Uri.parse(url))
        .into(imageView)
}

@BindingAdapter("degreeWithSymbol")
fun setDegreeWithSymbol(textView: TextView, text: String) {
    textView.text = text.plus("°")
}