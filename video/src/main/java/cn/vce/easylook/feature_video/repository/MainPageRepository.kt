package cn.vce.easylook.feature_video.repository

import androidx.lifecycle.liveData
import cn.vce.easylook.feature_video.api.MainPageNetWork
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class MainPageRepository{

    /*fun getDaily() = fire(Dispatchers.IO){
        val repoResponse = MainPageNetWork.getDaily()
        val repoItems = repoResponse.itemList
        if (repoItems != null){
            Result.success(repoItems)
        } else{
            Result.failure(RuntimeException("repoItems is $repoItems"))
        }
    }*/


    fun getDaily() = flow {
        val animeList = MainPageNetWork.getDaily()
        emit(animeList)
    }.flowOn(Dispatchers.IO)



    private fun <T> fire(context: CoroutineDispatcher, block: suspend() -> Result<T>) =
        liveData(Dispatchers.IO) {
            val result = try {
                block()
            }catch (e: Exception){
                Result.failure<T>(e)
            }
            emit(result)
        }
}
