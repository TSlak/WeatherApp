package com.example.weatherApp.dataBase

import io.realm.RealmObject

open class UserCity : RealmObject() {
    var cityNameEn = ""
    var cityName = ""
    var currentWeatherTemp = 0
    var weatherIcon = ""
}