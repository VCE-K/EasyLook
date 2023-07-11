package cn.vce.easylook.feature_music.presentation.bottom_music_dialog

import cn.vce.easylook.base.BaseEvent
import cn.vce.easylook.feature_music.models.MusicInfo

sealed class BottomDialogEvent: BaseEvent() {

    data class SaveMusicToPlaylist(val m: MusicInfo): BottomDialogEvent()

    data class RestoreMusicToPlaylist(val m: MusicInfo): BottomDialogEvent()

}