package cn.vce.easylook.feature_music.domain.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity( tableName = "Song", primaryKeys = ["mediaId", "playlistId"])
data class Song(
    val mediaId: String,//唯一ID
    val title: String = "",//歌名
    val subtitle: String = "",//专辑名称
    var songUrl: String = "",//歌曲url
    val imageUrl: String = "",//图片url
    val artistNames: String = "",//歌手名
    val playlistId: String = ""//歌单ID
): Serializable



class InvalidSongException(message: String?): java.lang.Exception(message)