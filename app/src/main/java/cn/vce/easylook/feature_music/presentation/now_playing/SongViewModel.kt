package cn.vce.easylook.feature_music.presentation.now_playing

import androidx.lifecycle.*
import cn.vce.easylook.feature_music.exoplayer.MusicServiceConnection
import cn.vce.easylook.feature_music.repository.MusicRepository
import cn.vce.easylook.utils.id
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SongViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection,
    private val musicRepository: MusicRepository
): ViewModel() {

    private val curPlayingSong = musicServiceConnection.curPlayingSong
    //歌词相关
    val isLyricShow = MutableLiveData(false)
    val lyric = isLyricShow.switchMap { isLyricShow ->
        liveData {
            if (isLyricShow){
                curPlayingSong.value?.id?.let {
                    val lrcText = musicRepository.getLyricInfo(it)

                    val array: Array<String> =
                        lrcText.split("\\n".toRegex()).dropLastWhile({ it.isEmpty() })
                            .toTypedArray()
                    val sb = StringBuffer()
                    for (i in array.indices){
                        var isFirst = false
                        for (j in 0.. array[i].length-1){
                            val c = array[i][j]
                            if (!isFirst && c == ','){
                                isFirst = true
                                sb.append("]")
                            }else if (j == array[i].length-1 && c == ']'){
                                sb.append("\n")
                            }else{
                                sb.append(c)
                            }
                        }
                    }
                    emit(sb.toString())
                }?:emit(null)
            }
        }
    }
    fun setLyricShow(isShowFlag: Boolean){
        isLyricShow.value = isShowFlag
    }
}



















