package cn.vce.easylook.feature_music.presentation.home_music.charts

import cn.vce.easylook.feature_music.api.MusicNetWork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

object ChartsRepo {
    /**
     * 加载Netease网易云歌单详情
     */
    fun getPlaylistDetail(
        pid: String
    ) =  flow {
        val playlistDetail = MusicNetWork.getPlaylistDetail(pid)
        emit(playlistDetail)
    }.flowOn(Dispatchers.IO)
}