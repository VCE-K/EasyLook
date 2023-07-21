package cn.vce.easylook.feature_music.models.bli

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Rights {
    @SerializedName("bp")
    @Expose
    var bp: Int? = null

    @SerializedName("elec")
    @Expose
    var elec: Int? = null

    @SerializedName("download")
    @Expose
    var download: Int? = null

    @SerializedName("movie")
    @Expose
    var movie: Int? = null

    @SerializedName("pay")
    @Expose
    var pay: Int? = null

    @SerializedName("hd5")
    @Expose
    var hd5: Int? = null

    @SerializedName("no_reprint")
    @Expose
    var noReprint: Int? = null

    @SerializedName("autoplay")
    @Expose
    var autoplay: Int? = null

    @SerializedName("ugc_pay")
    @Expose
    var ugcPay: Int? = null

    @SerializedName("is_cooperation")
    @Expose
    var isCooperation: Int? = null
}