package cn.vce.easylook.feature_music.repository

import cn.vce.easylook.base.BaseRepository
import cn.vce.easylook.feature_music.api.MusicNetWork
import cn.vce.easylook.feature_music.db.MusicDatabase
import cn.vce.easylook.feature_music.models.MusicInfo
import cn.vce.easylook.feature_music.models.PlaylistInfo
import cn.vce.easylook.feature_music.models.PlaylistWithMusicInfo
import cn.vce.easylook.feature_music.models.bli.AvInfoResponse
import cn.vce.easylook.feature_music.models.bli.download.Audio
import cn.vce.easylook.feature_music.models.bli.download.DownloadInfoResponse
import kotlinx.coroutines.Dispatchers
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

    suspend fun getLyricInfo(mid: String) = MusicNetWork.getLyricInfo(mid = mid)
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
