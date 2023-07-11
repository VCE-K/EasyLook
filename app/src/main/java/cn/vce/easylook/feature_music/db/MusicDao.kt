package cn.vce.easylook.feature_music.db

import androidx.lifecycle.LiveData
import androidx.room.*
import cn.vce.easylook.feature_music.models.MusicInfo
import cn.vce.easylook.feature_music.models.PlaylistInfo
import cn.vce.easylook.feature_music.models.PlaylistWithMusicInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicDao {
    //集体性质查询
    @Transaction
    @Query("SELECT * FROM PlaylistInfo order by timestamp desc")
    fun getAllPlaylistWithMusicInfo(): Flow<List<PlaylistWithMusicInfo>>

    //歌单增删改查
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylistInfo(p: PlaylistInfo)

    @Query("SELECT * FROM PlaylistInfo")
    suspend fun getPlaylists(): List<PlaylistInfo>?

    @Query("SELECT * FROM PlaylistInfo WHERE id = :pid order by timestamp desc")
    suspend fun getPlaylist(pid: String): PlaylistInfo

    //音乐的增删改查
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMusicInfos(ms: List<MusicInfo>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMusicInfo(m: MusicInfo)

    @Query("SELECT * FROM MusicInfo where pid = :pid order by timestamp desc")
    fun getMusicInfos(pid: String): List<MusicInfo>?

    @Delete
    suspend fun deleteMusicInfo(m: MusicInfo)
}