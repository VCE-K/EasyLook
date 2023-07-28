package cn.vce.easylook.feature_music.api

import cn.vce.easylook.feature_music.models.bli.DynamicResponse
import com.cyl.musicapi.netease.PersonalizedInfo
import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
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
    fun downloadMusic(@Url url: String? = "https://dldir1.qq.com/wework/work_weixin/wxwork_android_3.0.31.13637_100001.apk"): Call<ResponseBody>


}

