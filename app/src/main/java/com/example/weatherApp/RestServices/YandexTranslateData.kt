package com.example.weatherApp.RestServices

import com.google.gson.annotations.SerializedName

data class YandexTranslateData (
    @SerializedName("code") val code : Int,
    @SerializedName("lang") val lang : String,
    @SerializedName("text") val text : List<String>
)