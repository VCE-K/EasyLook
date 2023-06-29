package cn.vce.easylook.feature_music.db

import androidx.room.*
import cn.vce.easylook.feature_music.models.MusicInfo
import cn.vce.easylook.feature_music.models.PlaylistInfo
import cn.vce.easylook.feature_music.models.PlaylistWithMusicInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicDao {
    @Transaction
    @Query("SELECT * FROM PlaylistInfo")
    fun getPlaylistWithMusicInfo(): Flow<List<PlaylistWithMusicInfo>>
    @Query("SELECT * FROM PlaylistInfo WHERE id = :pid")
    suspend fun getPlaylist(pid: String): PlaylistInfo

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylistInfo(p: PlaylistInfo)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMusicInfo(m: MusicInfo)

    @Delete
    suspend fun deleteMusicInfo(m: MusicInfo)
}