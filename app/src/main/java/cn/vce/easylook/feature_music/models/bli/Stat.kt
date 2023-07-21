package cn.vce.easylook.feature_music.models.bli

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Stat {
    @SerializedName("aid")
    @Expose
    var aid: Int? = null

    @SerializedName("view")
    @Expose
    var view: Int? = null

    @SerializedName("danmaku")
    @Expose
    var danmaku: Int? = null

    @SerializedName("reply")
    @Expose
    var reply: Int? = null

    @SerializedName("favorite")
    @Expose
    var favorite: Int? = null

    @SerializedName("coin")
    @Expose
    var coin: Int? = null

    @SerializedName("share")
    @Expose
    var share: Int? = null

    @SerializedName("now_rank")
    @Expose
    var nowRank: Int? = null

    @SerializedName("his_rank")
    @Expose
    var hisRank: Int? = null

    @SerializedName("like")
    @Expose
    var like: Int? = null

    @SerializedName("dislike")
    @Expose
    var dislike: Int? = null
}