package cn.vce.easylook.feature_music.models


import androidx.room.*
import com.google.gson.annotations.SerializedName
import java.io.Serializable



class InvalidSongException(message: String?): java.lang.Exception(message)


data class ArtistSongs(@SerializedName("detail")
                       val detail: ArtistItem,
                       @SerializedName("songs")
                       val songs: List<MusicInfo>)


data class ArtistItem(@SerializedName("name")
                      val name: String = "",
                      @SerializedName("id")
                      val id: String = "",
                      @SerializedName("cover")
                      val cover: String? = null,
                      @SerializedName("desc")
                      val desc: String? = null)



data class TopListBean(@SerializedName("cover")
                       val cover: String = "",
                       @SerializedName("playCount")
                       val playCount: Long = 0,
                       @SerializedName("id")
                       val id: String? = "",
                       @SerializedName("name")
                       val name: String? = null,
                       @SerializedName("description")
                       val description: String = "",
                       @SerializedName("list")
                       val list: List<MusicInfo>?)


//歌曲信息
@Entity(primaryKeys = ["id", "pid"])
data class MusicInfo(@SerializedName("id")
                     var id: String,
                     @SerializedName("songId")
                     val songId: String? = null,
                     @SerializedName("name")
                     val name: String? = "",
                     @SerializedName("artists")
                     val artists: List<ArtistsItem>?,
                     @SerializedName("album")
                     @Embedded
                     val album: Album?,
                     @SerializedName("vendor")
                     var vendor: String? = "",
                     @SerializedName("dl")
                     val dl: Boolean = false,
                     @SerializedName("cp")
                     val cp: Boolean = false,
                     @SerializedName("quality")
                     @Embedded
                     val quality: QualityBean?,
                     var songUrl: String? = null,
                     var pid: String = ""): Serializable

//歌唱艺术家
data class ArtistsItem(@SerializedName("id")
                       val id: String = "",
                       @SerializedName("name")
                       val name: String = ""): Serializable
//专辑
data class Album(@SerializedName("id")
                 @ColumnInfo(name = "album_id")
                 val id: String? = "",
                 @SerializedName("name")
                 @ColumnInfo(name = "album_name")
                 val name: String? = "",
                 @SerializedName("cover")
                 val cover: String? = ""): Serializable
//音质
data class QualityBean(@SerializedName("192")
                       val high: Boolean = false,
                       @SerializedName("320")
                       val hq: Boolean = false,
                       @SerializedName("999")
                       val sq: Boolean = false): Serializable




