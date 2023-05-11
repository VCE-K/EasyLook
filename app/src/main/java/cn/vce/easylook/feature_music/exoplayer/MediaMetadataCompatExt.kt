package cn.vce.easylook.feature_music.exoplayer

import android.support.v4.media.MediaMetadataCompat
import cn.vce.easylook.feature_music.data.entities.Song

fun MediaMetadataCompat.toSong(): Song? {
    return description?.let {
        Song(
            it.mediaId ?: "",
            it.title.toString(),
            it.subtitle.toString(),
            it.mediaUri.toString(),
            it.iconUri.toString()
        )
    }
}