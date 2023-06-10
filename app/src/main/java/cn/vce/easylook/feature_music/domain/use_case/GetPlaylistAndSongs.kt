package cn.vce.easylook.feature_music.domain.use_case

import cn.vce.easylook.feature_music.domain.entities.InvalidSongException
import cn.vce.easylook.feature_music.domain.entities.PlayListAndSongs
import cn.vce.easylook.feature_music.domain.entities.Playlist
import cn.vce.easylook.feature_music.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow

class GetPlaylistAndSongs(
    private val repository: MusicRepository
) {
    operator fun invoke(
        pid: String
    ): Flow<List<PlayListAndSongs>> {
        return repository.getPlaylistAndSongs(pid)
    }
}
