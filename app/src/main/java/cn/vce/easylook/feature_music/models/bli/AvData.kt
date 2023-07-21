package cn.vce.easylook.feature_music.models.bli

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AvData {
    @SerializedName("aid")
    @Expose
    var aid: Int? = null

    @SerializedName("videos")
    @Expose
    var videos: Int? = null

    @SerializedName("tid")
    @Expose
    var tid: Int? = null

    @SerializedName("tname")
    @Expose
    var tname: String? = null

    @SerializedName("copyright")
    @Expose
    var copyright: Int? = null

    @SerializedName("pic")
    @Expose
    var pic: String? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("pubdate")
    @Expose
    var pubdate: Int? = null

    @SerializedName("ctime")
    @Expose
    var ctime: Int? = null

    @SerializedName("desc")
    @Expose
    var desc: String? = null

    @SerializedName("state")
    @Expose
    var state: Int? = null

    @SerializedName("attribute")
    @Expose
    var attribute: Int? = null

    @SerializedName("duration")
    @Expose
    var duration: Int? = null

    @SerializedName("rights")
    @Expose
    var rights: Rights? = null

    @SerializedName("owner")
    @Expose
    var owner: Owner? = null

    @SerializedName("stat")
    @Expose
    var stat: Stat? = null

    @SerializedName("dynamic")
    @Expose
    var dynamic: String? = null

    @SerializedName("cid")
    @Expose
    var cid: Int? = null

    @SerializedName("dimension")
    @Expose
    var dimension: Dimension? = null

    @SerializedName("no_cache")
    @Expose
    var noCache: Boolean? = null

    @SerializedName("pages")
    @Expose
    var pages: List<Page>? = null

    @SerializedName("subtitle")
    @Expose
    var subtitle: Subtitle? = null

    @SerializedName("url")
    @Expose
    var url: String? = null
}