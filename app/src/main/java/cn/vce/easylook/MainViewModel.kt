package cn.vce.easylook

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cn.vce.easylook.base.BaseEvent
import cn.vce.easylook.base.BaseViewModel
import cn.vce.easylook.feature_music.exoplayer.*
import cn.vce.easylook.feature_music.models.MusicInfo
import cn.vce.easylook.feature_music.other.Constants.MEDIA_ROOT_ID
import cn.vce.easylook.feature_music.other.MusicConfigManager
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
    private val _playMode = MutableLiveData<Int>()
    val playMode = _playMode

    init {
        subscribe()
        _playMode.value = MusicConfigManager.getPlayMode()
    }

    override fun onEvent(event: BaseEvent) {
        when (event) {
            is MainEvent.ClickPlay -> {
                event.musicInfos.toMutableList()?.apply {
                    viewModelScope.launch {
                        //正在播放的歌单是不是现在显示的歌单
                        val pid = get(0).pid
                        val fetchFlag = (playlistId == pid && pid != "")
                        if ( !fetchFlag ) { //不是同一个那就刷新数据
                            musicSource.fetchMediaData(this@apply)
                            pid?.let {
                                playlistId = pid
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
            is MainEvent.InitingPlayMode -> {
                when (_playMode.value) {
                    MusicConfigManager.REPEAT_MODE_ALL -> { //0
                        musicServiceConnection.transportControls.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ALL)
                    }
                    MusicConfigManager.REPEAT_MODE_ONE -> { //1
                        musicServiceConnection.transportControls.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ONE)
                    }
                    MusicConfigManager.PLAY_MODE_RANDOM -> {//2
                        musicServiceConnection.transportControls.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_NONE )
                        musicServiceConnection.transportControls.setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_ALL)
                    }
                    else -> {}
                }
            }
            is MainEvent.UpdatePlayMode -> {
                //默认顺序播放
                var playMode = _playMode.value?: MusicConfigManager.getPlayMode()
                playMode = if ((playMode + 1) == MusicConfigManager.PLAY_MODE_RANDOM){
                    MusicConfigManager.PLAY_MODE_RANDOM
                }else{
                    (playMode + 1) % MusicConfigManager.PLAY_MODE_RANDOM
                }
                //1 2 2
                //2 3 3
                //3 4 1
                when (playMode) {
                    MusicConfigManager.REPEAT_MODE_ALL -> { //0
                        musicServiceConnection.transportControls.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ALL)
                    }
                    MusicConfigManager.REPEAT_MODE_ONE -> { //1
                        musicServiceConnection.transportControls.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ONE)
                    }
                    MusicConfigManager.PLAY_MODE_RANDOM -> {//2
                        musicServiceConnection.transportControls.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_NONE )
                        musicServiceConnection.transportControls.setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_ALL)
                    }
                    else -> {}
                }
                MusicConfigManager.savePlayMode(playMode)
                _playMode.value = playMode
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
            musicServiceConnection.transportControls.playFromMediaId(musicInfo.id, null)
        }
    }

    override fun onCleared() {
        super.onCleared()
        musicServiceConnection.unsubscribe(MEDIA_ROOT_ID, object : MediaBrowserCompat.SubscriptionCallback() {})
    }



}












