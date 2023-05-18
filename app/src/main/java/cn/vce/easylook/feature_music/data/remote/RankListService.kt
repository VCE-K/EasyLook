package cn.vce.easylook.feature_music.data.remote

import cn.vce.easylook.feature_music.data.entities.TopListsResponse
import retrofit2.Call
import retrofit2.http.GET

interface RankListService {

    @GET("/getTopLists")
    fun getTopLists(): Call<TopListsResponse>
}

