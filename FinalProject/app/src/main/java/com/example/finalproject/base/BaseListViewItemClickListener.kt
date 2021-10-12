package com.example.finalproject.base

import androidx.annotation.IdRes

interface BaseListViewItemClickListener<T> {
    fun onItemClicked(clickedObject: T, @IdRes id: Int = 0)
}