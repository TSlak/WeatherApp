package com.example.weatherApp.RestServices

import androidx.core.util.Consumer
import com.example.weatherApp.dataBase.UserCity
import com.google.gson.Gson
import io.realm.Realm
import okhttp3.*
import java.io.IOException


class CityNameTranslateRequest {
    private val apiKey = "trnsl.1.1.20190823T173824Z.2154c335d330dcd2.370e9283b41d7795105f681524c3fa46ff669f0b"
    private val apiUrl = "https://translate.yandex.net/api/v1.5/tr.json/translate?"

    fun getTranslateText(textToTranslate: String, resultTextConsumer: Consumer<RequestStatus>) {
        val client = OkHttpClient.Builder().build()
        val request = Request.Builder()
            .url(
                apiUrl
                    .plus("key=")
                    .plus(apiKey)
                    .plus("&text=")
                    .plus(textToTranslate)
                    .plus("&lang=en")
                    .plus("&format=plain")
                    .plus("&options=1")
            )
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                resultTextConsumer.accept(RequestStatus.ERROR)
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val result = parseJson<YandexTranslateData>(response.body!!.string())
                    saveCity(result)
                    resultTextConsumer.accept(RequestStatus.SUCCESSFULLY)
                } catch (e: Exception) {
                    resultTextConsumer.accept(RequestStatus.ERROR)
                }
            }
        })
    }

    private inline fun <reified T> parseJson(json: String): T {
        val gson = Gson()
        return gson.fromJson(json, T::class.java)
    }

    private fun saveCity(data: YandexTranslateData) {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        val dailyWeather = realm.createObject(UserCity::class.java)
        dailyWeather.cityName = data.text[0]
        realm.commitTransaction()
    }
}