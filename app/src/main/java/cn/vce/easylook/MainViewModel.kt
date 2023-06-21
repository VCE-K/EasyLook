package cn.vce.easylook

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cn.vce.easylook.base.BaseViewModel
import cn.vce.easylook.base.BaseEvent
import cn.vce.easylook.feature_music.models.MusicInfo
import cn.vce.easylook.feature_music.exoplayer.*
import cn.vce.easylook.feature_music.other.Constants.MEDIA_ROOT_ID
import cn.vce.easylook.feature_music.other.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection,
    private val musicSource: MusicSource
) : BaseViewModel() {

    private var playlistId: String = ""

    private val _mediaItems = MutableLiveData<Resource<List<MusicInfo>>>()
    val mediaItems = _mediaItems

    val isConnected = musicServiceConnection.isConnected
    val networkError = musicServiceConnection.networkError
    val curPlayingSong = musicServiceConnection.curPlayingSong
    val playbackState = musicServiceConnection.playbackState


    init {
        subscribe()
    }

    override fun onEvent(event: BaseEvent) {
        when (event) {
            is MainEvent.ClickPlay -> {
                event.musicInfos.toMutableList()?.apply {
                    viewModelScope.launch {
                        //正在播放的歌单是不是现在显示的歌单
                        val id = get(0).id
                        val fetchFlag = (playlistId == id)
                        if ( !fetchFlag ) { //不是同一个那就刷新数据
                            musicSource.fetchMediaData {
                                return@fetchMediaData this@apply
                            }
                            id?.let {
                                playlistId = id
                            }
                        }
                        withContext(Dispatchers.Main) {
                            if (!fetchFlag) {
                                subscribe()//获取新的数据
                            }
                            playOrToggleSong(event.musicInfo)
                        }
                    }
                }
            }
        }
    }

    private fun subscribe() {
        _mediaItems.value = Resource.loading(null)
        musicServiceConnection.subscribe(MEDIA_ROOT_ID, object : MediaBrowserCompat.SubscriptionCallback() {
            override fun onChildrenLoaded(
                parentId: String,
                children: MutableList<MediaBrowserCompat.MediaItem>
            ) {
                super.onChildrenLoaded(parentId, children)
                val items = musicSource.musicInfos
                _mediaItems.value = Resource.success(items)
            }
        })
    }


    fun skipToNextSong() {
        musicServiceConnection.transportControls.skipToNext()
    }

    fun skipToPreviousSong() {
        musicServiceConnection.transportControls.skipToPrevious()
    }

    fun seekTo(pos: Long) {
        musicServiceConnection.transportControls.seekTo(pos)
    }

    fun playOrToggleSong(musicInfo: MusicInfo, toggle: Boolean = false) {
        val isPrepared = playbackState.value?.isPrepared ?: false
        if(isPrepared && musicInfo.songId?: musicInfo.id == curPlayingSong.value?.getString(METADATA_KEY_MEDIA_ID)) {
            playbackState.value?.let { playbackState ->
                when {
                    playbackState.isPlaying -> if(toggle) musicServiceConnection.transportControls.pause()
                    playbackState.isPlayEnabled -> musicServiceConnection.transportControls.play()
                    else -> Unit
                }
            }
        } else {
            musicServiceConnection.transportControls.playFromMediaId(musicInfo.songId?: musicInfo.id, null)
        }
    }

    override fun onCleared() {
        super.onCleared()
        musicServiceConnection.unsubscribe(MEDIA_ROOT_ID, object : MediaBrowserCompat.SubscriptionCallback() {})
    }



}

















