package cn.vce.easylook.feature_music.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceCreator {

    private const val BASE_URL: String = "http://112.74.191.65:3000" //App.context.resources.getString(R.string.base_url)

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()



    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)

    inline fun <reified T> create(): T = create(T::class.java)


    private const val BLI_BASE_URL: String = "https://api.bilibili.com/" //App.context.resources.getString(R.string.base_url)

    private val bliRetrofit = Retrofit.Builder()
        .baseUrl(BLI_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> createBli(serviceClass: Class<T>): T = bliRetrofit.create(serviceClass)

    inline fun <reified T> createBli(): T = createBli(T::class.java)
}