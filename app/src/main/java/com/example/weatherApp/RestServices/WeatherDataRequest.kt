package com.example.weatherApp.RestServices

import androidx.core.util.Consumer
import com.example.weatherApp.dataBase.CurrentWeather
import com.example.weatherApp.dataBase.DailyWeather
import com.example.weatherApp.dataBase.ForecastForDay
import com.example.weatherApp.dataBase.HourlyWeather
import com.google.gson.Gson
import io.realm.Realm
import io.realm.RealmList
import okhttp3.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashSet

class WeatherDataRequest {
    private val apiKey = "58be2a871e2354feae0f3ae5e39f99b3"
    private val forecastApiUrl = "https://api.openweathermap.org/data/2.5/forecast?"
    private val currentApiUrl = "https://api.openweathermap.org/data/2.5/weather?"

    fun getDailyWeather(locality: String?, requestStatusConsumer: Consumer<RequestStatus>) {
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
                requestStatusConsumer.accept(RequestStatus.ERROR)
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val result = parseJson<DataParseClassForDailyWeather>(response.body!!.string())
                    saveFiveDayWeather(result)
                    requestStatusConsumer.accept(RequestStatus.SUCCESSFULLY)
                } catch (e: Exception) {
                    requestStatusConsumer.accept(RequestStatus.ERROR)
                }
            }
        })
    }

    private inline fun <reified T> parseJson(json: String): T {
        val gson = Gson()
        return gson.fromJson(json, T::class.java)
    }

    fun getCurrentWeather(locality: String?, requestStatusConsumer: Consumer<RequestStatus>) {
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
                requestStatusConsumer.accept(RequestStatus.ERROR)
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val result = parseJson<DataParseClassForCurrentWeather>(response.body!!.string())
                    saveCurrentWeather(result)
                    requestStatusConsumer.accept(RequestStatus.SUCCESSFULLY)
                } catch (e: Exception) {
                    requestStatusConsumer.accept(RequestStatus.ERROR)
                }

            }
        })
    }

    private fun saveCurrentWeather(data: DataParseClassForCurrentWeather) {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        val currentWeather = realm.createObject(CurrentWeather::class.java)
        currentWeather.temp = data.main.temp
        currentWeather.cityName = data.name
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

    private fun saveFiveDayWeather(data: DataParseClassForDailyWeather) {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        val dailyWeather = realm.createObject(DailyWeather::class.java)
        dailyWeather.cityName = data.city.name
        dailyWeather.forecastForDay = parseForecastWeather(data.list)
        realm.commitTransaction()
    }

    private fun parseForecastWeather(hourlyWeather: List<DataParseClassForDailyWeather.List>): RealmList<ForecastForDay> {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)
        val realm = Realm.getDefaultInstance()
        val dateSet = HashSet<Date?>()
        for (item in hourlyWeather) {
            dateSet.add(sdf.parse(item.dt_txt))
        }

        val forecastForDayRealmList = RealmList<ForecastForDay>()
        for (date in dateSet) {
            if (date == null) {
                continue
            }
            val forecastForDayRealm = realm.createObject(ForecastForDay::class.java)
            forecastForDayRealm.date = date
            for (item in hourlyWeather) {
                if (date.compareTo(sdf.parse(item.dt_txt)) == 0) {
                    val hourlyWeatherRealm = realm.createObject(HourlyWeather::class.java)
                    if (!item.weather.isNullOrEmpty()) {
                        hourlyWeatherRealm.description = item.weather[0].description
                        hourlyWeatherRealm.icon = item.weather[0].icon
                    }
                    hourlyWeatherRealm.dt = item.dt
                    hourlyWeatherRealm.humidity = item.main.humidity
                    hourlyWeatherRealm.pressure = item.main.pressure
                    hourlyWeatherRealm.temp = item.main.temp
                    hourlyWeatherRealm.windSpeed = item.wind.speed
                    forecastForDayRealm.hourlyWeather.add(hourlyWeatherRealm)
                }
            }
            forecastForDayRealmList.add(forecastForDayRealm)
        }
        return forecastForDayRealmList
    }
}