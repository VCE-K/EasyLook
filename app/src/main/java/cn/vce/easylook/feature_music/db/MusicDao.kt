package cn.vce.easylook.feature_music.db

import androidx.room.*
import cn.vce.easylook.feature_music.models.PlaylistInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicDao {

    @Query("SELECT * FROM PlaylistInfo WHERE id = :pid")
    fun getPlaylist(pid: String): PlaylistInfo

    @Query("SELECT * FROM PlaylistInfo")
    fun getPlaylists(): Flow<List<PlaylistInfo>>
}