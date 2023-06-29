package cn.vce.easylook.feature_music.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import cn.vce.easylook.base.BaseRepository
import cn.vce.easylook.feature_music.api.MusicNetWork
import cn.vce.easylook.feature_music.db.MusicDatabase
import cn.vce.easylook.feature_music.models.ArtistSongs
import cn.vce.easylook.feature_music.models.MusicInfo
import cn.vce.easylook.feature_music.models.PlaylistInfo
import cn.vce.easylook.feature_music.models.PlaylistWithMusicInfo
import cn.vce.easylook.feature_music.other.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class MusicRepository(
    private val db: MusicDatabase
): BaseRepository(){

    /*fun getTopLists() = fire(Dispatchers.IO) {
        val topListsResponse = MusicNetWork.getTopLists()
        if( topListsResponse.response.code == 0) {
            Result.success(topListsResponse)
        }else {
            Result.failure(RuntimeException("response code is ${topListsResponse.response.code}"))
        }
    }
*/
    /**
     * 加载Netease网易云歌单
     */
    suspend fun loadNeteaseTopList() = MusicNetWork.loadNeteaseTopList()


    /**
     * 加载Netease网易云歌单详情
     */
    fun getPlaylistDetail(pid: String) = fire(Dispatchers.IO){
        val  artistSongs: ArtistSongs = MusicNetWork.getPlaylistDetail(pid)
        if (artistSongs != null){
            Result.success(artistSongs)
        } else{
            Result.failure(RuntimeException("artistSongs is $artistSongs"))
        }
    }

    suspend fun searchMusic(key: String, limit: Int, page: Int) = MusicNetWork.searchMusic(key, limit, page)

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



    //数据库操作
    fun getPlaylistWithMusicInfo(): Flow<List<PlaylistWithMusicInfo>> = db.musicDao.getPlaylistWithMusicInfo()
    suspend fun getPlaylist(pid: String): PlaylistInfo = withIO {
        db.musicDao.getPlaylist(pid)
    }
    suspend fun insertPlaylistInfo(p: PlaylistInfo) = withIO {
        db.musicDao.insertPlaylistInfo(p)
    }
    suspend fun insertMusicInfo(m: MusicInfo) = withIO {
        db.musicDao.insertMusicInfo(m)
    }

    suspend fun deleteMusicInfo(m: MusicInfo) = withIO {
        db.musicDao.deleteMusicInfo(m)
    }
}
