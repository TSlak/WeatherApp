package com.example.weatherApp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.weatherApp.RestServices.WeatherDataRequest
import com.example.weatherApp.dataBase.CurrentWeather
import com.example.weatherApp.dataBase.FiveDayWeather
import com.example.weatherApp.helper.getWeatherImageId
import io.realm.Realm
import io.realm.Sort
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.function.IntConsumer


class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {
    private val currentDaySdf: SimpleDateFormat = SimpleDateFormat("dd.MM", Locale.ROOT)
    private val currentTimeSdf: SimpleDateFormat = SimpleDateFormat("HH:mm", Locale.ROOT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Realm.init(this)
        swipeRefreshLayout.setOnRefreshListener(this)
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimaryDark, R.color.colorPrimary)
        showWeather()
        refreshWeatherData()
    }

    override fun onRefresh() {
        swipeRefreshLayout.setRefreshing(true)
        refreshWeatherData()
    }

    fun refreshWeatherData() {
        Toast.makeText(this, R.string.refresh_weather, Toast.LENGTH_LONG).show()
        val wfd = WeatherDataRequest()
        wfd.getCurrentWeather(null, IntConsumer { i ->
            this.runOnUiThread {
                if (i == 0) {
                    Toast.makeText(this, R.string.error_connected_server, Toast.LENGTH_LONG).show()
                }
                if (i == 1) {
                    showWeather()
                }
            }
        })

        wfd.getWeatherDataFor5Day(null, IntConsumer { i ->
            this.runOnUiThread {
                if (i == 0) {
                    Toast.makeText(this, R.string.error_connected_server, Toast.LENGTH_LONG).show()
                }
                if (i == 1) {
                    val realm = Realm.getDefaultInstance()
                    val e = realm.where(FiveDayWeather::class.java).findFirst()
                    if (e != null) {
                        val adapter = WeatherDayliDataAdapter(this, e.dailyWeather.sort("date"))
                        weatherRecyclerView.adapter = adapter
                        swipeRefreshLayout.isRefreshing = false
                    }
                }
            }
        })
    }

    private fun showWeather() {
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
                weatherImageView.setImageResource(getWeatherImageId(e.icon))
                localityTV.text = e.cityName
                updateDateTimeTV.text = resources.getString(R.string.last_update)
                    .plus(" ")
                    .plus(currentDaySdf.format(e.date))
                    .plus(" | ")
                    .plus(currentTimeSdf.format(e.date))
            }
        }
    }
}
