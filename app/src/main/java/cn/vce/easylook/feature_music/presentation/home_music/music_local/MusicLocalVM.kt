package cn.vce.easylook.feature_music.presentation.home_music.music_local

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import cn.vce.easylook.base.BaseViewModel
import cn.vce.easylook.base.BaseEvent
import cn.vce.easylook.feature_music.models.MusicInfo
import cn.vce.easylook.feature_music.models.PlaylistInfo
import cn.vce.easylook.feature_music.models.PlaylistWithMusicInfo
import cn.vce.easylook.feature_music.other.Resource
import cn.vce.easylook.feature_music.presentation.home_music.charts.ChartsEvent
import cn.vce.easylook.feature_music.presentation.home_music.charts.ChartsRepo
import cn.vce.easylook.feature_music.repository.MusicRepository
import cn.vce.easylook.utils.convertList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MusicLocalVM @Inject constructor(
    private val repository: MusicRepository
): BaseViewModel() {

    private val playlistWithMusicInfos =  MutableLiveData<List<PlaylistWithMusicInfo>?>()

    val playlistInfos = playlistWithMusicInfos.switchMap {
        val data = it?.map{ item ->
            item.playlist
        }
        liveData { emit(data) }
    }

    val songs = MutableLiveData<List<MusicInfo>?>()

    val filterSongs = MutableLiveData<List<MusicInfo>?>()

    val etSearchText = MutableLiveData<String>()

    private val pid = MutableLiveData<String>()

    val parentPosition = MutableLiveData<Int>()

    init {
        onEvent(MusicLocalEvent.FetchData)
    }
    override fun onEvent(event: BaseEvent) {
        when(event){
            is MusicLocalEvent.FetchData -> {
                getPlaylistJob?.cancel()
                getPlaylistJob = repository.getAllPlaylistWithMusicInfo()
                    .onEach { data ->
                        playlistWithMusicInfos.value  = data
                    }.launchIn(viewModelScope)
            }
            is MusicLocalEvent.SwitchPlaylist -> {
                if (event.pid != pid.value){
                    parentPosition.value = event.position
                    pid.value = event.pid
                    launch {
                        val data = repository.getMusicInfos(event.pid)
                        songs.postValue(null)
                        songs.postValue(data)
                        onEvent(MusicLocalEvent.TextChange)
                    }
                }
            }
            is MusicLocalEvent.TextChange -> {
                val input = etSearchText.value?:""
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
