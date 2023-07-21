package cn.vce.easylook.feature_music.api

import cn.vce.easylook.feature_music.models.bli.DynamicResponse
import com.cyl.musicapi.netease.PersonalizedInfo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


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





}

