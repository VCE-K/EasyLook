package cn.vce.easylook.feature_music.data.data_source

import androidx.room.*
import cn.vce.easylook.feature_music.domain.entities.PlayListAndSongs
import cn.vce.easylook.feature_music.domain.entities.Playlist
import cn.vce.easylook.feature_music.domain.entities.Song
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicDao {

    @Query("SELECT * FROM Playlist WHERE pid = :pid")
    fun getPlaylist(pid: String): Flow<List<Playlist>>

    @Transaction
    @Query("SELECT * FROM Playlist WHERE pid = :pid")
    fun getPlaylistAndSongs(pid: String): Flow<List<PlayListAndSongs>>

    @Insert(onConflict = OnConflictStrategy.REPLACE) //替代
    suspend fun insertPlaylist(playlist: Playlist)

    @Query("SELECT * FROM song where playlistId = :playlistId and " +
            "(title LIKE '%' || :query || '%' OR" +
            " artistNames LIKE '%' || :query || '%' OR" +
            " subtitle LIKE '%' || :query || '%')")
    fun searchSongInPlaylist(playlistId: String, query: String): Flow<List<Song>>
}