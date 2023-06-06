package cn.vce.easylook.feature_music.exoplayer.callbacks

import android.util.Log
import android.widget.Toast
import cn.vce.easylook.feature_music.data.Repository
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import cn.vce.easylook.feature_music.exoplayer.MusicService
import cn.vce.easylook.feature_music.exoplayer.toSong
import cn.vce.easylook.utils.LogE

//用于监听播放器的各种状态和事件，如播放状态变化、加载状态变化、播放错误、播放位置变化等
class MusicPlayerEventListener(
    private val musicService: MusicService
) : Player.EventListener {

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        super.onPlayerStateChanged(playWhenReady, playbackState)
        when (playbackState) {
            Player.STATE_BUFFERING ->
                LogE("Music","加载中")
            Player.STATE_READY ->
                LogE("Music","准备完毕")
            Player.STATE_ENDED ->
                LogE("Music","播放完成")
        }
        if(playbackState == Player.STATE_READY && !playWhenReady) {//准备完毕and 准备就绪时播放
            //将正在运行的服务从前台转移到后台
            musicService.stopForeground(false)
        }
    }


    override fun onPlayerError(error: ExoPlaybackException) {
        super.onPlayerError(error)
        Toast.makeText(musicService, "An unknown error occured", Toast.LENGTH_LONG).show()
    }
}