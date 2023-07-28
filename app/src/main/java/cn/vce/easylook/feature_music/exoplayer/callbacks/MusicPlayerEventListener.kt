package cn.vce.easylook.feature_music.exoplayer.callbacks

import android.support.v4.media.MediaMetadataCompat
import cn.vce.easylook.feature_music.exoplayer.MusicService
import cn.vce.easylook.feature_music.exoplayer.MusicSource
import cn.vce.easylook.feature_music.other.MusicConfigManager
import cn.vce.easylook.utils.LogE
import cn.vce.easylook.utils.id
import cn.vce.easylook.utils.toast
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import kotlinx.coroutines.*


//用于监听播放器的各种状态和事件，如播放状态变化、加载状态变化、播放错误、播放位置变化等
class MusicPlayerEventListener(
    private val musicService: MusicService,
    private val playerPrepared: (MediaMetadataCompat?) -> Unit,
    private val updatePlayQueue: () -> Unit
) :  Player.Listener {


    private val musicSource = musicService.musicSource

    private val playMode: Int
        get() = MusicConfigManager.getPlayMode()

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)


    private var playIndex: Int = 0
    override fun onPositionDiscontinuity(reason: Int) {
        /*DISCONTINUITY_REASON_PERIOD_TRANSITION=0: 进入新的时间段（period或track）。
        DISCONTINUITY_REASON_SEEK=1: 执行了媒体跳转操作。
        DISCONTINUITY_REASON_SEEK_ADJUSTMENT=2: 跳转操作针对“拖动进度条”行为进行了调整。
        DISCONTINUITY_REASON_INTERNAL=3: 发生某个内部组件状态变化（例如加载特殊类型的节目）
        DISCONTINUITY_REASON_AD_INSERTION=4: 广告插入导致跳过部分内容
        DISCONTINUITY_REASON_REMOVE=5: “播放下一个”等操作将当前节目从播放列表中移除。*/
        super.onPositionDiscontinuity(reason)
    }

    private var isFirstIdle = false
    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        super.onPlayerStateChanged(playWhenReady, playbackState)
        when (playbackState) {
            Player.STATE_IDLE -> {
                LogE("Music", "空闲状态" + musicService.exoPlayer.contentPosition / 1000)
                /*if (playIndex != musicService.exoPlayer.currentMediaItemIndex){

                }*/
                if (!isFirstIdle){
                    musicSource.whenReady {
                        serviceScope.launch {
                            playIndex = musicService.exoPlayer.currentMediaItemIndex
                            /*if (playMode == MusicConfigManager.PLAY_MODE_RANDOM) {
                                playIndex = musicSource.getShuffleSong()
                            }*/
                            musicSource.fetchSongUrl(playIndex, false)
                            /*playerPrepared(musicSource.songs[playIndex])*/
                            updatePlayQueue()
                            isFirstIdle = true
                        }
                    }
                }else{
                    isFirstIdle = false
                }

            }
            Player.STATE_BUFFERING ->
                LogE("Music","加载中"+musicService.exoPlayer.contentPosition/1000)
            Player.STATE_READY ->
                LogE("Music","准备完毕"+musicService.exoPlayer.contentPosition/1000)
            Player.STATE_ENDED -> {
                //REPEAT_MODE_ALL情况下播放全部列表歌曲之后才有这个状态
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