package com.example.finalproject.ui

import android.text.Editable
import android.text.TextWatcher
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.finalproject.R
import com.example.finalproject.Result
import com.example.finalproject.adapters.AutocompleteListAdapter
import com.example.finalproject.adapters.ViewPagerAdapter
import com.example.finalproject.base.BaseFragment
import com.example.finalproject.base.BaseViewItemClickListener
import com.example.finalproject.databinding.FragmentMainBinding
import com.example.finalproject.ui.weatherapp.model.Autocomplete
import com.example.finalproject.ui.weatherapp.model.ResultCurrent
import com.example.finalproject.ui.weatherapp.viewmodel.MainViewModel
import com.example.finalproject.util.ConnectionLiveData
import com.example.finalproject.util.gone
import com.example.finalproject.util.showToast
import com.example.finalproject.util.visible
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainFragment : BaseFragment<MainViewModel, FragmentMainBinding>() {
    private var weatherList = mutableListOf<ResultCurrent>()
    private lateinit var cld: ConnectionLiveData

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
            dataBinding.notificationText.gone()
            dataBinding.viewPager.visible()
            weatherList.add(ResultCurrent(it.getCurrentLocation(), it.getCurrentWeather(), true))
            dataBinding.viewPager.adapter?.notifyItemInserted(weatherList.size - 1)
        })

        mViewModel.onSingleResultFetched.observe(this, {
            if (it) showToast("This location is already bookmarked.")
            else showToast("Location has been bookmarked.")
        })

        mViewModel.onResultDelete.observe(this, {
            if (it > 0) showToast("Location has been deleted.")
        })

        cld = ConnectionLiveData(requireActivity().application)
        cld.observe(requireActivity(), { isConnected ->
            when (isConnected) {
                true -> {
                    mViewModel.refreshCurrent.observe(this, {
                        when (it) {
                            is Result.Progress -> showToast("Trying to refresh locations.")
                            is Result.Success -> {
                                val size = weatherList.size
                                weatherList.clear()
                                dataBinding.viewPager.adapter?.notifyItemRangeRemoved(0, size)
                                mViewModel.prepareCurrentsFromDB()
                            }
                            is Result.Error -> showToast("No internet.")
                        }
                    })
                }
            }
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
                            if (weatherList.size == 0) {
                                dataBinding.notificationText.visible()
                                dataBinding.viewPager.gone()
                            }
                            dataBinding.viewPager.adapter?.notifyItemRemoved(clickedIndex)
                        }
                        R.id.pagerDetails -> {
                            goToDetailsWithBundle(clickedObject.location.name)
                        }
                    }
                }
            })

        if (weatherList.size > 0) {
            dataBinding.notificationText.gone()
            dataBinding.viewPager.visible()
        }

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