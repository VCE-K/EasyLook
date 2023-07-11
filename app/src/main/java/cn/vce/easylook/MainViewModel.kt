package cn.vce.easylook

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import cn.vce.easylook.base.BaseEvent
import cn.vce.easylook.base.BaseViewModel
import cn.vce.easylook.feature_music.exoplayer.*
import cn.vce.easylook.feature_music.models.MusicInfo
import cn.vce.easylook.feature_music.models.PlaylistType
import cn.vce.easylook.feature_music.other.Constants
import cn.vce.easylook.feature_music.other.Constants.MEDIA_ROOT_ID
import cn.vce.easylook.feature_music.other.MusicConfigManager
import cn.vce.easylook.feature_music.other.Resource
import cn.vce.easylook.feature_music.repository.MusicRepository
import cn.vce.easylook.utils.toast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection,
    private val musicSource: MusicSource,
    private val musicRepository: MusicRepository
) : BaseViewModel() {
    val isConnected = musicServiceConnection.isConnected
    val networkError = musicServiceConnection.networkError
    val curPlayingSong = musicServiceConnection.curPlayingSong
    val playbackState = musicServiceConnection.playbackState


    private val _playlistId = MutableLiveData("")

    private val _mediaItems = MutableLiveData<Resource<List<MusicInfo>>>()
    val mediaItems = _mediaItems

    private val _playMode = MutableLiveData<Int>()
    val playMode = _playMode

    private val _isInitFlag = MutableLiveData<Any?>()

    //歌曲时长
    private val _curSongDuration = MutableLiveData<Long>()
    val curSongDuration: LiveData<Long> = _curSongDuration

    //所在节点
    private val _curPlayerPosition = MutableLiveData<Long>()
    val curPlayerPosition: LiveData<Long> = _curPlayerPosition


    init {
        _playMode.value = MusicConfigManager.getPlayMode()
        updateCurrentPlayerPosition()
    }


    override fun onEvent(event: BaseEvent) {
        when (event) {
            is MainEvent.InitPlaylist ->{
                _isInitFlag.value = _isInitFlag.value
                launch {
                    val songs = musicRepository.getMusicInfos(PlaylistType.HISPLAY.toString())
                    songs?.let {
                        onEvent(MainEvent.ClickPlay(songs, songs[0]))
                        //playOrToggleSong(songs[songs.size - 1], true)
                    }
                }
            }
            is MainEvent.ClickPlay -> {
                event.musicInfos.toMutableList()?.apply {
                    viewModelScope.launch {
                        //正在播放的歌单是不是现在显示的歌单
                        val pid = event.musicInfo.pid
                        val fetchFlag = (_playlistId.value == pid && pid != "")
                        //原来不是同一个那就刷新数据，但是存在新增歌单歌曲但是歌曲数据源没更新情况，so...
                        musicSource.fetchMediaData(this@apply)
                        if ( !fetchFlag ) {
                            pid?.let {
                                _playlistId.value = pid
                            }
                        }
                        withContext(Dispatchers.Main) {
                            /*if (!fetchFlag) {
                                subscribe()//获取新的数据
                            }*/
                            subscribe()//获取新的数据
                            playOrToggleSong(event.musicInfo)
                        }
                    }
                }
            }
            is MainEvent.InitPlayMode -> {
                when (_playMode.value) {
                    MusicConfigManager.REPEAT_MODE_ALL -> { //0
                        musicServiceConnection.transportControls.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ALL)
                    }
                    MusicConfigManager.REPEAT_MODE_ONE -> { //1
                        musicServiceConnection.transportControls.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ONE)
                    }
                    MusicConfigManager.PLAY_MODE_RANDOM -> {//2
                        musicServiceConnection.transportControls.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ALL )
                        //musicServiceConnection.transportControls.setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_ALL)
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
                when (playMode) {
                    MusicConfigManager.REPEAT_MODE_ALL -> { //1
                        musicServiceConnection.transportControls.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ALL)
                        musicServiceConnection.transportControls.setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_NONE)
                    }
                    MusicConfigManager.REPEAT_MODE_ONE -> { //2
                        musicServiceConnection.transportControls.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ONE)
                        musicServiceConnection.transportControls.setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_NONE)
                    }
                    MusicConfigManager.PLAY_MODE_RANDOM -> {//3
                        musicServiceConnection.transportControls.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ALL)
                        //musicServiceConnection.transportControls.setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_ALL)
                    }
                    else -> {}
                }
                MusicConfigManager.savePlayMode(playMode)
                _playMode.value = playMode
            }
        }
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
        if(isPrepared && musicInfo.id == curPlayingSong.value?.getString(METADATA_KEY_MEDIA_ID)) {
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

    private fun updateCurrentPlayerPosition() {
        viewModelScope.launch {
            while(true) {
                val pos = playbackState.value?.currentPlaybackPosition
                if(curPlayerPosition.value != pos) {
                    _curPlayerPosition.postValue(pos?:0L)
                    _curSongDuration.postValue(MusicService.curSongDuration)
                }
                delay(Constants.UPDATE_PLAYER_POSITION_INTERVAL)
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

    override fun onCleared() {
        super.onCleared()
        musicServiceConnection.unsubscribe(MEDIA_ROOT_ID, object : MediaBrowserCompat.SubscriptionCallback() {})
    }



}












