package cn.vce.easylook.feature_music.presentation.home_music.bli_music_list

import cn.vce.easylook.base.BaseEvent



sealed class BliMusicListEvent: BaseEvent() {

    data class SwitchCharts(val cataId: Int, val position: Int): BliMusicListEvent()

    object FetchChildData: BliMusicListEvent()


    object FetchMoreChildData: BliMusicListEvent()

    object TextChange: BliMusicListEvent()
}