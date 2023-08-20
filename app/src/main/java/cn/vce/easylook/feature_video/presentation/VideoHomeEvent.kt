package cn.vce.easylook.feature_video.presentation

import cn.vce.easylook.base.BaseEvent

sealed class VideoHomeEvent: BaseEvent(){

    object LoadData: VideoHomeEvent()

    object LoadMoreData: VideoHomeEvent()
}