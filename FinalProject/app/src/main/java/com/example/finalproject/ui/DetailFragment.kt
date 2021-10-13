package com.example.finalproject.ui

import com.example.finalproject.R
import com.example.finalproject.adapters.DetailListAdapter
import com.example.finalproject.base.BaseFragment
import com.example.finalproject.databinding.FragmentDetailBinding
import com.example.finalproject.ui.weatherapp.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailFragment : BaseFragment<MainViewModel, FragmentDetailBinding>() {
    override val mViewModel: MainViewModel by viewModel()

    override fun getLayoutID(): Int = R.layout.fragment_detail

    override fun observeLiveData() {
        mViewModel.onForecastFetched.observe(this, {
            dataBinding.viewStateModel = it
            dataBinding.executePendingBindings()
            dataBinding.detailRV.adapter = DetailListAdapter(it.getHourlyList())
        })
    }

    override fun prepareView() {
        mViewModel.prepareForecast(arguments?.get("locationName").toString())
    }
}