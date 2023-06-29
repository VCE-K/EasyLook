package cn.vce.easylook.feature_music.presentation.home_music.music_local

import cn.vce.easylook.base.BaseEvent
import cn.vce.easylook.feature_music.models.PlaylistWithMusicInfo


sealed class MusicLocalEvent: BaseEvent() {
    //查询所有歌单
    object SearchAllPlaylist: MusicLocalEvent()


    data class RefreshSearchEvent(val callback: (List<PlaylistWithMusicInfo>) -> Unit): BaseEvent()

}
