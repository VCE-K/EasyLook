package cn.vce.easylook

import android.support.v4.media.MediaDescriptionCompat
import cn.vce.easylook.base.BaseEvent
import cn.vce.easylook.feature_music.models.MusicInfo
import java.util.*
import kotlin.collections.ArrayList

sealed class MainEvent: BaseEvent() {
    // 如果是点击播放该列表歌曲事件
    data class InitPlaylist(val flag:Boolean): MainEvent()
    data class ClickPlay(val musicInfos:List<MusicInfo>, val musicInfo: MusicInfo): MainEvent()

    object InitPlayMode: MainEvent()
    object UpdatePlayMode: MainEvent()

    data class AddQueueItem(val description: MediaDescriptionCompat, val index: Int?): MainEvent()

    data class DownloadMusic(val musicInfos: List<MusicInfo>): MainEvent()


}

