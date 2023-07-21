package cn.vce.easylook.feature_music.models.bli

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Subtitle {
    @SerializedName("allow_submit")
    @Expose
    var allowSubmit: Boolean? = null

    @SerializedName("list")
    @Expose
    var list: List<Any>? = null
}