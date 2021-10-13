package com.example.finalproject.ui

import android.text.Editable
import android.text.TextWatcher
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.finalproject.R
import com.example.finalproject.adapters.AutocompleteListAdapter
import com.example.finalproject.base.BaseFragment
import com.example.finalproject.base.BaseListViewItemClickListener
import com.example.finalproject.databinding.FragmentMainBinding
import com.example.finalproject.ui.weatherapp.model.Autocomplete
import com.example.finalproject.ui.weatherapp.viewmodel.MainViewModel
import com.example.finalproject.util.showToast
import com.example.finalproject.viewpager.BindableCustomViewPager
import com.example.finalproject.viewpager.CustomPagerAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainFragment : BaseFragment<MainViewModel, FragmentMainBinding>() {
    private var mList = mutableListOf<Autocomplete>()
    private var weatherList = mutableListOf<BindableCustomViewPager>()
    private var autocompleteAdapter: AutocompleteListAdapter? = null
    private var pagerAdapter: CustomPagerAdapter? = null

    override val mViewModel: MainViewModel by viewModel()

    override fun getLayoutID() = R.layout.fragment_main

    override fun observeLiveData() {
        mViewModel.onAutocompleteFetched.observe(this, {
            dataBinding.mainViewState = it
            dataBinding.executePendingBindings()
            mList.clear()
            mList.addAll(it.getAutocompleteList())
            autocompleteAdapter = AutocompleteListAdapter(
                requireContext(),
                mList,
                object : BaseListViewItemClickListener<Autocomplete> {
                    override fun onItemClicked(clickedObject: Autocomplete, id: Int) {
                        when (id) {
                            R.id.add -> {
                                mViewModel.prepareCurrentWeather(clickedObject.name)
                                showToast(clickedObject.name.plus(getString(R.string.bookmarked)))
                            }
                            R.id.info -> {
                                val bundle = bundleOf("locationName" to clickedObject.name)
                                findNavController().navigate(
                                    R.id.action_mainFragment_to_detailFragment,
                                    bundle
                                )
                            }
                        }
                    }
                })
            dataBinding.autoComplete.setAdapter(autocompleteAdapter)
        })
        mViewModel.onCurrentWeatherFetched.observe(this, {
            dataBinding.currentWeatherViewState = it
            dataBinding.executePendingBindings()
            weatherList.clear()
            weatherList.add(
                BindableCustomViewPager(
                    it.getCurrentLocation(),
                    it.getCurrentWeather(),
                    it
                )
            )
            pagerAdapter?.addAll(weatherList)
            dataBinding.viewPager.adapter = pagerAdapter
        })
        mViewModel.onForecastFetched.observe(this, {
            dataBinding.executePendingBindings()
            it.getForecast()
        })
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
        pagerAdapter = CustomPagerAdapter(requireContext())
        dataBinding.autoComplete.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0!!.length >= 3) mViewModel.prepareAutocomplete(p0.toString())
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }
}