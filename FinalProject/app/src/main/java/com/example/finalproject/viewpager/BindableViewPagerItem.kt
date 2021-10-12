package com.example.finalproject.viewpager

import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BindableViewPagerItem<T : ViewDataBinding> : ViewPagerItem() {
    private var binding: T? = null

    override fun bind(view: View, position: Int) {
        if (binding == null) {
            binding = DataBindingUtil.bind(view)
        }
        binding?.let {
            bind(it, position)
        }
    }

    override fun unbind(view: View, position: Int) {
        binding = null
    }

    abstract fun bind(viewDataBinding: T, position: Int)
}