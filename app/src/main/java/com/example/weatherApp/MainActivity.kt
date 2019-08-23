package com.example.weatherApp

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.Consumer
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.weatherApp.RestServices.CityNameTranslateRequest
import com.example.weatherApp.RestServices.RequestStatus
import com.example.weatherApp.RestServices.WeatherDataRequest
import com.example.weatherApp.adapters.SavedCityDataAdapter
import com.example.weatherApp.adapters.WeatherDailyDataAdapter
import com.example.weatherApp.dataBase.CurrentWeather
import com.example.weatherApp.dataBase.DailyWeather
import com.example.weatherApp.dataBase.UserCity
import com.example.weatherApp.helper.IconHelper
import io.realm.Realm
import io.realm.Sort
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.city_saved_item.view.*
import kotlinx.android.synthetic.main.weather_dayli_item.view.*
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {
    private val currentDaySdf: SimpleDateFormat = SimpleDateFormat("dd.MM", Locale.ROOT)
    private val currentTimeSdf: SimpleDateFormat = SimpleDateFormat("HH:mm", Locale.ROOT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Realm.init(this)
        swipeRefreshLayout.setOnRefreshListener(this)
        showCurrentWeather()
        refreshWeatherData()
        initSavedCity()
    }

    private fun initSavedCity() {
        this.runOnUiThread {
            val realm = Realm.getDefaultInstance()
            val e = realm.where(UserCity::class.java).findAll()
            val adapter = SavedCityDataAdapter(this, e)
            cityRecyclerView.adapter = adapter
            adapter.setOnItemClickListener(object : SavedCityDataAdapter.ClickListener {
                override fun onItemClick(position: Int, v: View) {
                    test(v.cityNameTV.text.toString())
                }
            })
        }

    }

    override fun onRefresh() {
        swipeRefreshLayout.isRefreshing = true
        refreshWeatherData()
    }


    private fun refreshWeatherData() {
        Toast.makeText(this, R.string.refresh_weather, Toast.LENGTH_LONG).show()
        val dataRequest = WeatherDataRequest()
        dataRequest.getCurrentWeather(null, Consumer { requestStatus ->
            this.runOnUiThread {
                when (requestStatus) {
                    RequestStatus.ERROR -> {
                        Toast.makeText(this, R.string.error_connected_server, Toast.LENGTH_LONG).show()
                        swipeRefreshLayout.isRefreshing = false
                    }
                    RequestStatus.SUCCESSFULLY -> {
                        showCurrentWeather()
                    }
                    null -> {
                    }

                }
            }
        })

        dataRequest.getDailyWeather(null, Consumer { requestStatus ->
            this.runOnUiThread {
                when (requestStatus) {
                    null -> {
                    }
                    RequestStatus.ERROR -> {
                        Toast.makeText(this, R.string.error_connected_server, Toast.LENGTH_LONG).show()
                        swipeRefreshLayout.isRefreshing = false
                    }
                    RequestStatus.SUCCESSFULLY -> {
                        showDailyWeather()
                    }
                }
            }
        })
    }

    private fun test(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
        drawer_layout.closeDrawer(GravityCompat.START)
    }

    private fun showCurrentWeather() {
        this.runOnUiThread {
            val realm = Realm.getDefaultInstance()
            val e = realm.where(CurrentWeather::class.java)
                .sort("date", Sort.DESCENDING)
                .findFirst()
            if (e != null) {
                currentTempTV.text = e.temp.toString().plus(" ").plus(resources.getString(R.string.celsius))
                windSpeedTV.text = e.windSpeed.toString().plus(" ").plus(resources.getString(R.string.wind_speed_unit))
                sunriseTV.text = e.getSunriseTime()
                sunsetTV.text = e.getSunsetTime()
                humidityTV.text = e.humidity.toString().plus("%")
                descriptionTV.text = e.description
                pressureTV.text = e.pressure.toString().plus(" ").plus(resources.getString(R.string.pressure_unit))
                weatherImageView.setImageResource(IconHelper.getWeatherImageId(e.icon))
                localityTV.text = e.cityName
                updateDateTimeTV.text = resources.getString(R.string.last_update)
                    .plus(" ")
                    .plus(currentDaySdf.format(e.date))
                    .plus(" | ")
                    .plus(currentTimeSdf.format(e.date))
            }
        }
    }

    private fun showDailyWeather() {
        val realm = Realm.getDefaultInstance()
        val e = realm.where(DailyWeather::class.java).findFirst()
        if (e != null) {
            val adapter = WeatherDailyDataAdapter(this, e.forecastForDay.sort("date"))
            weatherRecyclerView.adapter = adapter
            adapter.setOnItemClickListener(object : WeatherDailyDataAdapter.ClickListener {
                override fun onItemClick(position: Int, v: View) {
                    test(v.weekdayTV.text.toString())
                }
            })

            weatherRecyclerView.scheduleLayoutAnimation()
            swipeRefreshLayout.isRefreshing = false
        }
    }

    fun testCitySave(view: View) {
        val dataRequest = CityNameTranslateRequest()
        dataRequest.getTranslateText(cityNameET.text.toString(), Consumer { requestStatus ->
            when (requestStatus) {
                RequestStatus.SUCCESSFULLY -> initSavedCity()
                RequestStatus.ERROR, null -> this.runOnUiThread({
                    Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
                })
            }
        })
    }

    fun openMenu(view: View) {
        drawer_layout.openDrawer(nav_view, true)
    }


}
