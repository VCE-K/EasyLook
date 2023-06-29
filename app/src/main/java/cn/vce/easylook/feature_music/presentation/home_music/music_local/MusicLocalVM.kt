package cn.vce.easylook.feature_music.presentation.home_music.music_local

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cn.vce.easylook.base.BaseViewModel
import cn.vce.easylook.base.BaseEvent
import cn.vce.easylook.feature_music.models.PlaylistInfo
import cn.vce.easylook.feature_music.models.PlaylistWithMusicInfo
import cn.vce.easylook.feature_music.other.Resource
import cn.vce.easylook.feature_music.repository.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MusicLocalVM @Inject constructor(
    private val repository: MusicRepository
): BaseViewModel() {

    private var getPlaylistJob: Job? = null
    private val _playlistWithMusicInfos =  MutableLiveData<Resource<List<PlaylistWithMusicInfo>>>(Resource.loading(null))
    val playlistWithMusicInfos = _playlistWithMusicInfos

    init {
        onEvent(MusicLocalEvent.SearchAllPlaylist)
    }


    override fun onEvent(event: BaseEvent) {
        when(event){
            is MusicLocalEvent.SearchAllPlaylist -> {
                getPlaylistJob?.cancel()
                getPlaylistJob = repository.getPlaylistWithMusicInfo()
                    .onEach { playlistWithMusicInfos ->
                        _playlistWithMusicInfos.value  = Resource.success(playlistWithMusicInfos)
                    }
                    .launchIn(viewModelScope)
            }
            is MusicLocalEvent.RefreshSearchEvent -> {
                getPlaylistJob?.cancel()
                getPlaylistJob = repository.getPlaylistWithMusicInfo()
                    .onEach { playlistWithMusicInfos ->
                        event.callback.invoke(playlistWithMusicInfos)
                    }
                    .launchIn(viewModelScope)
            }
        }
    }
}
