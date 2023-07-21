package cn.vce.easylook.feature_music.models.bli.download

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Durl {
    @SerializedName("order")
    @Expose
    var order: Int? = null

    @SerializedName("length")
    @Expose
    var length: Int? = null

    @SerializedName("size")
    @Expose
    var size: Int? = null

    @SerializedName("ahead")
    @Expose
    var ahead: String? = null

    @SerializedName("vhead")
    @Expose
    var vhead: String? = null

    @SerializedName("url")
    @Expose
    var url: String? = null

    @SerializedName("backup_url")
    @Expose
    var backupUrl: List<String>? = null
}