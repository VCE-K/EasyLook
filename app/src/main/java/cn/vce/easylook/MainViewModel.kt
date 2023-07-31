package cn.vce.easylook

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.*
import cn.vce.easylook.base.BaseEvent
import cn.vce.easylook.base.BaseViewModel
import cn.vce.easylook.feature_music.exoplayer.*
import cn.vce.easylook.feature_music.models.*
import cn.vce.easylook.feature_music.other.Constants
import cn.vce.easylook.feature_music.other.Constants.MEDIA_ROOT_ID
import cn.vce.easylook.feature_music.db.MusicConfigManager
import cn.vce.easylook.feature_music.other.Resource
import cn.vce.easylook.feature_music.other.Status
import cn.vce.easylook.feature_music.repository.MusicRepository
import cn.vce.easylook.utils.LogE
import cn.vce.easylook.utils.getString
import cn.vce.easylook.utils.id
import cn.vce.easylook.utils.toast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.text.NumberFormat
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

    val curPlayingMusic = curPlayingSong.switchMap { curPlayingSong ->
        liveData {
            if (curPlayingSong != null) {
                emit(curPlayingSong.toMusicInfo())
            }else{
                emit(null)
            }
        }
    }

    val playbackState = musicServiceConnection.playbackState

    private val _playlistId = MutableLiveData("")

    private val _mediaItems = MutableLiveData<Resource<List<MusicInfo>>>()
    val mediaItems = _mediaItems

    private val _playMode = MutableLiveData<Int>()
    val playMode = _playMode

    private val _allGranted = MutableLiveData<Boolean>(false)
    val allGranted = _allGranted
    //歌曲时长
    private val _curSongDuration = MutableLiveData<Long>()
    val curSongDuration: LiveData<Long> = _curSongDuration

    //所在节点
    private val _curPlayerPosition = MutableLiveData<Long>()
    val curPlayerPosition: LiveData<Long> = _curPlayerPosition

    private val _songList = musicServiceConnection.songList
    val songList: LiveData<List<MusicInfo>> = _songList


    private val _initPlaylistFlag = MutableLiveData(false)
    val initPlaylistFlag: LiveData<Boolean> = _initPlaylistFlag

    init {
        initConfig()
        updateCurrentPlayerPosition()
    }

    private fun initConfig(){
        var playMode: Int = if (musicRepository.getPlayMode() > 2){
            0
        }else{
            musicRepository.getPlayMode()
        }
        musicServiceConnection.curPlayingSong
        _playMode.value = playMode

        if (isAllGranted()){
            _allGranted.value = getAllGranted()
        }else{
            saveAllGranted(false)
        }
        var allGranted: Boolean = getAllGranted()
        _allGranted.value = allGranted
    }
    private fun isAllGranted() = musicRepository.isAllGranted()

    private fun getAllGranted() = musicRepository.getAllGranted()

    fun saveAllGranted(allGranted: Boolean) {
        _allGranted.value = allGranted
        MusicConfigManager.saveAllGranted(allGranted)
    }

    override fun onEvent(event: BaseEvent) {
        when (event) {
            is MainEvent.InitPlaylist ->{
                _initPlaylistFlag.value = event.flag
                if (_initPlaylistFlag.value == false){
                    launch {
                        val songs = musicRepository.getMusicInfos(PlaylistType.HISPLAY.toString())
                        if (songs != null && songs.isNotEmpty()) {
                            onEvent(MainEvent.ClickPlay(songs, songs[0]))
                        }
                    }
                }else{
                    val isPlaying = playbackState.value?.isPlaying ?: false
                    if (isPlaying) {
                        curPlayingMusic.value?.let { playOrToggleSong(it, toggle = true) }
                    }
                }
            }
            is MainEvent.ClickPlay -> {
                event.musicInfos.toMutableList()?.apply {
                    if (event.musicInfo.id != curPlayingSong.value?.id){
                        //正在播放的歌单是不是现在显示的歌单
                        val pid = event.musicInfo.pid
                        val fetchFlag = (_playlistId.value == pid && pid != "")
                        //原来不是同一个那就刷新数据，但是存在新增歌单歌曲但是歌曲数据源没更新情况，so...
                        musicSource.fetchMediaData(this@apply)
                        if (!fetchFlag) {
                            pid?.let {
                                _playlistId.value = pid
                            }
                        }
                        if (!fetchFlag) {
                            subscribe()//获取新的数据
                        }
                    }
                    playOrToggleSong(event.musicInfo)
                }
            }
            is MainEvent.InitPlayMode -> {
                switchPlayMode(_playMode.value)
            }
            is MainEvent.UpdatePlayMode -> {
                //默认顺序播放
                var playMode = _playMode.value?: musicRepository.getPlayMode()
                if (playMode == PlaybackStateCompat.REPEAT_MODE_ALL){
                    playMode = 0
                }else{
                    if (playMode > 2){
                        playMode = 0
                    }else{
                        playMode++
                    }
                }
                switchPlayMode(playMode)
                subscribe()
                musicRepository.savePlayMode(playMode)
                _playMode.value = playMode
            }
            is MainEvent.AddQueueItem -> {
                if (event.index != null){
                    musicServiceConnection.mediaController.addQueueItem(event.description, event.index)
                }else{
                    musicServiceConnection.mediaController.addQueueItem(event.description)
                }
            }
            is MainEvent.DownloadMusic -> {
                for (i in event.musicInfos.indices){
                    downloadMusic(event.musicInfos[i])
                }
            }
        }
    }

    private fun switchPlayMode(playMode: Int?){
        when (playMode) {
            PlaybackStateCompat.REPEAT_MODE_NONE -> { //0
                musicServiceConnection.transportControls.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ALL)
                musicServiceConnection.transportControls.setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_NONE)
            }
            PlaybackStateCompat.REPEAT_MODE_ONE -> { //1
                musicServiceConnection.transportControls.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ONE)
                musicServiceConnection.transportControls.setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_NONE)
            }
            PlaybackStateCompat.REPEAT_MODE_ALL -> {//2
                musicServiceConnection.transportControls.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ALL)
                musicServiceConnection.transportControls.setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_ALL)
            }
            else -> {}
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
        if(isPrepared && musicInfo.id == curPlayingSong.value?.id) {
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

    private fun downloadMusic(m: MusicInfo){
        launch {
            toast("开始下载下载歌曲：${m.name}")
            val downloadMusic = musicRepository.downloadMusic(m)
            downloadMusic?.apply {
                this.catch { LogE("catch... when searching", t = it) }
                .onEach {
                    when(it.status){
                        Status.SUCCESS -> {
                            LogE("下载文件:$it")
                            toast("下载歌曲：${m.name}成功")
                        }
                        Status.LOADING -> {
                            val num = NumberFormat.getPercentInstance()
                            num.maximumFractionDigits = 2
                            LogE("下载歌曲${m.name}进度:"+
                                    num.format(it.process))
                        }
                        Status.ERROR -> {
                            toast("下载歌曲：${m.name}失败")
                        }
                    }
                }
                .flowOn(Dispatchers.Main)
                .launchIn(viewModelScope)
            }?:toast(getString(R.string.error_connection))
        }
    }

    private fun updateCurrentPlayerPosition() {
        viewModelScope.launch {
            while(true) {
                val pos = playbackState.value?.currentPlaybackPosition
                if(curPlayerPosition.value != pos) {
                    _curPlayerPosition.postValue(pos?:0L)
                }
                if(_curSongDuration.value != MusicService.curSongDuration){
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












