package com.example.weatherApp.helper

import io.realm.Realm
import io.realm.RealmObject

object RealmHelper {
    fun commitObject(`object`: RealmObject) {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        val objectRealm = realm.copyToRealm(`object`)
        realm.commitTransaction()
    }
}