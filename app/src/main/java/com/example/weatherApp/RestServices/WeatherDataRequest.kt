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
import kotlin.math.roundToInt

object WeatherDataRequest {
    private val apiKey = "58be2a871e2354feae0f3ae5e39f99b3"
    private val forecastApiUrl = "https://api.openweathermap.org/data/2.5/forecast?"
    private val currentApiUrl = "https://api.openweathermap.org/data/2.5/weather?"

    fun getDailyWeather(locality: String, requestStatusConsumer: Consumer<DailyWeather>) {
        val client = OkHttpClient.Builder().build()
        val request = Request.Builder()
            .url(
                forecastApiUrl
                    .plus("q=")
                    .plus(locality)
                    .plus("&appid=")
                    .plus(apiKey)
                    .plus("&units=metric")
                    .plus("&lang=ru")
            )
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                requestStatusConsumer.accept(null)
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val result = parseJson<DataParseClassForDailyWeather>(response.body!!.string())
                    requestStatusConsumer.accept(saveDailyWeather(result))
                } catch (e: Exception) {
                    requestStatusConsumer.accept(null)
                }
            }
        })
    }

    fun getDailyWeather(latitude: String, longitude: String, requestStatusConsumer: Consumer<DailyWeather>) {
        val client = OkHttpClient.Builder().build()
        val request = Request.Builder()
            .url(
                forecastApiUrl
                    .plus("lat=")
                    .plus(latitude)
                    .plus("&lon=")
                    .plus(longitude)
                    .plus("&appid=")
                    .plus(apiKey)
                    .plus("&units=metric")
                    .plus("&lang=ru")
            )
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                requestStatusConsumer.accept(null)
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val result = parseJson<DataParseClassForDailyWeather>(response.body!!.string())
                    requestStatusConsumer.accept(saveDailyWeather(result))
                } catch (e: Exception) {
                    requestStatusConsumer.accept(null)
                }
            }
        })
    }

    private inline fun <reified T> parseJson(json: String): T {
        val gson = Gson()
        return gson.fromJson(json, T::class.java)
    }

    fun getCurrentWeather(locality: String, currentWeather: Consumer<CurrentWeather>) {
        val client = OkHttpClient.Builder().build()
        val request = Request.Builder()
            .url(
                currentApiUrl
                    .plus("q=")
                    .plus(locality)
                    .plus("&appid=")
                    .plus(apiKey)
                    .plus("&units=metric")
                    .plus("&lang=ru")
            )
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                currentWeather.accept(null)
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val result = parseJson<DataParseClassForCurrentWeather>(response.body!!.string())
                    currentWeather.accept(saveCurrentWeather(result))
                } catch (e: NullPointerException) {
                    currentWeather.accept(null)
                }
            }
        })
    }

    fun getCurrentWeather(latitude: String, longitude: String, currentWeather: Consumer<CurrentWeather>) {
        val client = OkHttpClient.Builder().build()
        val request = Request.Builder()
            .url(
                currentApiUrl
                    .plus("lat=")
                    .plus(latitude)
                    .plus("&lon=")
                    .plus(longitude)
                    .plus("&appid=")
                    .plus(apiKey)
                    .plus("&units=metric")
                    .plus("&lang=ru")
            )
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                currentWeather.accept(null)
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val result = parseJson<DataParseClassForCurrentWeather>(response.body!!.string())
                    currentWeather.accept(saveCurrentWeather(result))
                } catch (e: NullPointerException) {
                    currentWeather.accept(null)
                }
            }
        })
    }

    private fun saveCurrentWeather(data: DataParseClassForCurrentWeather): CurrentWeather? {
        val currentWeather = CurrentWeather()
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
        currentWeather.pressure = data.main.pressure.roundToInt()
        return currentWeather
    }

    private fun saveDailyWeather(data: DataParseClassForDailyWeather): DailyWeather {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        val dailyWeather = realm.createObject(DailyWeather::class.java)
        dailyWeather.cityName = data.city.name
        dailyWeather.forecastForDay = parseForecastWeather(data.list)
        realm.commitTransaction()
        return realm.copyFromRealm(dailyWeather)
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