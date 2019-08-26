package com.example.weatherApp.dataBase

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class UserCity : RealmObject() {
    @PrimaryKey
    var cityNameEn = ""
    var cityName = ""
    var currentWeather: CurrentWeather? = CurrentWeather()
    var dailyWeather: DailyWeather? = DailyWeather()
}