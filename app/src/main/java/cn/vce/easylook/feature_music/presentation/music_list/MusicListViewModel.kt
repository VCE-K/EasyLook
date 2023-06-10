package cn.vce.easylook.feature_music.presentation.music_list

import androidx.lifecycle.*
import cn.vce.easylook.feature_music.data.Repository
import cn.vce.easylook.feature_music.domain.entities.PlayListAndSongs

import cn.vce.easylook.feature_music.domain.entities.Song
import cn.vce.easylook.feature_music.domain.entities.Playlist
import cn.vce.easylook.feature_music.exoplayer.MusicSource
import cn.vce.easylook.feature_music.other.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MusicListViewModel @Inject constructor(
    private val musicSource: MusicSource,
    state: SavedStateHandle
) : ViewModel() {

    private val _playlist = MutableLiveData<Playlist>()

    val playlist = _playlist

    private val playListAndSongs = Transformations.switchMap(_playlist) { playlist ->
        Repository.getPlaylistDetail(playlist.pid)
    }

    private val _mediaItems = MediatorLiveData<Resource<List<Song>>>().apply {
        value = Resource.loading( null )
        addSource(playListAndSongs) { newValue ->
            val data = newValue.getOrNull()
            if (data != null && data.song != value?.data) {
                value = Resource.success(data.song)
            }
        }
    }
    val mediaItems = _mediaItems



    init {
        //点击新的歌单，或者点击现在的歌单但是数据已经不一致的情况
        val playlist = state.get<Playlist>("playlist")
        playlist?.let{
            _playlist.value = it
        }
    }


    fun onEvent(event: MusicListEvent) {
        when (event) {
            is MusicListEvent.PlayList -> {
                _mediaItems.value?.data?.toMutableList()?.apply {
                    viewModelScope.launch {
                        //正在播放的歌单是不是现在显示的歌单
                        val id = playlist.value?.pid
                        val fetchFlag = (musicSource.playlistId == id)
                        if ( !fetchFlag ) { //不是同一个那就刷新数据
                            musicSource.fetchMediaData {
                                return@fetchMediaData this@apply
                            }
                            id?.let {
                                musicSource.playlistId = id
                            }
                        }
                        withContext(Dispatchers.Main) {
                            event.action(!fetchFlag)
                        }
                    }
                }
            }
        }
    }
}