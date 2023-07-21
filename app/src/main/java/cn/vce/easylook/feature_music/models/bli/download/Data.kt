package cn.vce.easylook.feature_music.models.bli.download

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Data {
    @SerializedName("from")
    @Expose
    var from: String? = null

    @SerializedName("result")
    @Expose
    var result: String? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("quality")
    @Expose
    var quality: Int? = null

    @SerializedName("format")
    @Expose
    var format: String? = null

    @SerializedName("timelength")
    @Expose
    var timelength: Int? = null

    @SerializedName("accept_format")
    @Expose
    var acceptFormat: String? = null

    @SerializedName("accept_description")
    @Expose
    var acceptDescription: List<String>? = null

    @SerializedName("accept_quality")
    @Expose
    var acceptQuality: List<Int>? = null

    @SerializedName("video_codecid")
    @Expose
    var videoCodecid: Int? = null

    @SerializedName("seek_param")
    @Expose
    var seekParam: String? = null

    @SerializedName("seek_type")
    @Expose
    var seekType: String? = null

    @SerializedName("dash")
    @Expose
    var downloadResources: DownloadResources? = null

    @SerializedName("durl")
    @Expose
    val durl: List<Durl>? = null
}