package cn.vce.easylook.feature_music.presentation.home_music.bli_music_list

import cn.vce.easylook.base.BaseRepository
import cn.vce.easylook.feature_music.api.MusicNetWork
import cn.vce.easylook.feature_music.models.bli.HotSong

object BliMusicListRepo: BaseRepository()  {

    /**
     * Bli_music
     */
    suspend fun getTrendingPlaylist(cateId: Int,
                                    page: Int,
                                    pageSize: Int,
                                    timeFrom: String,
                                    timeTo: String) = withIO {
        val trendingPlaylist = MusicNetWork.getTrendingPlaylist(cateId, page, pageSize, timeFrom, timeTo)
        val list = mutableListOf<HotSong>()
        if (0 == trendingPlaylist.code) {
            trendingPlaylist.hotSong?.let { list.addAll(it.toMutableList()) }
        }else{
            list
        }
        list
    }

    suspend fun getAvInfo(avId: Int) = MusicNetWork.getAvInfo(avId)


    suspend fun getDownloadInfo(avId: Int, cid: Int) = MusicNetWork.getDownloadInfo(avId, cid)


}