package com.sommerval.sentimentali

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer
import se.akerfeldt.okhttp.signpost.SigningInterceptor
import java.util.concurrent.TimeUnit


object RetrofitClient {
    private var retrofit: Retrofit? = null

    //Define the base URL//
    private const val BASE_URL = "https://api.twitter.com/1.1/search/"

    //Create the Retrofit instance//
    fun getRetrofitInstance(context: Context): Retrofit? {
        val consumer = OkHttpOAuthConsumer(
            context.getString(R.string.key),
            context.getString(R.string.secret_key)
        )
        consumer.setTokenWithSecret(
            context.getString(R.string.token),
            context.getString(R.string.secret_token)
        )
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .connectTimeout(100, TimeUnit.SECONDS)
            .readTimeout(100, TimeUnit.SECONDS)
            .addInterceptor(SigningInterceptor(consumer))
            .addInterceptor(interceptor)
            .build()
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }
        return retrofit
    }
}