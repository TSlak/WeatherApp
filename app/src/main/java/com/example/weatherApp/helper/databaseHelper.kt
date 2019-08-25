package com.example.weatherApp.helper

import io.realm.Realm
import io.realm.RealmObject
import java.util.*

object RealmHelper {
    fun commitObject(objectToCommit: RealmObject) {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        realm.copyToRealm(objectToCommit)
        realm.commitTransaction()
    }

    fun convertObjectToRealm(objectToConvert: RealmObject): RealmObject {
        val realm = Realm.getDefaultInstance()
        return realm.copyToRealm(objectToConvert)
    }

}