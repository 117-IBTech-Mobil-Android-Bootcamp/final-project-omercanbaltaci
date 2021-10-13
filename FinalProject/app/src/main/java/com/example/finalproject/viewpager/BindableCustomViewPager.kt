package com.example.finalproject.viewpager

import com.example.finalproject.R
import com.example.finalproject.databinding.ViewpagerItemBinding
import com.example.finalproject.ui.weatherapp.model.Current
import com.example.finalproject.ui.weatherapp.model.CurrentWeatherViewStateModel
import com.example.finalproject.ui.weatherapp.model.Location
import com.example.finalproject.util.gone
import com.example.finalproject.util.visible

class BindableCustomViewPager(
    private val locationModel: Location,
    private val currentModel: Current,
    private val stateModel: CurrentWeatherViewStateModel
) : BaseBindableViewPagerItem<ViewpagerItemBinding>() {
    override fun bind(viewDataBinding: ViewpagerItemBinding, position: Int) {
        viewDataBinding.location = locationModel
        viewDataBinding.current = currentModel
        viewDataBinding.stateModel = stateModel

        viewDataBinding.toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    viewDataBinding.celcius.id -> {
                        viewDataBinding.temperatureC.visible()
                        viewDataBinding.feelsLikeTempC.visible()
                        viewDataBinding.temperatureF.gone()
                        viewDataBinding.feelsLikeTempF.gone()
                    }
                    else -> {
                        viewDataBinding.temperatureC.gone()
                        viewDataBinding.feelsLikeTempC.gone()
                        viewDataBinding.temperatureF.visible()
                        viewDataBinding.feelsLikeTempF.visible()
                    }
                }
            }
        }
    }

    override val layout = R.layout.viewpager_item
}