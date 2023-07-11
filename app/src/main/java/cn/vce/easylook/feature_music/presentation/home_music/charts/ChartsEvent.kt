package cn.vce.easylook.feature_music.presentation.home_music.charts

import cn.vce.easylook.base.BaseEvent

sealed class ChartsEvent: BaseEvent() {
    data class SwitchCharts(val pid: String, val position: Int): BaseEvent()

    object TextChange: ChartsEvent()
}