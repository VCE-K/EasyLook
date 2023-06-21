package cn.vce.easylook.feature_music.models

import androidx.room.*
import com.google.gson.annotations.SerializedName
import java.io.Serializable

//播放列表信息
@Entity
data class PlaylistInfo(
    @SerializedName("id")
    @PrimaryKey
    val id: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("cover")
    val cover: String? = null,
    @SerializedName("playCount")
    val playCount: Long = 0,
    @SerializedName("total")
    val total: Int = 0,/*
    @Relation(
        parentColumn = "id",
        entityColumn = "pid"
    )
    @SerializedName("list")
    val list: MutableList<MusicInfo>? = null*/): Serializable


data class PlaylistWithMusicInfo(
    @Embedded val playlist: PlaylistInfo,
    @Relation(
        parentColumn = "id",
        entityColumn = "pid"
    )
    @SerializedName("list")
    val list: MutableList<MusicInfo>? = null
)