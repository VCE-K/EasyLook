package cn.vce.easylook.feature_music.presentation.home_music.personalized_playlist

import cn.vce.easylook.base.BaseRepository
import cn.vce.easylook.feature_music.api.MusicNetWork
import cn.vce.easylook.feature_music.models.PlaylistInfo
import cn.vce.easylook.feature_music.other.Resource

object PersonalizedPlaylistRepo: BaseRepository() {

/*    *//**
     * 推荐歌单
     *//*
    suspend fun personalizedPlaylist(): Resource<MutableList<PlaylistInfo>> {
        val personalizedResponse = MusicNetWork.personalizedPlaylist()
        return if(200 == personalizedResponse.code){
            val list = mutableListOf<PlaylistInfo>()
            personalizedResponse.result?.forEach {
                val playlistInfo = PlaylistInfo(it.id.toString(),
                    name = it.name,
                    description = it.copywriter,
                    cover = it.picUrl,
                    playCount = it.playCount.toLong())

                list.add(playlistInfo)
            }
            Resource.success(list)
        }else{
            Resource.error("personalizedPlaylist code is ${personalizedResponse.code}", null)
        }
    }*/

    /**
     * 推荐歌单
     */
    suspend fun personalizedPlaylist() = withIO {
        val personalizedResponse = MusicNetWork.personalizedPlaylist()
        val list = mutableListOf<PlaylistInfo>()
        if(200 == personalizedResponse.code){
            personalizedResponse.result?.forEach {
                val playlistInfo = PlaylistInfo(it.id.toString(),
                    name = it.name,
                    description = it.copywriter,
                    cover = it.picUrl,
                    playCount = it.playCount.toLong())

                list.add(playlistInfo)
            }
        }
        list
    }

}