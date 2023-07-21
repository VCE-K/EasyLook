package cn.vce.easylook.feature_music.models.bli.download

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Audio {

    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("baseUrl")
    @Expose
    var baseUrl: String? = null

    @SerializedName("backupUrl")
    @Expose
    var backupUrl: Any? = null

    @SerializedName("bandwidth")
    @Expose
    var bandwidth: Int? = null

    @SerializedName("mimeType")
    @Expose
    var mimeType: String? = null

    @SerializedName("codecs")
    @Expose
    var codecs: String? = null

    @SerializedName("width")
    @Expose
    var width: Int? = null

    @SerializedName("height")
    @Expose
    var height: Int? = null

    @SerializedName("frameRate")
    @Expose
    var frameRate: String? = null

    @SerializedName("sar")
    @Expose
    var sar: String? = null

    @SerializedName("startWithSap")
    @Expose
    var startWithSap: Int? = null

    @SerializedName("SegmentBase")
    @Expose
    var segmentBase: SegmentBase? = null

    @SerializedName("codecid")
    @Expose
    var codecid: Int? = null
}