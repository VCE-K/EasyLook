package cn.vce.easylook.feature_video.presentation.video_detail

import cn.vce.easylook.base.BaseEvent
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer

class VideoDetailEvent: BaseEvent() {
    data class EnterFullscreen(val standardGSYVideoPlayer: StandardGSYVideoPlayer): BaseEvent()

    object ExitFullscreen: BaseEvent()

    data class RecordCurrentPosition(val currentPosition: Long): BaseEvent()

    object IsNeedMuteSaved : BaseEvent()
}