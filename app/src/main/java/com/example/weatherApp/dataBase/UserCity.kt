package com.example.weatherApp.dataBase

import io.realm.RealmObject

open class UserCity : RealmObject() {
    var cityNameEn = ""
    var cityName = ""
    var currentWeather: CurrentWeather? = CurrentWeather()
    var dailyWeather: DailyWeather? = DailyWeather()
}