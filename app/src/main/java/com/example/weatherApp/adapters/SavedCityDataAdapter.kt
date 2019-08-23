package com.example.weatherApp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherApp.R
import com.example.weatherApp.dataBase.UserCity

class SavedCityDataAdapter(context: Context, var userCityList: List<UserCity>) :
    RecyclerView.Adapter<SavedCityDataAdapter.ViewHolder>() {
    private var clickListener: ClickListener? = null
    var inflater = LayoutInflater.from(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.city_saved_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val savedCity = userCityList[position]
        holder.savedCityTV.text = savedCity.cityName
    }

    override fun getItemCount(): Int {
        return userCityList.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        init {
            view.setOnClickListener(this)
        }

        val savedCityTV: TextView = view.findViewById(R.id.cityNameTV)


        override fun onClick(v: View) {
            clickListener!!.onItemClick(adapterPosition, v)
        }
    }

    fun setOnItemClickListener(clickListener: ClickListener) {
        this.clickListener = clickListener
    }

    interface ClickListener {
        fun onItemClick(position: Int, v: View)
    }
}