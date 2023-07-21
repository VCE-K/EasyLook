package cn.vce.easylook.feature_music.presentation.home_music.personalized_playlist

import cn.vce.easylook.base.BaseEvent
import cn.vce.easylook.feature_music.presentation.home_music.music_local.MusicLocalEvent

sealed class PersonalizedPlaylistEvent: BaseEvent(){

    //获取数据
    object FetchData: PersonalizedPlaylistEvent()

    data class SwitchPlaylist(val pid: String, val position: Int): PersonalizedPlaylistEvent()

    object TextChange: PersonalizedPlaylistEvent()
}
