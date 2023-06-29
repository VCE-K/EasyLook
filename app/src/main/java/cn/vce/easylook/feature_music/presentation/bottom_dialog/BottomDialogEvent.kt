package cn.vce.easylook.feature_music.presentation.bottom_dialog

import cn.vce.easylook.base.BaseEvent
import cn.vce.easylook.feature_music.models.MusicInfo

class BottomDialogEvent: BaseEvent() {

    data class SaveMusicToPlaylist(val m: MusicInfo): BaseEvent()

    data class RestoreMusicToPlaylist(val m: MusicInfo): BaseEvent()

}