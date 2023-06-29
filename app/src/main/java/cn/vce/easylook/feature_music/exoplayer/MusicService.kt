package cn.vce.easylook.feature_music.exoplayer

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.media.MediaBrowserServiceCompat
import cn.vce.easylook.feature_music.exoplayer.callbacks.MusicPlaybackPreparer
import cn.vce.easylook.feature_music.exoplayer.callbacks.MusicPlayerEventListener
import cn.vce.easylook.feature_music.exoplayer.callbacks.MusicPlayerNotificationListener
import cn.vce.easylook.feature_music.other.Constants.MEDIA_ROOT_ID
import cn.vce.easylook.feature_music.other.Constants.NETWORK_ERROR
import cn.vce.easylook.feature_music.other.MusicConfigManager
import cn.vce.easylook.utils.id
import com.google.android.exoplayer2.ControlDispatcher
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject


private const val SERVICE_TAG = "MusicService"

@AndroidEntryPoint
class MusicService : MediaBrowserServiceCompat() {

    @Inject
    lateinit var dataSourceFactory: DefaultDataSourceFactory

    @Inject
    lateinit var exoPlayer: SimpleExoPlayer

    @Inject
    lateinit var musicSource: MusicSource

    private lateinit var musicNotificationManager: MusicNotificationManager

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaSessionConnector: MediaSessionConnector

    var isForegroundService = false

    var curPlayingSong: MediaMetadataCompat? = null

    private var isPlayerInitialized = false

    private lateinit var musicPlayerEventListener: MusicPlayerEventListener

    companion object {
        var curSongDuration = 0L
            private set
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onCreate() {
        super.onCreate()

        val activityIntent = packageManager?.getLaunchIntentForPackage(packageName)?.let {sessionIntent ->
            PendingIntent.getActivity(this, 0, sessionIntent, PendingIntent.FLAG_IMMUTABLE)
        }

        mediaSession = MediaSessionCompat(this, SERVICE_TAG).apply {
            setSessionActivity(activityIntent)
            isActive = true
        }
        sessionToken = mediaSession.sessionToken

        musicNotificationManager = MusicNotificationManager(
            this,
            mediaSession.sessionToken,
            MusicPlayerNotificationListener(this)
        ) {
            curSongDuration = exoPlayer.duration
        }

        val musicPlaybackPreparer = MusicPlaybackPreparer(musicSource) {
            curPlayingSong = it
            preparePlayer(
                musicSource.songs,
                it,
                true
            )
        }
        //MediaSessionConnector当中会注册 mediaSession的callback
        mediaSessionConnector = MediaSessionConnector(mediaSession)
        //PlaybackPreparer和QueueNavigator会被mediaSession的callback不同实现调用
        mediaSessionConnector.setPlaybackPreparer(musicPlaybackPreparer)
        mediaSessionConnector.setQueueNavigator(MusicQueueNavigator())
        mediaSessionConnector.setPlayer(exoPlayer)

        musicPlayerEventListener = MusicPlayerEventListener(this, musicSource) {
            curPlayingSong = it
            preparePlayer(
                musicSource.songs,
                it,
                true
            )
        }
        exoPlayer.addListener(musicPlayerEventListener)
        musicNotificationManager.showNotification(exoPlayer)
    }

    private inner class MusicQueueNavigator : TimelineQueueNavigator(mediaSession) {

        //确保当用户通过媒体控制器（如前进，后退，手动选择）从媒体序列中选择文件时，系统会提供正确的元数据和标签，这对于用户在应用程序中导航和使用提供良好的体验。
        override fun getMediaDescription(player: Player, windowIndex: Int): MediaDescriptionCompat {
            return musicSource.songs[windowIndex].description
        }


        //上一首
        override fun onSkipToPrevious(player: Player, controlDispatcher: ControlDispatcher) {
            //super.onSkipToPrevious(player, controlDispatcher)
            val previousWindowIndex = player.previousWindowIndex
            val itemToPlay = musicSource.songs[previousWindowIndex]
            preparePlayer( musicSource.songs, itemToPlay, true, previousFlag = true)
        }

        override fun onSkipToQueueItem(
            player: Player,
            controlDispatcher: ControlDispatcher,
            id: Long
        ) {
            super.onSkipToQueueItem(player, controlDispatcher, id)
        }

        //下一首
        override fun onSkipToNext(player: Player, controlDispatcher: ControlDispatcher) {
            //super.onSkipToNext(player, controlDispatcher)
            if (MusicConfigManager.getPlayMode() == MusicConfigManager.PLAY_MODE_RANDOM){
                val itemToPlay = musicSource.getShuffleSong()
                preparePlayer(
                    musicSource.songs,
                    itemToPlay,
                    true
                )
            }else{
                val nextWindowIndex = player.nextWindowIndex
                val itemToPlay = musicSource.songs[nextWindowIndex]
                preparePlayer( musicSource.songs, itemToPlay, true)
            }
        }
    }

    private fun preparePlayer(
        songs: List<MediaMetadataCompat>,
        itemToPlay: MediaMetadataCompat?,
        playNow: Boolean,
        previousFlag: Boolean = false
    ) {
        val curSongIndex = if(curPlayingSong == null)
            0
        else {
            songs.indexOfFirst { metadata ->
                metadata.id == itemToPlay?.id
            }
        }

        serviceScope.launch {
            exoPlayer.playWhenReady = false // 暂停播放器，等待当前曲目完全播放结束
            withContext(Dispatchers.IO) {
                musicSource.fetchSongUrl(curSongIndex, previousFlag = previousFlag)
            }
            exoPlayer.prepare(musicSource.asMediaSource(dataSourceFactory))
            exoPlayer.seekTo(curSongIndex, 0L)
            exoPlayer.playWhenReady = playNow
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        exoPlayer.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        exoPlayer.removeListener(musicPlayerEventListener)
        exoPlayer.release()
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        return BrowserRoot(MEDIA_ROOT_ID, null)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        when(parentId) {
            MEDIA_ROOT_ID -> {
                val resultsSent = musicSource.whenReady { isInitialized ->
                    if(isInitialized) {
                        result.sendResult(musicSource.asMediaItems())
                        if(!isPlayerInitialized && musicSource.songs.isNotEmpty()) {
                            preparePlayer(musicSource.songs, musicSource.songs[0], false)
                            isPlayerInitialized = true
                        }
                    } else {
                        mediaSession.sendSessionEvent(NETWORK_ERROR, null)
                        result.sendResult(null)
                    }
                }
                if(!resultsSent) {
                    result.detach()
                }
            }
        }
    }
}