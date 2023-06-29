package cn.vce.easylook.feature_music.exoplayer

import android.support.v4.media.MediaMetadataCompat
import cn.vce.easylook.feature_music.models.Album
import cn.vce.easylook.feature_music.models.MusicInfo
import cn.vce.easylook.utils.ConvertUtils


fun MediaMetadataCompat.toMusicInfo(): MusicInfo? {
    return description?.let {
        MusicInfo(
            id = it.mediaId ?: "",
            name = it.title.toString(),
            artists = emptyList(),
            quality = null,
            album = Album(cover = it.iconUri.toString(),name = it.subtitle.toString()),
            songUrl = it.mediaUri.toString(),
        )
    }
}

fun MutableList<MusicInfo>.transSongs(): MutableList<MediaMetadataCompat> {
    return map { musicInfo ->

        var artistNames = ConvertUtils.getArtist( musicInfo.artists)
        MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, musicInfo.album?.name)
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, musicInfo.songId?: musicInfo.id)
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, musicInfo.name)
            .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, musicInfo.name)
            .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI, musicInfo.album?.cover ?: "")
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, musicInfo.songUrl)
            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, musicInfo.album?.cover ?: "")
            .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, musicInfo.album?.name)
            .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, musicInfo.album?.name)
            .putString(MediaMetadataCompat.METADATA_KEY_AUTHOR, artistNames)
            .build()
    }.toMutableList()
}

fun MusicInfo.transSong(): MediaMetadataCompat {

    var artistNames = ConvertUtils.getArtist( this.artists)
    return MediaMetadataCompat.Builder()
        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, album?.name)
        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, songId?: id)
        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, name)
        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, name)
        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI, album?.cover ?: "")
        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, songUrl)
        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, album?.cover ?: "")
        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, album?.name)
        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, album?.name)
        .putString(MediaMetadataCompat.METADATA_KEY_AUTHOR, artistNames)
        .build()
}