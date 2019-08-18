package com.example.weatherApp.helper

import com.example.weatherApp.R

fun getWeatherImageId(icon: String): Int {
    when (icon) {
        "01n" -> return R.drawable.ic_moon
        "02n" -> return R.drawable.ic_moon_with_grey_cloud
        "03n", "03d" -> return R.drawable.ic_grey_cloud
        "04n", "04d" -> return R.drawable.ic_grey_clouds
        "09n", "09d" -> return R.drawable.ic_grey_clouds_shower_rain
        "10d" -> return R.drawable.ic_grey_cloud_with_small_sun_and_rain
        "10n" -> return R.drawable.ic_grey_cloud_with_moon_and_rain
        "11d", "11n" -> return R.drawable.ic_grey_cloud_lightning
        "13d", "13n" -> return R.drawable.ic_grey_cloud_snow
        "50d", "50n" -> return R.drawable.ic_fog
    }
    return R.drawable.ic_moon
}