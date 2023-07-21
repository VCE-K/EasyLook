package cn.vce.easylook.feature_music.models.bli

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DynamicResponse {
    @SerializedName("exp_list")
    @Expose
    var expList: Any? = null

    @SerializedName("code")
    @Expose
    var code: Int? = null

    @SerializedName("cost_time")
    @Expose
    var costTime: CostTime? = null

    @SerializedName("result")
    @Expose
    var hotSong: List<HotSong>? = null

    @SerializedName("show_column")
    @Expose
    var showColumn: Int? = null

    @SerializedName("rqt_type")
    @Expose
    var rqtType: String? = null

    @SerializedName("numPages")
    @Expose
    var numPages: Int? = null

    @SerializedName("numResults")
    @Expose
    var numResults: Int? = null

    @SerializedName("crr_query")
    @Expose
    var crrQuery: String? = null

    @SerializedName("pagesize")
    @Expose
    var pagesize: Int? = null

    @SerializedName("suggest_keyword")
    @Expose
    var suggestKeyword: String? = null

    @SerializedName("egg_info")
    @Expose
    var eggInfo: Any? = null

    @SerializedName("exp_bits")
    @Expose
    var expBits: Int? = null

    @SerializedName("seid")
    @Expose
    var seid: String? = null

    @SerializedName("msg")
    @Expose
    var msg: String? = null

    @SerializedName("egg_hit")
    @Expose
    var eggHit: Int? = null

    @SerializedName("page")
    @Expose
    var page: Int? = null
}