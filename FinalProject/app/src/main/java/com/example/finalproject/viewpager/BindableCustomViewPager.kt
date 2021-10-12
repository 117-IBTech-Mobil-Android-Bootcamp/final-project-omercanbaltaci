package com.example.finalproject.viewpager

import com.example.finalproject.R
import com.example.finalproject.databinding.ViewpagerItemBinding
import com.example.finalproject.ui.weatherapp.model.Current
import com.example.finalproject.ui.weatherapp.model.CurrentWeatherViewStateModel
import com.example.finalproject.ui.weatherapp.model.Location

class BindableCustomViewPager(
    private val locationModel: Location,
    private val currentModel: Current,
    private val stateModel: CurrentWeatherViewStateModel
) :
    BindableViewPagerItem<ViewpagerItemBinding>() {
    override fun bind(viewDataBinding: ViewpagerItemBinding, position: Int) {
        viewDataBinding.location = locationModel
        viewDataBinding.current = currentModel
        viewDataBinding.stateModel = stateModel
    }

    override val layout = R.layout.viewpager_item
}