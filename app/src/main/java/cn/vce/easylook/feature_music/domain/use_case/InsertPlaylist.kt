package cn.vce.easylook.feature_music.domain.use_case

import cn.vce.easylook.feature_music.domain.entities.Playlist
import cn.vce.easylook.feature_music.domain.repository.MusicRepository

class InsertPlaylist(
    private val repository: MusicRepository
) {
    suspend operator fun invoke(
        playlist: Playlist
    ) {
        return repository.insertPlaylist(playlist)
    }
}