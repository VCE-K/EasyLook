package cn.vce.easylook.feature_music.data.repository

import cn.vce.easylook.feature_music.data.data_source.MusicDao
import cn.vce.easylook.feature_music.domain.entities.PlayListAndSongs
import cn.vce.easylook.feature_music.domain.entities.Playlist
import cn.vce.easylook.feature_music.domain.entities.Song
import cn.vce.easylook.feature_music.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow

class MusicRepositoryImpl(
    private val dao: MusicDao
) : MusicRepository {
    override fun getPlaylist(pid: String): Flow<List<Playlist>> = dao.getPlaylist(pid)

    override fun getPlaylistAndSongs(pid: String): Flow<List<PlayListAndSongs>> = dao.getPlaylistAndSongs(pid)

    override suspend fun insertPlaylist(playlist: Playlist) = dao.insertPlaylist(playlist)

    override fun searchSongInPlaylist(playlistId: String, query: String): Flow<List<Song>> = dao.searchSongInPlaylist(playlistId, query)

}