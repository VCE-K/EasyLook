package cn.vce.easylook.feature_music.api

import cn.vce.easylook.feature_music.models.bli.DynamicResponse
import com.cyl.musicapi.netease.PersonalizedInfo
import com.drake.net.utils.withIO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import retrofit2.http.Streaming
import retrofit2.http.Url


interface RankListService {

 /*   *//**
     * 获取推荐歌单
     *//*
    @GET("/personalized")
    fun personalizedPlaylist(): Call<PersonalizedInfo>
*/

    /**
     * 获取推荐歌单
     */
    @GET("/personalized")
    fun personalizedPlaylist(): Call<PersonalizedInfo>


    @Streaming
    @GET
    fun downloadMusic(@Header("RANGE") range: String?, @Url url: String): Call<ResponseBody>


}
