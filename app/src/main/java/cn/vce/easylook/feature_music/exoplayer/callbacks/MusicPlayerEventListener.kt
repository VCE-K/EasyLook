package cn.vce.easylook.feature_music.exoplayer.callbacks

import android.support.v4.media.MediaMetadataCompat
import android.widget.Toast
import cn.vce.easylook.feature_music.exoplayer.MusicService
import cn.vce.easylook.feature_music.exoplayer.MusicSource
import cn.vce.easylook.feature_music.exoplayer.toSong
import cn.vce.easylook.utils.LogE
import cn.vce.easylook.utils.mediaUri
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import kotlinx.coroutines.*


//用于监听播放器的各种状态和事件，如播放状态变化、加载状态变化、播放错误、播放位置变化等
class MusicPlayerEventListener(
    private val musicService: MusicService,
    private val musicSource: MusicSource,
    private val playerPrepared: (MediaMetadataCompat?) -> Unit
) : Player.EventListener {
    private val currentWindowIndex
        get() = musicService.exoPlayer.currentWindowIndex
    override fun onPositionDiscontinuity(reason: Int) {
        /*DISCONTINUITY_REASON_PERIOD_TRANSITION=0: 进入新的时间段（period或track）。
        DISCONTINUITY_REASON_SEEK=1: 执行了媒体跳转操作。
        DISCONTINUITY_REASON_SEEK_ADJUSTMENT=2: 跳转操作针对“拖动进度条”行为进行了调整。
        DISCONTINUITY_REASON_INTERNAL=3: 发生某个内部组件状态变化（例如加载特殊类型的节目）
        DISCONTINUITY_REASON_AD_INSERTION=4: 广告插入导致跳过部分内容
        DISCONTINUITY_REASON_REMOVE=5: “播放下一个”等操作将当前节目从播放列表中移除。*/

        super.onPositionDiscontinuity(reason)


        when( reason ) {
            Player.DISCONTINUITY_REASON_PERIOD_TRANSITION -> {
                var nextSongIndex = currentWindowIndex
                fetchNext(nextSongIndex)
            }
            else -> Unit
        }
    }

    private fun fetchNext(nextSongIndex: Int){
        if (nextSongIndex < musicSource.songs.size) {
            val itemToPlay = musicSource.songs[nextSongIndex]
            playerPrepared(itemToPlay)
        }
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
            Player.STATE_ENDED ->
                LogE("Music","播放完成"+musicService.exoPlayer.contentPosition/1000)
        }
        if(playbackState == Player.STATE_READY && !playWhenReady) {//准备完毕and 准备就绪时播放
            //将正在运行的服务从前台转移到后台
            musicService.stopForeground(false)
        }
    }


    override fun onPlayerError(error: ExoPlaybackException) {
        super.onPlayerError(error)
        musicService.curPlayingSong?.mediaUri?.let {
            Toast.makeText(musicService, "load failed", Toast.LENGTH_LONG).show()
            return
        }
        Toast.makeText(musicService, "An unknown error occured", Toast.LENGTH_LONG).show()
    }
}