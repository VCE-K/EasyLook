package cn.vce.easylook.feature_video.presentation.home

import cn.vce.easylook.feature_video.api.MainPageNetWork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

object VideoHomeRepo {

    fun getAllRec(page: String) = flow {
        val animeList = MainPageNetWork.getAllRec(page)
        emit(animeList)
    }.flowOn(Dispatchers.IO)
}