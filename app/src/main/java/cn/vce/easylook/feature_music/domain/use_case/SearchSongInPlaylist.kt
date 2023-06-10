package cn.vce.easylook.feature_music.domain.use_case

import cn.vce.easylook.feature_music.domain.entities.InvalidSongException
import cn.vce.easylook.feature_music.domain.entities.Playlist
import cn.vce.easylook.feature_music.domain.entities.Song
import cn.vce.easylook.feature_music.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow

class SearchSongInPlaylist(
    private val repository: MusicRepository
) {
    operator fun invoke(
        playlistId: String,
        query: String
    ): Flow<List<Song>> {
        if(query.isBlank()) {
            throw InvalidSongException("The query of the Playlist can't be empty.")
        }
        return repository.searchSongInPlaylist(playlistId, query)
    }
}