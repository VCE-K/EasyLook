package cn.vce.easylook.feature_music.presentation.music_local

import cn.vce.easylook.base.BaseEvent


sealed class MusicLocalEvent: BaseEvent() {
    //查询所有歌单
    object SearchAllPlaylist: MusicLocalEvent()

}
