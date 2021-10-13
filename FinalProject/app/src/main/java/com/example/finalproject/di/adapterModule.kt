package com.example.finalproject.di

import com.example.finalproject.adapters.AutocompleteListAdapter
import org.koin.dsl.module

val adapterModule = module {
    factory { AutocompleteListAdapter(get(), get()) }
}