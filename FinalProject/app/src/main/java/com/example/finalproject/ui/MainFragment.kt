package com.example.finalproject.ui

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.finalproject.R
import com.example.finalproject.adapters.AutocompleteListAdapter
import com.example.finalproject.adapters.ViewPagerAdapter
import com.example.finalproject.base.BaseFragment
import com.example.finalproject.base.BaseViewItemClickListener
import com.example.finalproject.databinding.FragmentMainBinding
import com.example.finalproject.ui.weatherapp.model.Autocomplete
import com.example.finalproject.ui.weatherapp.model.ResultCurrent
import com.example.finalproject.ui.weatherapp.viewmodel.MainViewModel
import com.example.finalproject.util.showToast
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainFragment : BaseFragment<MainViewModel, FragmentMainBinding>() {
    private var weatherList = mutableListOf<ResultCurrent>()

    override val mViewModel: MainViewModel by viewModel()

    override fun getLayoutID() = R.layout.fragment_main

    override fun observeLiveData() {
        mViewModel.prepareCurrentsFromDB()

        mViewModel.onAutocompleteFetched.observe(this, {
            dataBinding.mainViewState = it
            dataBinding.executePendingBindings()
            dataBinding.autoComplete.setAdapter(
                AutocompleteListAdapter(
                    requireContext(),
                    it.getAutocompleteList(),
                    object : BaseViewItemClickListener<Autocomplete> {
                        override fun onItemClicked(clickedObject: Autocomplete, id: Int) {
                            when (id) {
                                R.id.autocompleteContainer -> {
                                    dataBinding.autoComplete.setText(clickedObject.name)
                                }
                                R.id.add -> {
                                    mViewModel.prepareResult(
                                        clickedObject.name.substring(
                                            0,
                                            clickedObject.name.indexOf(",")
                                        ),
                                        clickedObject.region,
                                        clickedObject.name
                                    )
                                }
                                R.id.info -> {
                                    goToDetailsWithBundle(clickedObject.name)
                                }
                            }
                        }
                    })
            )
        })

        mViewModel.onCurrentWeatherFetched.observe(this, {
            dataBinding.currentWeatherViewState = it
            dataBinding.executePendingBindings()
            weatherList.add(ResultCurrent(it.getCurrentLocation(), it.getCurrentWeather(), true))
            dataBinding.viewPager.adapter?.notifyItemInserted(weatherList.size - 1)
        })

        mViewModel.onForecastFetched.observe(this, {
            dataBinding.executePendingBindings()
            it.getForecast()
        })

        mViewModel.onSingleResultFetched.observe(this, {
            if (it) showToast("This location is already bookmarked.")
            else showToast("Location has been bookmarked.")
        })

        mViewModel.onResultDeleteFetched.observe(this, {
            if (it > 0) showToast("Location has been deleted.")
        })

        mViewModel.onNewCurrentWeatherFetched.observe(this, {
            val size = weatherList.size
            weatherList.clear()
            dataBinding.viewPager.adapter?.notifyItemRangeRemoved(0, size)
        })

        // Error observation
        mViewModel.onAutocompleteError.observe(this, {
            showToast(getString(R.string.error_occurred))
        })

        mViewModel.onCurrentWeatherError.observe(this, {
            showToast(getString(R.string.error_occurred))
        })

        mViewModel.onForecastError.observe(this, {
            showToast(getString(R.string.error_occurred))
        })
    }

    override fun prepareView() {
        dataBinding.viewPager.adapter = ViewPagerAdapter(
            weatherList,
            object : BaseViewItemClickListener<ResultCurrent> {
                override fun onItemClicked(clickedObject: ResultCurrent, id: Int) {
                    when (id) {
                        R.id.pagerBookmark -> {
                            mViewModel.deleteResult(
                                clickedObject.location.name,
                                clickedObject.location.region
                            )
                            val clickedIndex = weatherList.indexOf(clickedObject)
                            weatherList.removeAt(clickedIndex)
                            dataBinding.viewPager.adapter?.notifyItemRemoved(clickedIndex)
                        }
                        R.id.pagerDetails -> {
                            goToDetailsWithBundle(clickedObject.location.name)
                        }
                    }
                }
            })
        dataBinding.autoComplete.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0!!.length >= 3) mViewModel.prepareAutocomplete(p0.toString())
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    fun goToDetailsWithBundle(locationName: String) {
        val bundle = bundleOf("locationName" to locationName)
        findNavController().navigate(R.id.action_mainFragment_to_detailFragment, bundle)
    }
}