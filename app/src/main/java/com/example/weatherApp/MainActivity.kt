package com.example.weatherApp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.weatherApp.dataBase.CurrentWeather
import com.example.weatherApp.RestServices.WeatherDataRequest
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.function.IntConsumer
import io.realm.Realm


class MainActivity : AppCompatActivity() {
    val currentDaySdf: SimpleDateFormat = SimpleDateFormat("dd.MM.yyyy EEEE", Locale.ROOT)
    val currentTimeSdf: SimpleDateFormat = SimpleDateFormat("HH:mm", Locale.ROOT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Realm.init(this);
        setContentView(R.layout.activity_main)

        currentDayTV.text = currentDaySdf.format(Calendar.getInstance().time)
        currentTimeTV.text = currentTimeSdf.format(Calendar.getInstance().time)
    }

    fun refreshWeatherData(view: View) {
        val wfd = WeatherDataRequest()
        wfd.getCurrentWeather(null, IntConsumer { i ->
            this.runOnUiThread {
                if (i == 0) {
                    this.runOnUiThread {
                        Toast.makeText(this, "Ошибка соединения с сервером", Toast.LENGTH_LONG).show()
                    }
                }
                if (i == 1) {
                    updateWeather();
                }
            }

        })

    }


    private fun updateWeather() {
        this.runOnUiThread {
            val realm = Realm.getDefaultInstance()
            val e = realm.where(CurrentWeather::class.java).findFirst()
            if (e != null) {
                currentTempTV.text = e.temp.toString().plus("℃")
                maxTempTV.text = e.maxTemp.toString().plus("℃")
                minTempTV.text = e.minTemp.toString().plus("℃")
                windSpeedTV.text = e.windSpeed.toString().plus("m/h")
                sunriseTV.text = e.getSunriseTime()
                sunsetTV.text = e.getSunsetTime()
                humidity.text = e.humidity.toString().plus("%")
                val imageUrl = "http://openweathermap.org/img/wn/".plus(e.icon.plus("@2x.png"))
                Picasso.with(this).load(imageUrl).into(imageView)
            }
        }
    }
}
