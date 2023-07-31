package cn.vce.easylook.feature_music.models


import androidx.databinding.BaseObservable
import androidx.room.*
import com.drake.brv.annotaion.ItemOrientation
import com.drake.brv.item.ItemExpand
import com.drake.brv.item.ItemSwipe
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
                       val list: List<MusicInfo>?,
                       var checked: Boolean = false,
                       var visibility: Boolean = false
                        ) : BaseObservable()


//歌曲信息
@Entity(primaryKeys = ["id", "pid"])
data class MusicInfo(
         @ColumnInfo(name = "id")
         var id: String,
         @ColumnInfo(name = "songId")
         val songId: String? = null,
         @ColumnInfo(name = "name")
         val name: String? = "",
         @ColumnInfo(name = "artists")
         val artists: List<ArtistsItem>?,
         @Embedded
         val album: Album?,
         @ColumnInfo(name = "vendor")
         var vendor: String? = "",
         @ColumnInfo(name = "dl", defaultValue = "0")
         val dl: Boolean = false,//在线歌曲是否付费歌曲，false 不能下载
         @ColumnInfo(name = "cp", defaultValue = "0")
         val cp: Boolean = false,//在线歌曲是否限制播放，false 可以播放
         @Embedded
         val quality: QualityBean?,
         @ColumnInfo(name = "pid")
         var pid: String = "",
         @ColumnInfo(name = "timestamp")
         val timestamp: Long = 0,
         @ColumnInfo(name = "source")
         var source: String = "NETEASE",
         var songUrl: String? = ""
): Serializable, ItemExpand /*ItemSwipe*/ {

    @Ignore
    constructor(
        id: String,
        songId: String? = null,
        name: String? = "",
        artists: List<ArtistsItem>?,
        album: Album?,
        vendor: String? = "",
        dl: Boolean = false,
        cp: Boolean = false,
        quality: QualityBean?,
        songUrl: String? = null,
        pid: String = "",
        source: String = "NETEASE"//BLIBLI和NETEASE
    ) : this(id, songId, name, artists, album, vendor, dl, cp, quality,pid, System.currentTimeMillis(), source, songUrl)


    @Ignore
    override var itemGroupPosition: Int = 0
    @Ignore
    override var itemExpand: Boolean = false
    @Ignore
    override var itemSublist: List<Any?>? = null
    /*@Ignore
    override var itemOrientationSwipe: Int = ItemOrientation.HORIZONTAL // 侧滑方向*/
}

enum class MusicSourceType{
    BLIBLI, NETEASE
}

//歌唱艺术家
data class ArtistsItem(@SerializedName("id")
                       val id: String = "",
                       @SerializedName("name")
                       val name: String = ""): Serializable
//专辑
data class Album(@ColumnInfo(name = "album_id")
                 val id: String? = "",
                 @ColumnInfo(name = "album_name")
                 val name: String? = "",
                 @ColumnInfo(name = "cover")
                 val cover: String? = ""): Serializable
//音质
data class QualityBean(@SerializedName("192")
                       @ColumnInfo(name = "high", defaultValue = "0")
                       val high: Boolean = false,
                       @SerializedName("320")
                       @ColumnInfo(name = "hq", defaultValue = "0")
                       val hq: Boolean = false,
                       @SerializedName("999")
                       @ColumnInfo(name = "sq", defaultValue = "0")
                       val sq: Boolean = false): Serializable




