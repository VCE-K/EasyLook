package cn.vce.easylook.feature_music.data.entities

import com.google.gson.annotations.SerializedName

data class TopListsResponse(val response: Response){

    data class Response(val code: Int, val data: Data)

    data class Data(val topList: List<TopList>)

    data class TopList(val id: Int, val picUrl: String, val songList: List<SongList>, val topTitle: String, val type: Int)

    data class SongList(@SerializedName("singername")val singerName: String, @SerializedName("songname") val songName: String)
}