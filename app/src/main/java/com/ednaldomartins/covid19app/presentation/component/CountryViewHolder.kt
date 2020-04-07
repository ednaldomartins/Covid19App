package com.ednaldomartins.covid19app.presentation.component

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ednaldomartins.covid19app.R

class CountryViewHolder (
    view: View,
    var onClickCardView: OnCardViewClickListener? = null
) : RecyclerView.ViewHolder(view), View.OnClickListener {

    var slugName: String = ""

    var mTextCountryName: TextView = view.findViewById(R.id.viewholder_country_name)
    var mTextCountryTotalConfirmed: TextView = view.findViewById(R.id.viewholder_country_total_confirmed)
    var mTextCountryTotalDeaths: TextView = view.findViewById(R.id.viewholder_country_total_deaths)
    var mTextCountryTotalRecovered: TextView = view.findViewById(R.id.viewholder_country_total_recovered)
    var mTextCountryNewConfirmed: TextView = view.findViewById(R.id.viewholder_country_new_confirmed)
    var mTextCountryNewDeaths: TextView = view.findViewById(R.id.viewholder_country_new_deaths)
    var mTextCountryNewRecovered: TextView = view.findViewById(R.id.viewholder_country_new_recovered)
    var mImageCountryFlag: ImageView = view.findViewById(R.id.viewholder_country_flag)

    init {
        view.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        onClickCardView?.onCountryClick( slugName )
    }

    interface OnCardViewClickListener{ fun onCountryClick(countryName: String) }

}