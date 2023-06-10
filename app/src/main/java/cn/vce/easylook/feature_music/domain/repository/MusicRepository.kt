package cn.vce.easylook.feature_music.domain.repository

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import cn.vce.easylook.feature_music.domain.entities.PlayListAndSongs
import cn.vce.easylook.feature_music.domain.entities.Playlist
import cn.vce.easylook.feature_music.domain.entities.Song
import kotlinx.coroutines.flow.Flow

interface MusicRepository {

    fun getPlaylist(pid: String): Flow<List<Playlist>>

    fun getPlaylistAndSongs(pid: String): Flow<List<PlayListAndSongs>>

    suspend fun insertPlaylist(playlist: Playlist)

    fun searchSongInPlaylist(playlistId: String, query: String): Flow<List<Song>>

}