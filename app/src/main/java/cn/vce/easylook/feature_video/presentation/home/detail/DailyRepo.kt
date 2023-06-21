package cn.vce.easylook.feature_video.presentation.home.detail

import cn.vce.easylook.feature_video.api.MainPageNetWork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

object DailyRepo {

    fun getDaily() = flow {
        val animeList = MainPageNetWork.getDaily()
        emit(animeList)
    }.flowOn(Dispatchers.IO)
}