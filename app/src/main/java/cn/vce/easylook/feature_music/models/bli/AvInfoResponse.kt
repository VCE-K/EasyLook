package cn.vce.easylook.feature_music.models.bli

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AvInfoResponse {
    @SerializedName("code")
    @Expose
    var code: Int? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("ttl")
    @Expose
    var ttl: Int? = null

    @SerializedName("data")
    @Expose
    var avData: AvData? = null
}