package cn.vce.easylook.feature_music.presentation.music_search

import cn.vce.easylook.feature_music.api.MusicNetWork

object MusicSearchRepo {

    suspend fun searchMusic(key: String, limit: Int, page: Int) = MusicNetWork.searchMusic(key, limit, page)
}