package cn.vce.easylook.feature_music.models.bli

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Owner {
    @SerializedName("mid")
    @Expose
    var mid: Long? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("face")
    @Expose
    var face: String? = null
}