package cn.vce.easylook.feature_music.presentation.now_playing

import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.*
import cn.vce.easylook.base.BaseViewModel
import cn.vce.easylook.feature_music.exoplayer.MusicServiceConnection
import cn.vce.easylook.feature_music.repository.MusicRepository
import cn.vce.easylook.utils.id
import com.drake.net.Get
import com.drake.net.Post
import com.drake.net.component.Progress
import com.drake.net.interfaces.ProgressListener
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SongViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection,
    private val musicRepository: MusicRepository
): BaseViewModel() {

    private val curPlayingSong = musicServiceConnection.curPlayingSong
    //歌词相关
    val isLyricShow = MutableLiveData(false)
    val lyric = curPlayingSong.switchMap { cps ->
        liveData {
            try {
                val data = cps?.id?.let {
                    val lrcText = musicRepository.getLyricInfo(it)
                    lrcText
                }?:null
                emit(data)
            }catch (e: Throwable){
                e.printStackTrace()
                emit(null)
            }
        }
    }
    fun setLyricShow(isShowFlag: Boolean){
        isLyricShow.value = isShowFlag
    }
}



















