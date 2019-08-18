package com.example.weatherApp

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherApp.RestServices.WeatherDataRequest
import com.example.weatherApp.dataBase.CurrentWeather
import com.example.weatherApp.dataBase.FiveDayWeather
import com.example.weatherApp.helper.getWeatherImageId
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.function.IntConsumer


class MainActivity : AppCompatActivity() {
    val currentDaySdf: SimpleDateFormat = SimpleDateFormat("dd.MM.yyyy EEEE", Locale.ROOT)
    val currentTimeSdf: SimpleDateFormat = SimpleDateFormat("HH:mm", Locale.ROOT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Realm.init(this);
        setContentView(R.layout.activity_main)

        currentDayTV.text = currentDaySdf.format(Calendar.getInstance().time)
        currentTimeTV.text = currentTimeSdf.format(Calendar.getInstance().time)
        refreshWeatherData(null)

    }

    fun refreshWeatherData(view: View?) {
        val wfd = WeatherDataRequest()
        wfd.getCurrentWeather(null, IntConsumer { i ->
            this.runOnUiThread {
                if (i == 0) {
                    Toast.makeText(this, "Ошибка соединения с сервером", Toast.LENGTH_LONG).show()
                }

                if (i == 1) {
                    updateWeather();
                }
            }
        })

        wfd.getWeatherDataFor5Day(null, IntConsumer { i ->
            this.runOnUiThread {
                if (i == 0) {
                    Toast.makeText(this, "Ошибка соединения с сервером", Toast.LENGTH_LONG).show()
                }
                if (i == 1) {
                    val realm = Realm.getDefaultInstance()
                    val e = realm.where(FiveDayWeather::class.java).findFirst()
                    if (e != null) {
                        val adapter = WeatherDayliDataAdapter(this, e.list)
                        weatherRecyclerView.setAdapter(adapter)
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
                windSpeedTV.text = e.windSpeed.toString().plus(" м/с")
                sunriseTV.text = e.getSunriseTime()
                sunsetTV.text = e.getSunsetTime()
                humidityTV.text = e.humidity.toString().plus("%")
                descriptionTV.text = e.description
                pressureTV.text = e.pressure.toString().plus(" мбар")
                imageView.setImageResource(getWeatherImageId(e.icon))
            }
        }
    }
}
