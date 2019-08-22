package com.example.weatherApp.RestServices

import com.example.weatherApp.dataBase.CurrentWeather
import com.example.weatherApp.dataBase.DailyWeather
import com.example.weatherApp.dataBase.FiveDayWeather
import com.example.weatherApp.dataBase.HourlyWeather
import com.google.gson.Gson
import io.realm.Realm
import io.realm.RealmList
import okhttp3.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.function.IntConsumer
import kotlin.collections.HashSet

class WeatherDataRequest {
    private val apiKey = "58be2a871e2354feae0f3ae5e39f99b3"
    private val forecastApiUrl = "https://api.openweathermap.org/data/2.5/forecast?"
    private val currentApiUrl = "https://api.openweathermap.org/data/2.5/weather?"

    fun getWeatherDataFor5Day(locality: String?, consumer: IntConsumer) {
        val client = OkHttpClient.Builder().build()
        val request = Request.Builder()
            .url(
                forecastApiUrl
                    .plus("q=Makhachkala,ru")
                    .plus("&appid=")
                    .plus(apiKey)
                    .plus("&units=metric")
                    .plus("&lang=ru")
            )
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                consumer.accept(0)
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val result = parseJson<DataParseClassFor5Day>(response.body!!.string())
                    saveFiveDayWeather(result)
                    consumer.accept(1)
                } catch (e: Exception) {
                }
            }
        })
    }

    private inline fun <reified T> parseJson(json: String): T {
        val gson = Gson()
        return gson.fromJson(json, T::class.java)
    }

    fun getCurrentWeather(locality: String?, consumer: IntConsumer) {
        val client = OkHttpClient.Builder().build()
        val request = Request.Builder()
            .url(
                currentApiUrl
                    .plus("q=Makhachkala,ru")
                    .plus("&appid=")
                    .plus(apiKey)
                    .plus("&units=metric")
                    .plus("&lang=ru")
            )
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                consumer.accept(0)
            }

            override fun onResponse(call: Call, response: Response) {
                val result = parseJson<DataParseClassForCurrentDay>(response.body!!.string())
                saveCurrentWeather(result)
                consumer.accept(1)
            }
        })
    }

    private fun saveCurrentWeather(data: DataParseClassForCurrentDay) {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        val currentWeather = realm.createObject(CurrentWeather::class.java)
        currentWeather.temp = data.main.temp
        currentWeather.maxTemp = data.main.temp_max
        currentWeather.minTemp = data.main.temp_min
        currentWeather.windSpeed = data.wind.speed
        currentWeather.sunset = data.sys.sunset
        currentWeather.sunrise = data.sys.sunrise
        if (!data.weather.isNullOrEmpty()) {
            currentWeather.description = data.weather[0].description
            currentWeather.icon = data.weather[0].icon
        }
        currentWeather.humidity = data.main.humidity
        currentWeather.pressure = data.main.pressure
        realm.commitTransaction()

    }

    private fun saveFiveDayWeather(data: DataParseClassFor5Day) {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        val fiveDayWeather = realm.createObject(FiveDayWeather::class.java)
        fiveDayWeather.cityName = data.city.name
        fiveDayWeather.dailyWeather = parseHourlyWeather(data.list)
        realm.commitTransaction()
    }

    private fun parseHourlyWeather(hourlyWeather: List<DataParseClassFor5Day.List>): RealmList<DailyWeather> {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)
        val realm = Realm.getDefaultInstance()
        val dateSet = HashSet<Date>()
        for (item in hourlyWeather) {
            dateSet.add(sdf.parse(item.dt_txt))
        }

        val result = RealmList<DailyWeather>()
        for (date in dateSet) {
            val dw = realm.createObject(DailyWeather::class.java)
            dw.date = date
            for (item in hourlyWeather) {
                if (date.compareTo(sdf.parse(item.dt_txt)) == 0) {
                    val hw = realm.createObject(HourlyWeather::class.java)
                    if (!item.weather.isNullOrEmpty()) {
                        hw.description = item.weather[0].description
                        hw.icon = item.weather[0].icon
                    }
                    hw.dt = item.dt
                    hw.humidity = item.main.humidity
                    hw.pressure = item.main.pressure
                    hw.temp = item.main.temp
                    hw.windSpeed = item.wind.speed
                    dw.hourlyWeatherList.add(hw)
                }
            }
            result.add(dw)
        }
        return result
    }
}