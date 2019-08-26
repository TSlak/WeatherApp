package com.example.weatherApp.helper

import io.realm.Realm
import io.realm.RealmObject
import java.util.*

object RealmHelper {
    fun commitOrUpdateObject(objectToCommit: RealmObject) {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        realm.insertOrUpdate(objectToCommit)
        realm.commitTransaction()
    }

    fun convertObjectToRealm(objectToConvert: RealmObject): RealmObject {
        val realm = Realm.getDefaultInstance()
        return realm.copyToRealm(objectToConvert)
    }

}