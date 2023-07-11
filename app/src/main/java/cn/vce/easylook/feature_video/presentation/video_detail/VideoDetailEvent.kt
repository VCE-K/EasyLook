package cn.vce.easylook.feature_video.presentation.video_detail

import cn.vce.easylook.base.BaseEvent
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer

sealed class VideoDetailEvent: BaseEvent() {
    data class EnterFullscreen(val standardGSYVideoPlayer: StandardGSYVideoPlayer): VideoDetailEvent()

    object ExitFullscreen: VideoDetailEvent()

    data class RecordCurrentPosition(val currentPosition: Long): VideoDetailEvent()

    object IsNeedMuteSaved : VideoDetailEvent()
}