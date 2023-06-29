package cn.vce.easylook.feature_video.presentation.video_detail

import cn.vce.easylook.feature_video.other.VideoConfigManager

object VideoDetailRepo {

    fun isNeedMuteSaved() = VideoConfigManager.isNeedMuteSaved()

    fun getSavedIsNeedMute() = VideoConfigManager.getSavedIsNeedMute()

    fun saveIsNeedMuteSaved(isNeedMuteSaved: Boolean) = VideoConfigManager.saveIsNeedMuteSaved(isNeedMuteSaved)
}