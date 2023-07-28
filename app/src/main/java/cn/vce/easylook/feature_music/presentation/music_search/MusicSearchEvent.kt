package cn.vce.easylook.feature_music.presentation.music_search

import cn.vce.easylook.base.BaseEvent
import cn.vce.easylook.feature_music.models.MusicInfo

sealed class MusicSearchEvent: BaseEvent() {
    data class RefreshSearchEvent(val mOffset: Int): MusicSearchEvent()
}