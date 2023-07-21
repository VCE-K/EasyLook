package cn.vce.easylook.feature_music.models.bli

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class HotSong {
    @SerializedName("senddate")
    @Expose
    var senddate: Int? = null

    @SerializedName("rank_offset")
    @Expose
    var rankOffset: Int? = null

    @SerializedName("tag")
    @Expose
    var tag: String? = null

    @SerializedName("duration")
    @Expose
    var duration: Int? = null

    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("rank_score")
    @Expose
    var rankScore: Int? = null

    @SerializedName("badgepay")
    @Expose
    var badgepay: Boolean? = null

    @SerializedName("pubdate")
    @Expose
    var pubdate: String? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("review")
    @Expose
    var review: Int? = null

    @SerializedName("mid")
    @Expose
    var mid: Long? = null

    @SerializedName("is_union_video")
    @Expose
    var isUnionVideo: Int? = null

    @SerializedName("rank_index")
    @Expose
    var rankIndex: Int? = null

    @SerializedName("type")
    @Expose
    var type: String? = null

    @SerializedName("arcrank")
    @Expose
    var arcrank: String? = null

    @SerializedName("play")
    @Expose
    var play: String? = null

    @SerializedName("pic")
    @Expose
    var pic: String? = null

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("video_review")
    @Expose
    var videoReview: Int? = null

    @SerializedName("is_pay")
    @Expose
    var isPay: Int? = null

    @SerializedName("favorites")
    @Expose
    var favorites: Int? = null

    @SerializedName("arcurl")
    @Expose
    var arcurl: String? = null

    @SerializedName("author")
    @Expose
    var author: String? = null
}