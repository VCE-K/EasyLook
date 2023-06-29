package cn.vce.easylook.feature_music.models

import androidx.room.*
import cn.vce.easylook.feature_music.models.group.Group1Model
import com.drake.brv.annotaion.ItemOrientation
import com.drake.brv.item.ItemDrag
import com.drake.brv.item.ItemSwipe
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
    var playCount: Long = 0,
    @SerializedName("total")
    var total: Int = 0,/*
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
): ItemDrag, ItemSwipe, Group1Model() {
    @Ignore
    override var itemOrientationDrag: Int = ItemOrientation.ALL // 拖拽方向
    @Ignore
    override var itemOrientationSwipe: Int = ItemOrientation.HORIZONTAL // 侧滑方向
    // 这里要求子列表也是可以侧滑的所以重写该字段, 并且得是可变集合, 否则无法子列表被删除
    @get:Ignore
    override val title
        get() = "${playlist.name} [${list?.size?: 0} ] [${playlist?.playCount} ]"

    // 这里要求子列表也是可以侧滑的所以重写该字段, 并且得是可变集合, 否则无法子列表被删除
    @Ignore
    override var itemSublist: List<Any?>? = list?.reversed()
}

enum class PlaylistType{
    LOCAL, LOVE, HISPLAY, WEBSEARCH
}