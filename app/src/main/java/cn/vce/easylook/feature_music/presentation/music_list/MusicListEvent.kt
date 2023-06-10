package cn.vce.easylook.feature_music.presentation.music_list

sealed class MusicListEvent {
    //// 如果是点击播放该列表歌曲事件
    data class PlayList(val action: (fetchFlag: Boolean) -> Unit): MusicListEvent()
}
