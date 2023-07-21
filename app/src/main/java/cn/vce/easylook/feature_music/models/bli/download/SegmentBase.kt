package cn.vce.easylook.feature_music.models.bli.download

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SegmentBase {
    @SerializedName("Initialization")
    @Expose
    var initialization: String? = null

    @SerializedName("indexRange")
    @Expose
    var indexRange: String? = null
}