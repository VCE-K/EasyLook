package cn.vce.easylook.feature_music.exoplayer.callbacks

import android.support.v4.media.MediaMetadataCompat
import cn.vce.easylook.feature_music.exoplayer.MusicService
import cn.vce.easylook.feature_music.exoplayer.MusicSource
import cn.vce.easylook.feature_music.other.MusicConfigManager
import cn.vce.easylook.utils.LogE
import cn.vce.easylook.utils.toast
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import kotlinx.coroutines.*


//用于监听播放器的各种状态和事件，如播放状态变化、加载状态变化、播放错误、播放位置变化等
class MusicPlayerEventListener(
    private val musicService: MusicService,
    private val musicSource: MusicSource,
    private val playerPrepared: (MediaMetadataCompat?) -> Unit
) : Player.EventListener {

    private val playMode: Int
        get() = MusicConfigManager.getPlayMode()

    private var currentWindowIndex = 0

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)


    override fun onPositionDiscontinuity(reason: Int) {
        /*DISCONTINUITY_REASON_PERIOD_TRANSITION=0: 进入新的时间段（period或track）。
        DISCONTINUITY_REASON_SEEK=1: 执行了媒体跳转操作。
        DISCONTINUITY_REASON_SEEK_ADJUSTMENT=2: 跳转操作针对“拖动进度条”行为进行了调整。
        DISCONTINUITY_REASON_INTERNAL=3: 发生某个内部组件状态变化（例如加载特殊类型的节目）
        DISCONTINUITY_REASON_AD_INSERTION=4: 广告插入导致跳过部分内容
        DISCONTINUITY_REASON_REMOVE=5: “播放下一个”等操作将当前节目从播放列表中移除。*/
        when( reason ) {
            Player.DISCONTINUITY_REASON_AUTO_TRANSITION -> {//自动切换
                if (currentWindowIndex != musicService.exoPlayer.currentWindowIndex) {
                    serviceScope.launch {
                        if (playMode == MusicConfigManager.PLAY_MODE_RANDOM) {
                            /*val playIndex = musicService.exoPlayer.currentWindowIndex + 1*/
                            val playIndex = musicSource.getShuffleSong()
                            withContext(Dispatchers.IO) {
                                musicSource.fetchSongUrl(playIndex, false)
                            }
                            playerPrepared(musicSource.songs[playIndex])
                        } else if (playMode == MusicConfigManager.REPEAT_MODE_ALL) {
                            //拿到url
                            var playIndex = musicService.exoPlayer.currentWindowIndex
                            withContext(Dispatchers.IO) {
                                musicSource.fetchSongUrl(playIndex, false)
                            }
                            playerPrepared(musicSource.songs[playIndex])
                        }else if (playMode == MusicConfigManager.REPEAT_MODE_ONE) {
                            //单曲循环不用做任何操作
                        }
                        currentWindowIndex = musicService.exoPlayer.currentWindowIndex
                    }
                }
            }
            Player.DISCONTINUITY_REASON_SEEK -> Unit //第一次初始化跳转或者点击
            else -> Unit
        }

        super.onPositionDiscontinuity(reason)

    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        super.onPlayerStateChanged(playWhenReady, playbackState)
        when (playbackState) {
            Player.STATE_IDLE ->
                LogE("Music","空闲状态"+musicService.exoPlayer.contentPosition/1000)
            Player.STATE_BUFFERING ->
                LogE("Music","加载中"+musicService.exoPlayer.contentPosition/1000)
            Player.STATE_READY ->
                LogE("Music","准备完毕"+musicService.exoPlayer.contentPosition/1000)
            Player.STATE_ENDED -> {
                //REPEAT_MODE_ALL情况下播放全部列表歌曲之后才有这个状态
                /*if (playMode == MusicConfigManager.PLAY_MODE_RANDOM){
                    val itemToPlay = musicSource.getShuffleSong()
                    playerPrepared(itemToPlay)
                }*/
                LogE("Music","播放完成"+musicService.exoPlayer.contentPosition/1000)
            }
        }
        if(playbackState == Player.STATE_READY && !playWhenReady) {//准备完毕and 准备就绪时播放
            //将正在运行的服务从前台转移到后台
            musicService.stopForeground(false)
        }
    }


    override fun onPlayerError(error: PlaybackException) {
        super.onPlayerError(error)
        //toast(getString(R.string.unknown_error))
    }

}