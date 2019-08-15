package com.example.weatherApp.RestServices

import com.example.weatherApp.dataBase.CurrentWeather
import com.google.gson.Gson
import io.realm.Realm
import okhttp3.*
import java.io.IOException
import java.util.function.IntConsumer

class WeatherDataRequest {
    val API_KEY = "58be2a871e2354feae0f3ae5e39f99b3"
    val FORECAST_API_URL = "https://api.openweathermap.org/data/2.5/forecast?"
    val CURRENT_API_URL = "https://api.openweathermap.org/data/2.5/weather?"

    fun getWeatherDataFor5Day(locality: String?, consumer: IntConsumer) {
        val client = OkHttpClient.Builder().build()
        val request = Request.Builder()
            .url(
                FORECAST_API_URL
                    .plus("q=Makhachkala,ru")
                    .plus("&appid=")
                    .plus(API_KEY)
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
                    parseJson<DataParseClassFor5Day>(response.body!!.string())

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
                CURRENT_API_URL
                    .plus("q=Makhachkala,ru")
                    .plus("&appid=")
                    .plus(API_KEY)
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
            currentWeather.humidity = data.main.humidity
        }
        realm.commitTransaction()

    }
}