package cn.vce.easylook.feature_music.presentation.home_music.charts

import androidx.lifecycle.MutableLiveData
import cn.vce.easylook.base.BaseEvent
import cn.vce.easylook.base.BaseViewModel
import cn.vce.easylook.feature_music.models.MusicInfo
import cn.vce.easylook.feature_music.models.MusicSourceType
import cn.vce.easylook.feature_music.models.TopListBean
import cn.vce.easylook.feature_music.repository.MusicRepository
import cn.vce.easylook.utils.convertMusicList
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChartsViewModel
    @Inject constructor(
        private val repository: MusicRepository
    ): BaseViewModel() {

    private val pid = MutableLiveData<String>()

    val neteaseTopList = MutableLiveData<List<TopListBean>?>()

    val songs = MutableLiveData<List<MusicInfo>?>()

    val etSearchText = MutableLiveData<String?>()


    val filterSongs = MutableLiveData<List<MusicInfo>?>()

    val parentPosition = MutableLiveData<Int>()


    init {
        onEvent(ChartsEvent.FetchData)
    }

    override fun onEvent(event: BaseEvent) {
        when(event){
            is ChartsEvent.FetchData -> {
                launch({
                    neteaseTopList.value = ChartsRepo.loadNeteaseTopList()
                },{
                    neteaseTopList.value = null
                })
            }
            is ChartsEvent.SwitchCharts -> {
                if (event.pid != pid.value){
                    pid.value = event.pid
                    parentPosition.value = event.position
                    launch {
                        val playlistDetail = ChartsRepo.getPlaylistDetail(event.pid)
                        val data = playlistDetail?.run {
                            convertMusicList(songs, MusicSourceType.NETEASE.toString())
                        }
                        songs.postValue(data)
                        onEvent(ChartsEvent.TextChange)
                    }
                }
            }
            is ChartsEvent.TextChange -> {
                val input = etSearchText.value
                input?.run {
                    val data = songs.value?.filter { v ->
                        when {
                            input.isBlank() -> true
                            v is MusicInfo -> v.name?.contains(input, true) ?: false
                            else -> true
                        }
                    }
                    filterSongs.value = data
                }
            }
        }
    }

}