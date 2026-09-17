package com.spikestats.app.data.remote

import com.spikestats.app.BuildConfig
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.Retrofit

private val json = Json {
    ignoreUnknownKeys = true
    isLenient = true
    explicitNulls = false
}

/** Every Riot API call authenticates with this single header. */
private val riotTokenInterceptor = Interceptor { chain ->
    val request = chain.request().newBuilder()
        .addHeader("X-Riot-Token", BuildConfig.RIOT_API_KEY)
        .build()
    chain.proceed(request)
}

private fun buildOkHttpClient(): OkHttpClient {
    val logging = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BASIC
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }
    return OkHttpClient.Builder()
        .addInterceptor(riotTokenInterceptor)
        .addInterceptor(logging)
        .build()
}

fun riotRetrofit(baseUrl: String): Retrofit =
    Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(buildOkHttpClient())
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
