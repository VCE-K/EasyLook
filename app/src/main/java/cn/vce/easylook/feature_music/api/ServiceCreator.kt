package cn.vce.easylook.feature_music.api

import cn.vce.easylook.R
import cn.vce.easylook.utils.getString
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceCreator {

    private val BASE_URL: String = "http://${getString(R.string.ip)}:${getString(R.string.music_port)}"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)

    inline fun <reified T> create(): T = create(T::class.java)


    private val BLI_BASE_URL: String = getString(R.string.blibli_http)

    private val bliRetrofit = Retrofit.Builder()
        .baseUrl(BLI_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> createBli(serviceClass: Class<T>): T = bliRetrofit.create(serviceClass)

    inline fun <reified T> createBli(): T = createBli(T::class.java)
}