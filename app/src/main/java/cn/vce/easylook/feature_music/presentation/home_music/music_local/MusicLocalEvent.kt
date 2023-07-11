package cn.vce.easylook.feature_music.presentation.home_music.music_local

import cn.vce.easylook.base.BaseEvent
import cn.vce.easylook.feature_music.models.PlaylistWithMusicInfo


sealed class MusicLocalEvent: BaseEvent() {
    //获取数据
    object FetchData: MusicLocalEvent()

    data class SwitchPlaylist(val pid: String, val position: Int): MusicLocalEvent()

    object TextChange: MusicLocalEvent()
}
