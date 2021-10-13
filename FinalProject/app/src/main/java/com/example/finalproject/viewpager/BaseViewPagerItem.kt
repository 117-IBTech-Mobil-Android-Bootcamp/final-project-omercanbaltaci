package com.example.finalproject.viewpager

import android.view.View

abstract class BaseViewPagerItem {
    abstract val layout: Int
    abstract fun bind(view: View, position: Int)
    open fun unbind(view: View, position: Int) {}
}