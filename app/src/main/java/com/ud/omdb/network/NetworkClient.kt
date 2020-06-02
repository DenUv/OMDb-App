package com.ud.omdb.network

import android.content.Context
import com.google.gson.GsonBuilder
import com.ud.omdb.R
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class NetworkClient(private val context: Context) {

    private lateinit var retrofitInstance: Retrofit
    private lateinit var httpClient: OkHttpClient

    private val connectionTimeout: Long = 30
    private val readTimeout: Long = 30

    init {
        buildRetrofitClient()
    }

    private fun buildRetrofitClient() {
        val gson =
            GsonBuilder().create()

        httpClient = configureHttpClient()
        retrofitInstance = Retrofit.Builder()
            .baseUrl(context.getString(R.string.url))
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(httpClient)
            .build()
    }

    private fun configureHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient().newBuilder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(connectionTimeout, TimeUnit.SECONDS)
            .readTimeout(readTimeout, TimeUnit.SECONDS)
            .build()
    }

    fun <T> createService(serviceClass: Class<T>): T {
        return retrofitInstance.create(serviceClass)
    }

}