package com.example.finalproject.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import com.example.finalproject.R
import com.example.finalproject.base.BaseListViewItemClickListener
import com.example.finalproject.databinding.AutocompleteListItemBinding
import com.example.finalproject.ui.weatherapp.model.Autocomplete

open class AutocompleteListAdapter(
    context: Context,
    list: List<Autocomplete>
) : ArrayAdapter<Autocomplete>(
    context,
    ViewHolder.LAYOUT,
    list
) {
    private var itemClickListener: BaseListViewItemClickListener<Autocomplete>? = null

    constructor(
        context: Context,
        list: List<Autocomplete>,
        itemClickListener: BaseListViewItemClickListener<Autocomplete>
    ) : this(context, list) {
        this.itemClickListener = itemClickListener
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return onBindView(parent, position)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return onBindView(parent, position)
    }

    private fun onBindView(parent: ViewGroup, position: Int): View {
        val autocomplete = getItem(position)
        val dataBinding: AutocompleteListItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            ViewHolder.LAYOUT,
            parent,
            false
        )
        dataBinding.autocomplete = autocomplete

        dataBinding.add.setOnClickListener {
            if (autocomplete != null) {
                itemClickListener!!.onItemClicked(autocomplete, it.id)
            }
        }
        dataBinding.info.setOnClickListener {
            if (autocomplete != null) {
                itemClickListener!!.onItemClicked(autocomplete, it.id)
            }
        }

        return dataBinding.root
    }

    private class ViewHolder {
        companion object {
            @LayoutRes
            val LAYOUT = R.layout.autocomplete_list_item
        }
    }
}
