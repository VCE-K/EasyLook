package cn.vce.easylook.feature_music.presentation.playlist_detail

import androidx.lifecycle.*
import cn.vce.easylook.base.BaseViewModel
import cn.vce.easylook.base.BaseEvent
import cn.vce.easylook.feature_music.models.MusicInfo

import cn.vce.easylook.feature_music.models.PlaylistInfo
import cn.vce.easylook.feature_music.other.Resource
import cn.vce.easylook.feature_music.repository.MusicRepository
import cn.vce.easylook.utils.convertList
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlaylistDetailVM @Inject constructor(
    private val repository: MusicRepository,
    state: SavedStateHandle
) : BaseViewModel() {

    private val _playlist = MutableLiveData<PlaylistInfo>()

    val playlist = _playlist

    private val artistSongs = Transformations.switchMap(_playlist) { playlist ->
        repository.getPlaylistDetail(playlist.id)
    }

    private val _mediaItems = MediatorLiveData<Resource<List<MusicInfo>>>().apply {
        value = Resource.loading( null )
        addSource(artistSongs) { newValue ->
            val newData = newValue.getOrNull()
            if (newData != null && newData.songs != value?.data) {
                val data = convertList(newData.songs, MusicInfo::class.java).map{
                    it.pid = ""
                    it
                }
                value = Resource.success(data)
            }
        }
    }
    val mediaItems = _mediaItems



    init {
        //点击新的歌单，或者点击现在的歌单但是数据已经不一致的情况
        val playlistInfo = state.get<PlaylistInfo>("playlistInfo")
        playlistInfo?.let{
            _playlist.value = it
        }
    }

    override fun onEvent(event: BaseEvent) {
        when (event) {

        }
    }
}