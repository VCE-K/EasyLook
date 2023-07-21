package cn.vce.easylook.feature_music.models.bli

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CostTime {
    @SerializedName("params_check")
    @Expose
    var paramsCheck: String? = null

    @SerializedName("illegal_handler")
    @Expose
    var illegalHandler: String? = null

    @SerializedName("as_response_format")
    @Expose
    var asResponseFormat: String? = null

    @SerializedName("as_request")
    @Expose
    var asRequest: String? = null

    @SerializedName("deserialize_response")
    @Expose
    var deserializeResponse: String? = null

    @SerializedName("as_request_format")
    @Expose
    var asRequestFormat: String? = null

    @SerializedName("total")
    @Expose
    var total: String? = null

    @SerializedName("main_handler")
    @Expose
    var mainHandler: String? = null
}