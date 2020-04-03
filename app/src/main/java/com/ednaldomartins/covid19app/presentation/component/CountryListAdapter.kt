package com.ednaldomartins.covid19app.presentation.component

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ednaldomartins.covid19app.R
import com.ednaldomartins.covid19app.domain.entity.Country

class CountryListAdapter (
    private var context: Context?,
    private var countryList: List<Country>
)
    : RecyclerView.Adapter<CountryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.viewholder_country, parent, false)

        return CountryViewHolder(view)
    }

    override fun getItemCount(): Int = countryList.size

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        countryList[position].let{
            holder.mTextCountryName.text = it.country
            //  total
            holder.mTextCountryTotalConfirmed.text = ( "${it.totalConfirmed}" )
            holder.mTextCountryTotalDeaths.text = ( "${it.totalDeaths}" )
            holder.mTextCountryTotalRecovered.text = ( "${it.totalRecovered}" )
            //  new
            holder.mTextCountryNewConfirmed.text = ( "(+${it.newConfirmed})" )
            holder.mTextCountryNewDeaths.text = ( "(+${it.newDeaths})" )
            holder.mTextCountryNewRecovered.text = ( "(+${it.newRecovered})" )

            if (it.flagImage != "") {
                val imgUri = Uri.parse(it.flagImage)
                Glide.with(holder.mImageCountryFlag.context).load(imgUri).into(holder.mImageCountryFlag)
            }
            else {
                holder.mImageCountryFlag.setImageResource(R.drawable.ic_flag_64dp)
            }
        }
    }

}