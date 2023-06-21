package cn.vce.easylook

import cn.vce.easylook.base.BaseEvent
import cn.vce.easylook.feature_music.models.MusicInfo

sealed class MainEvent: BaseEvent() {
    // 如果是点击播放该列表歌曲事件
    data class ClickPlay(val musicInfos:List<MusicInfo>, val musicInfo: MusicInfo): MainEvent()
}
