package cn.vce.easylook.feature_video.presentation.home.detail

import cn.vce.easylook.base.BaseEvent
import cn.vce.easylook.feature_video.models.Daily

class DailyEvent: BaseEvent() {

    data class Search(val callback: (List<Daily.Item>) -> Unit): BaseEvent()
}