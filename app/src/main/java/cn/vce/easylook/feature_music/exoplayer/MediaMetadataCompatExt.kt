package cn.vce.easylook.feature_music.exoplayer

import android.content.Context
import android.support.v4.media.MediaMetadataCompat
import cn.vce.easylook.feature_music.models.Album
import cn.vce.easylook.feature_music.models.ArtistsItem
import cn.vce.easylook.feature_music.models.MusicInfo
import cn.vce.easylook.feature_music.models.MusicSourceType
import cn.vce.easylook.feature_music.models.bli.AvData
import cn.vce.easylook.feature_music.models.bli.HotSong
import cn.vce.easylook.feature_music.repository.MusicRepository
import cn.vce.easylook.utils.ConvertUtils


//这个方法只是用于证明可以转化MusicInfo的
fun MediaMetadataCompat.toMusicInfo(): MusicInfo? {
    return description?.let {
        MusicInfo(
            id = it.mediaId ?: "",
            name = it.title.toString(),
            artists = emptyList(),
            quality = null,
            album = Album(cover = it.iconUri.toString(),name = it.subtitle.toString()),
            songUrl = it.mediaUri.toString()
        )
    }
}

fun MutableList<MusicInfo>.transSongs(): MutableList<MediaMetadataCompat> {
    return map { musicInfo ->
        val artistNames = ConvertUtils.getArtist( musicInfo.artists)
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

//BliBli相关
fun AvData.transSongs(): MediaMetadataCompat? { //初代Version先不考虑
    val url: String = url?:return null
    return MediaMetadataCompat.Builder()
        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, owner?.name ?: "")
        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, aid.toString())
        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, title)
        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI, pic)
        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, url)
        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, pic)
        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, owner?.name ?: "")
        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, owner?.name ?: "")
        .putString(MediaMetadataCompat.METADATA_KEY_AUTHOR, owner?.name ?: "")
        .build()
}



fun HotSong.transMusicInfo(): MusicInfo? {
    return MusicInfo(
        id = id.toString(),
        name = title,
        artists = emptyList(),
        quality = null,
        album = Album(cover = pic,name = title),
        songUrl = null,
        source = MusicSourceType.BLIBLI.toString()
    )
}

fun List<HotSong>.transMusicInfos(): MutableList<MusicInfo> {
    return map {
        MusicInfo(
            id = it.id.toString(),
            name = it.title,
            artists = emptyList(),
            quality = null,
            album = Album(cover = "http:"+it.pic,name = it.title),
            songUrl = null,
            source = MusicSourceType.BLIBLI.toString()
        )
    }.toMutableList()
}

fun AvData.transMusicInfo(): MusicInfo? {
    val artists = mutableListOf<ArtistsItem>()
    owner?.run {
        mid?.run {
            name?.run {
                artists.add(ArtistsItem(mid!!.toString(), name!!))
            }
        }
    }
    return MusicInfo(
        id = aid.toString(),
        name = title,
        artists = artists,
        quality = null,
        album = Album(cover = pic,name = title),
        songUrl = url,
        pid = cid.toString(),
        source = MusicSourceType.BLIBLI.toString()
    )
}