package cn.vce.easylook.feature_video.presentation.video_detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import cn.vce.easylook.base.BaseEvent
import cn.vce.easylook.base.BaseViewModel
import cn.vce.easylook.feature_video.models.VideoInfo
import cn.vce.easylook.feature_video.other.VideoConfigManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VideoDetailVM @Inject constructor(
    private val state: SavedStateHandle
): BaseViewModel() {
    private val _videoInfo = MutableLiveData<VideoInfo>()
    val videoInfo = _videoInfo
    init {
        state.get<VideoInfo>("videoInfo")?.apply {
            _videoInfo.value = this
        }
    }

    override fun onEvent(event: BaseEvent) {
        when(event){
            is VideoDetailEvent.IsNeedMuteSaved -> {

            }
        }
    }



}