package com.example.weatherApp

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.weatherApp.RestServices.WeatherDataRequest
import com.example.weatherApp.dataBase.CurrentWeather
import com.example.weatherApp.dataBase.FiveDayWeather
import com.example.weatherApp.helper.getWeatherImageId
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.function.IntConsumer


class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {
    private val currentDaySdf: SimpleDateFormat = SimpleDateFormat("dd.MM.yyyy EEEE", Locale.ROOT)
    private val currentTimeSdf: SimpleDateFormat = SimpleDateFormat("HH:mm", Locale.ROOT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Realm.init(this)
        swipeRefreshLayout.setOnRefreshListener(this)
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimaryDark, R.color.colorPrimary);
        refreshWeatherData(null)
    }

    override fun onRefresh() {
        swipeRefreshLayout.setRefreshing(true)
        refreshWeatherData(null)
    }

    fun refreshWeatherData(view: View?) {
        Toast.makeText(this, "Обновление погоды", Toast.LENGTH_LONG).show()
        val wfd = WeatherDataRequest()
        wfd.getCurrentWeather(null, IntConsumer { i ->
            this.runOnUiThread {
                if (i == 0) {
                    Toast.makeText(this, "Ошибка соединения с сервером дневной", Toast.LENGTH_LONG).show()
                }
                if (i == 1) {
                    updateWeather()
                }
            }
        })

        wfd.getWeatherDataFor5Day(null, IntConsumer { i ->
            this.runOnUiThread {
                if (i == 0) {
                    Toast.makeText(this, "Ошибка соединения с сервером почасовой", Toast.LENGTH_LONG).show()
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

    private fun updateWeather() {
        this.runOnUiThread {
            val realm = Realm.getDefaultInstance()
            val e = realm.where(CurrentWeather::class.java).findFirst()
            if (e != null) {
                currentTempTV.text = e.temp.toString().plus(" ℃")
                windSpeedTV.text = e.windSpeed.toString().plus(" m/s")
                sunriseTV.text = e.getSunriseTime()
                sunsetTV.text = e.getSunsetTime()
                humidityTV.text = e.humidity.toString().plus("%")
                descriptionTV.text = e.description
                pressureTV.text = e.pressure.toString().plus(" hpa")
                imageView.setImageResource(getWeatherImageId(e.icon))
            }
        }
    }
}
