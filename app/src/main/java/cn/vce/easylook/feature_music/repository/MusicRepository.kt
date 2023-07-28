package cn.vce.easylook.feature_music.repository

import cn.vce.easylook.base.BaseRepository
import cn.vce.easylook.feature_music.api.MusicNetWork
import cn.vce.easylook.feature_music.db.MusicDatabase
import cn.vce.easylook.feature_music.models.MusicInfo
import cn.vce.easylook.feature_music.models.PlaylistInfo
import cn.vce.easylook.feature_music.models.PlaylistWithMusicInfo
import cn.vce.easylook.feature_music.other.LRUCacheLyric
import kotlinx.coroutines.flow.Flow

class MusicRepository(
    private val db: MusicDatabase
): BaseRepository(){

    suspend fun getAvInfo(avId: Int) = MusicNetWork.getAvInfo(avId)

    suspend fun getDownloadInfo(avId: Int, cid: Int) = MusicNetWork.getDownloadInfo(avId, cid)



    /**
     * 获取歌曲url信息
     * @param br 音乐品质
     */
    suspend fun getMusicUrl(mid: String) = MusicNetWork.getMusicUrl(mid = mid)

    suspend fun getLyricInfo(mid: String): String {
        //先去缓存进行查找
        return LRUCacheLyric.getInstance()[mid]?:MusicNetWork.getLyricInfo(mid = mid).also {
            //放入缓存
            LRUCacheLyric.getInstance().put(mid, it)
            val lrcText = it
            val array: Array<String> =
                lrcText.split("\\n".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
            val sb = StringBuffer()
            for (i in array.indices){
                var isFirst = false
                for (j in 0 until array[i].length){
                    val c = array[i][j]
                    if (!isFirst && c == ','){
                        isFirst = true
                        sb.append("]")
                    }else if (j == array[i].length-1 && c == ']'){
                        sb.append("\n")
                    }else{
                        sb.append(c)
                    }
                }
            }
            return sb.toString()
        }
    }
    //数据库操作
    fun getAllPlaylistWithMusicInfo(): Flow<List<PlaylistWithMusicInfo>> = db.musicDao.getAllPlaylistWithMusicInfo()

    suspend fun getMusicInfos(pid: String) = withIO {
        db.musicDao.getMusicInfos(pid)
    }

    suspend fun getPlaylist(pid: String): PlaylistInfo = withIO {
        db.musicDao.getPlaylist(pid)
    }
    suspend fun insertPlaylistInfo(p: PlaylistInfo) = withIO {
        db.musicDao.insertPlaylistInfo(p)
    }
    suspend fun insertMusicInfo(m: MusicInfo) = withIO {
        val musicInfoCopy = m.copy(timestamp = System.currentTimeMillis())
        db.musicDao.insertMusicInfo(musicInfoCopy)
    }
    suspend fun deleteMusicInfo(m: MusicInfo) = withIO {
        db.musicDao.deleteMusicInfo(m)
    }
}
