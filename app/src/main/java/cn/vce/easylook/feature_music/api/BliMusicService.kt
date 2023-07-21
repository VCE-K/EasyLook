package cn.vce.easylook.feature_music.api

import cn.vce.easylook.feature_music.models.bli.AvInfoResponse
import cn.vce.easylook.feature_music.models.bli.DynamicResponse
import cn.vce.easylook.feature_music.models.bli.download.DownloadInfoResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface BliMusicService {

    /*&cate_id=28&page=1&pagesize=50&time_from=20190401&time_to=20190411*/
    @GET("https://s.search.bilibili.com/cate/search?main_ver=v3&search_type=video&view_type=hot_rank&order=click&jsonp=jsonp")
    fun getTrendingPlaylist(
        @Query("cate_id") cateId: Int?,
        @Query("page") page: Int?,
        @Query("pagesize") pageSize: Int?,
        @Query("time_from") timeFrom: String?,
        @Query("time_to") timeTo: String?
    ): Call<DynamicResponse>


    @GET("x/web-interface/view")
    fun getAvInfo(@Query("aid") avId: Int?): Call<AvInfoResponse>


    @GET("x/player/playurl?fnval=16&otype=json&platform=html5")
    fun getDownloadInfo(
        @Query("avid") avId: Int?,
        @Query("cid") cid: Int?
    ): Call<DownloadInfoResponse>
}