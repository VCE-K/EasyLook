package cn.vce.easylook.feature_music.presentation.home_music.charts

import cn.vce.easylook.base.BaseEvent
import cn.vce.easylook.feature_music.presentation.home_music.music_local.MusicLocalEvent

sealed class ChartsEvent: BaseEvent() {
    //获取数据
    object FetchData: ChartsEvent()
    data class SwitchCharts(val pid: String, val position: Int): BaseEvent()

    object TextChange: ChartsEvent()
}