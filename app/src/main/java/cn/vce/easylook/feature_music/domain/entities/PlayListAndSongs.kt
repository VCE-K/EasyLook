package cn.vce.easylook.feature_music.domain.entities

import androidx.room.Embedded
import androidx.room.Relation

class PlayListAndSongs(
    @Embedded val playlist: Playlist,
    @Relation(
        parentColumn = "pid",
        entityColumn = "playlistId"
    )
    val song: List<Song>
)
