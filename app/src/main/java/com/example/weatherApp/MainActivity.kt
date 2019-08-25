package com.example.weatherApp

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.Consumer
import androidx.core.view.GravityCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.weatherApp.RestServices.CityNameTranslateRequest
import com.example.weatherApp.RestServices.WeatherDataRequest
import com.example.weatherApp.adapters.DailyWeatherDataAdapter
import com.example.weatherApp.adapters.SavedCityDataAdapter
import com.example.weatherApp.dataBase.CurrentWeather
import com.example.weatherApp.dataBase.DailyWeather
import com.example.weatherApp.dataBase.UserCity
import com.example.weatherApp.helper.IconHelper
import com.example.weatherApp.helper.RealmHelper
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.weather_dayli_item.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {
    private val currentDaySdf: SimpleDateFormat = SimpleDateFormat("dd.MM", Locale.ROOT)
    private val currentTimeSdf: SimpleDateFormat = SimpleDateFormat("HH:mm", Locale.ROOT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Realm.init(this)
        swipeRefreshLayout.setOnRefreshListener(this)
//        showCurrentWeather()
        refreshCurrentWeatherData("Makhachkala")
//        refreshSavedCityWeather()
        initSavedCity()
    }

    private fun refreshSavedCityWeather() {
        val realm = Realm.getDefaultInstance()
        val cityList = realm.where(UserCity::class.java).findAll()
        cityList.forEach({
            initCityCurrentWeather(it)
        })
    }

    private fun initSavedCity() {
        this.runOnUiThread {
            val realm = Realm.getDefaultInstance()
            val cityList = realm.where(UserCity::class.java).findAll()
            val adapter = SavedCityDataAdapter(this, cityList)
            cityRecyclerView.adapter = adapter
            adapter.setOnItemClickListener(object : SavedCityDataAdapter.ClickListener {
                override fun onItemClick(position: Int, v: View) {
                    showCurrentWeather(cityList[position]!!.currentWeather!!)
                    showDailyWeather(cityList[position]!!.dailyWeather!!)
                    drawer_layout.closeDrawer(GravityCompat.START)
                }
            })
        }
    }

    override fun onRefresh() {
        swipeRefreshLayout.isRefreshing = true
        refreshCurrentWeatherData(localityTV.text.toString())
        refreshDailyWeatherData(localityTV.text.toString())
    }


    private fun refreshCurrentWeatherData(locality: String) {
        Toast.makeText(this, R.string.refresh_weather, Toast.LENGTH_LONG).show()
        WeatherDataRequest.getCurrentWeather(locality, Consumer { currentWeather ->
            this.runOnUiThread {
                if (currentWeather != null) {
                    showCurrentWeather(currentWeather)
                    RealmHelper.commitObject(currentWeather)
                } else {
                    Toast.makeText(this, R.string.error_connected_server, Toast.LENGTH_LONG).show()
                    swipeRefreshLayout.isRefreshing = false
                }
            }
        })


    }

    private fun refreshDailyWeatherData(locality: String) {
        WeatherDataRequest.getDailyWeather(locality, Consumer { dailyWeather ->
            this.runOnUiThread {
                if (dailyWeather != null) {
                    showDailyWeather(dailyWeather)
                } else {
                    Toast.makeText(this, R.string.error_connected_server, Toast.LENGTH_LONG).show()
                    swipeRefreshLayout.isRefreshing = false
                }
            }
        })
    }

    private fun refreshCurrentWeatherData(latitude: String, longitude: String) {
        Toast.makeText(this, R.string.refresh_weather, Toast.LENGTH_LONG).show()
        WeatherDataRequest.getCurrentWeather(latitude, longitude, Consumer { currentWeather ->
            this.runOnUiThread {
                if (currentWeather != null) {
                    showCurrentWeather(currentWeather)
                    RealmHelper.commitObject(currentWeather)
                } else {
                    Toast.makeText(this, R.string.error_connected_server, Toast.LENGTH_LONG).show()
                    swipeRefreshLayout.isRefreshing = false
                }
            }
        })

    }

    private fun refreshDailyWeatherData(latitude: String, longitude: String) {
        WeatherDataRequest.getDailyWeather(latitude, longitude, Consumer { dailyWeather ->
            this.runOnUiThread {
                if (dailyWeather != null) {
                    showDailyWeather(dailyWeather)
                    RealmHelper.commitObject(dailyWeather)
                } else {
                    Toast.makeText(this, R.string.error_connected_server, Toast.LENGTH_LONG).show()
                    swipeRefreshLayout.isRefreshing = false
                }
            }
        })
    }

    private fun test(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()

    }

    private fun showCurrentWeather(weather: CurrentWeather) {
        this.runOnUiThread {
            currentTempTV.text = weather.temp.roundToInt().toString()
                .plus(" ")
                .plus(resources.getString(R.string.celsius))
            windSpeedTV.text =
                weather.windSpeed.toString()
                    .plus(" ")
                    .plus(resources.getString(R.string.wind_speed_unit))
            sunriseTV.text = weather.getSunriseTime()
            sunsetTV.text = weather.getSunsetTime()
            humidityTV.text = weather.humidity.toString().plus("%")
            descriptionTV.text = weather.description
            pressureTV.text = weather.pressure.toString()
                .plus(" ")
                .plus(resources.getString(R.string.pressure_unit))
            weatherImageView.setImageResource(IconHelper.getWeatherImageId(weather.icon))
            localityTV.text = weather.cityName
            updateDateTimeTV.text = resources.getString(R.string.last_update)
                .plus(" ")
                .plus(currentDaySdf.format(weather.date))
                .plus(" | ")
                .plus(currentTimeSdf.format(weather.date))
        }
    }

    private fun showDailyWeather(dailyWeather: DailyWeather) {
        val adapter = DailyWeatherDataAdapter(this, dailyWeather.forecastForDay
            .toList()
            .sortedBy { forecastForDay -> forecastForDay.date })
        weatherRecyclerView.adapter = adapter
        adapter.setOnItemClickListener(object : DailyWeatherDataAdapter.ClickListener {
            override fun onItemClick(position: Int, v: View) {
                test(v.weekdayTV.text.toString())
            }
        })
        weatherRecyclerView.scheduleLayoutAnimation()
        swipeRefreshLayout.isRefreshing = false
    }

    fun saveUserCity(view: View) {
        hideKeyboardFrom(this)
        val cityName = cityNameET.text.toString()
        if (cityName.isEmpty()) {
            return
        }
        val realm = Realm.getDefaultInstance()
        val hasCity = realm.where(UserCity::class.java)
            .equalTo("cityNameEn", cityName)
            .or()
            .equalTo("cityName", cityName)
            .findFirst()
        if (hasCity != null) {
            Toast.makeText(this, "Данный город уже добавлен", Toast.LENGTH_LONG).show()
            return
        }
        val dataRequest = CityNameTranslateRequest()
        dataRequest.getTranslateText(cityNameET.text.toString(), Consumer { userCity ->
            this.runOnUiThread {
                if (userCity != null) {
                    initCityCurrentWeather(userCity)
                } else {
                    Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
                }
            }

        })
    }

    private fun initCityCurrentWeather(userCity: UserCity) {
        WeatherDataRequest.getCurrentWeather(userCity.cityNameEn, Consumer { currentWeather ->
            this.runOnUiThread {
                if (currentWeather != null) {
                    userCity.currentWeather = currentWeather
                    initCityDailyWeather(userCity)
                } else {
                    Toast.makeText(this, "Город не найден, попробуйте позднее или измените запрос", Toast.LENGTH_LONG)
                        .show()
                }
            }
        })
    }

    private fun initCityDailyWeather(userCity: UserCity) {
        WeatherDataRequest.getDailyWeather(userCity.cityNameEn, Consumer { dailyWeather ->
            this.runOnUiThread {
                if (dailyWeather != null) {
                    userCity.dailyWeather = dailyWeather
                    RealmHelper.commitObject(userCity)
                    initSavedCity()
                } else {
                    Toast.makeText(this, "Город не найден, попробуйте позднее или измените запрос", Toast.LENGTH_LONG)
                        .show()
                }
            }
        })
    }

    fun openMenu(view: View) {
        drawer_layout.openDrawer(nav_view, true)
    }

    private fun hideKeyboardFrom(activity: Activity) {
        val view = activity.currentFocus
        if (view != null) {
            val imm = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }


}
