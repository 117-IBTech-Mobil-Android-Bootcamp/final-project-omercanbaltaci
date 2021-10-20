package com.example.finalproject.util

import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.progressindicator.LinearProgressIndicator

fun Fragment.showToast(messageToShow: String) {
    Toast.makeText(requireContext(), messageToShow, Toast.LENGTH_SHORT).show()
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

// Smoothly filling up the linear progress indicator in 3000 ms
fun LinearProgressIndicator.smoothProgress(percent: Int) {
    val animation = ObjectAnimator.ofInt(this, "progress", percent)
    animation.duration = DELAY
    animation.interpolator = DecelerateInterpolator()
    animation.start()
}