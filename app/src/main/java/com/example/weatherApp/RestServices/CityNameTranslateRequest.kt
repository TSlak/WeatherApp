package com.example.weatherApp.RestServices

import androidx.core.util.Consumer
import com.example.weatherApp.dataBase.UserCity
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException


class CityNameTranslateRequest {
    private val apiKey = "trnsl.1.1.20190823T173824Z.2154c335d330dcd2.370e9283b41d7795105f681524c3fa46ff669f0b"
    private val apiUrl = "https://translate.yandex.net/api/v1.5/tr.json/translate?"

    fun getTranslateText(textToTranslate: String, resultTextConsumer: Consumer<UserCity>) {
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
                resultTextConsumer.accept(null)
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val result = parseJson<YandexTranslateData>(response.body!!.string())
                    resultTextConsumer.accept(parseUserCity(result, textToTranslate))
                } catch (e: Exception) {
                    resultTextConsumer.accept(null)
                }
            }
        })
    }

    private inline fun <reified T> parseJson(json: String): T {
        val gson = Gson()
        return gson.fromJson(json, T::class.java)
    }

    private fun parseUserCity(data: YandexTranslateData, textToTranslate: String): UserCity {
        val userCity = UserCity()
        userCity.cityNameEn = data.text[0]
        userCity.cityName = textToTranslate
        return userCity
    }
}