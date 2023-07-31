package cn.vce.easylook.feature_music.exoplayer

import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import cn.vce.easylook.feature_music.models.MusicInfo
import cn.vce.easylook.feature_music.models.PlaylistType
import cn.vce.easylook.feature_music.other.Constants.NETWORK_ERROR
import cn.vce.easylook.feature_music.other.Event
import cn.vce.easylook.feature_music.other.Resource
import cn.vce.easylook.feature_music.repository.MusicRepository
import cn.vce.easylook.utils.id
import cn.vce.easylook.utils.toast
import kotlinx.coroutines.*

class MusicServiceConnection(
    context: Context,
    private val musicRepository: MusicRepository,
    private val musicSource: MusicSource
) {
    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private val _isConnected = MutableLiveData<Event<Resource<Boolean>>>()
    val isConnected: LiveData<Event<Resource<Boolean>>> = _isConnected

    private val _networkError = MutableLiveData<Event<Resource<Boolean>>>()
    val networkError: LiveData<Event<Resource<Boolean>>> = _networkError

    private val _playbackState = MutableLiveData<PlaybackStateCompat?>()
    val playbackState: LiveData<PlaybackStateCompat?> = _playbackState

    private val _curPlayingSong = MutableLiveData<MediaMetadataCompat?>()
    val curPlayingSong: LiveData<MediaMetadataCompat?> = _curPlayingSong

    private val _songList = MutableLiveData<List<MusicInfo>>()
    val songList: LiveData<List<MusicInfo>> = _songList
    
    lateinit var mediaController: MediaControllerCompat

    private val mediaBrowserConnectionCallback = MediaBrowserConnectionCallback(context)

    private val mediaBrowser = MediaBrowserCompat(
        context,
        ComponentName(
            context,
            MusicService::class.java
        ),
        mediaBrowserConnectionCallback,
        null
    ).apply {
        connect()
    }

    val transportControls: MediaControllerCompat.TransportControls
        get() = mediaController.transportControls

    //订阅
    fun subscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback) {
        mediaBrowser.subscribe(parentId, callback)
    }

    //解除订阅
    fun unsubscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback) {
        mediaBrowser.unsubscribe(parentId, callback)
    }

    private inner class MediaBrowserConnectionCallback(
        private val context: Context
    ) : MediaBrowserCompat.ConnectionCallback() {

        //连接服务成功
        override fun onConnected() {
            Log.d("MusicServiceConnection", "CONNECTED")
            mediaController = MediaControllerCompat(context, mediaBrowser.sessionToken).apply {
                //为媒体控制器注册回调接口
                registerCallback(MediaContollerCallback())
            }
            _isConnected.postValue(Event(Resource.success(true)))
        }

        //连接中断
        override fun onConnectionSuspended() {
            Log.d("MusicServiceConnection", "SUSPENDED")
            _isConnected.postValue(
                Event(
                    Resource.error(
                "The connection was suspended", false
            ))
            )
        }
        //连接失败
        override fun onConnectionFailed() {
            Log.d("MusicServiceConnection", "FAILED")
            serviceScope.cancel()
            _isConnected.postValue(
                Event(
                    Resource.error(
                "Couldn't connect to media browser", false
            ))
            )
        }
    }

    private inner class MediaContollerCallback : MediaControllerCompat.Callback() {
        //播放状态变更
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            _playbackState.postValue(state)
        }

        override fun onQueueChanged(queue: MutableList<MediaSessionCompat.QueueItem>?) {
            super.onQueueChanged(queue)
            _songList.value = queue?.toMusicInfo()
        }


        //当前媒体数据变更
        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            //在这里记录历史记录
            val music = metadata?.toMusicInfo()
            music?.let {
                if (it.id.isNotEmpty()){
                    val index = musicSource.musicInfos.indexOfFirst { musicInfo ->
                        it.id == musicInfo.id
                    }
                    if (index == -1){
                        toast(index.toString())
                    }
                    val musicInfo = musicSource.musicInfos[index].copy()
                    if (musicInfo.id != _curPlayingSong.value?.id ?: ""){
                        serviceScope.launch{
                            //如果原来的pid就不是空的，那这里要修改total
                            musicInfo.pid?.let {
                                val playlistInfo = musicRepository.getPlaylist(musicInfo.pid)
                                playlistInfo?.let {
                                    playlistInfo.playCount += 1
                                    musicRepository.insertPlaylistInfo(playlistInfo)
                                }
                            }
                            musicInfo.pid = PlaylistType.HISPLAY.toString()
                            musicRepository.insertMusicInfo(musicInfo)
                        }
                        _curPlayingSong.value = metadata
                    }
                }
            }
        }
        //网络错误监控
        override fun onSessionEvent(event: String?, extras: Bundle?) {
            super.onSessionEvent(event, extras)
            when(event) {
                NETWORK_ERROR -> _networkError.postValue(
                    Event(
                        Resource.error(
                            "Couldn't connect to the server. Please check your internet connection.",
                            null
                        )
                    )
                )
            }
        }


        override fun onSessionDestroyed() {
            mediaBrowserConnectionCallback.onConnectionSuspended()
        }
    }
}

















