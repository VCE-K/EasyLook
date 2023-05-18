package cn.vce.easylook.feature_music.data

import androidx.lifecycle.liveData
import cn.vce.easylook.feature_music.data.remote.MusicNetWork
import com.cyl.musicapi.BaseApiImpl
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

object Repository {

    fun getTopLists() = fire(Dispatchers.IO) {
        val topListsResponse = MusicNetWork.getTopLists()
        if( topListsResponse.response.code == 0) {
            Result.success(topListsResponse)
        }else {
            Result.failure(RuntimeException("response code is ${topListsResponse.response.code}"))
        }
    }

    /**
     * 加载Netease网易云歌单
     */
    suspend fun loadNeteaseTopList() = MusicNetWork.loadNeteaseTopList()


    /**
     * 加载Netease网易云歌单
     */
    suspend fun getPlaylistDetail(idx: String) = MusicNetWork.getPlaylistDetail(idx)


    /**
     * 获取歌曲url信息
     * @param br 音乐品质
     */
    suspend fun getMusicUrl(mid: String) = MusicNetWork.getMusicUrl(mid =  mid)

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
