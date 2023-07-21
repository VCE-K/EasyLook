package cn.vce.easylook.feature_music.models.bli

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Page {
    @SerializedName("cid")
    @Expose
    var cid: Int? = null

    @SerializedName("page")
    @Expose
    var page: Int? = null

    @SerializedName("from")
    @Expose
    var from: String? = null

    @SerializedName("part")
    @Expose
    var part: String? = null

    @SerializedName("duration")
    @Expose
    var duration: Int? = null

    @SerializedName("vid")
    @Expose
    var vid: String? = null

    @SerializedName("weblink")
    @Expose
    var weblink: String? = null

    @SerializedName("dimension")
    @Expose
    var dimension: Dimension? = null
}