package cn.vce.easylook.feature_music.models.bli.download

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DownloadResources {
    @SerializedName("duration")
    @Expose
    var duration: Int? = null

    @SerializedName("minBufferTime")
    @Expose
    var minBufferTime: Double? = null

    @SerializedName("video")
    @Expose
    var video: List<Video>? = null

    @SerializedName("audio")
    @Expose
    var audio: List<Audio>? = null
}