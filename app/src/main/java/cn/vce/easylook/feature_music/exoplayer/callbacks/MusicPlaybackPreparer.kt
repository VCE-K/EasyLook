package cn.vce.easylook.feature_music.exoplayer.callbacks

import android.net.Uri
import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import cn.vce.easylook.feature_music.exoplayer.FirebaseMusicSource
import com.google.android.exoplayer2.ControlDispatcher
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import kotlinx.coroutines.*


/*MusicPlaybackPreparer（音乐播放准备器） 类，它的主要作用是以异步方式从 Firebase 数据库中获取音乐数据，并通知应用程序使用新的数据来更新元数据和 UI。*/
class MusicPlaybackPreparer(
    private val firebaseMusicSource: FirebaseMusicSource,
    private val playerPrepared: (MediaMetadataCompat?) -> Unit
) : MediaSessionConnector.PlaybackPreparer {


    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
    //onCommand() 方法在这里始终返回 false，表明该类不支持额外的命令。
    override fun onCommand(
        player: Player,
        controlDispatcher: ControlDispatcher,
        command: String,
        extras: Bundle?,
        cb: ResultReceiver?
    ) = false

    //getSupportedPrepareActions() 方法指定了可供指令集中支持该方法的命令，
    //ACTION_PREPARE_FROM_MEDIA_ID 表示从媒体 ID 准备播放，而 ACTION_PLAY_FROM_MEDIA_ID 则表示从媒体 ID 开始播放
    override fun getSupportedPrepareActions(): Long {
        return PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID or
                PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID
    }

    override fun onPrepare(playWhenReady: Boolean) = Unit
    /*
    onPrepareFromMediaId() 方法会在播放器准备好之后被调用，
    该函数通过 FirebaseMusicSource 对象获取可播放的音乐列表，
    找到与给定的 mediaId 相匹配的元素，
    并在完成之后通过调用 playerPrepared() 函数将该元素传递给钩子函数。
     */
    override fun onPrepareFromMediaId(mediaId: String, playWhenReady: Boolean, extras: Bundle?) {
        firebaseMusicSource.whenReady {
            val itemToPlay = firebaseMusicSource.songs.find { mediaId == it.description.mediaId }
            itemToPlay?.let {
                serviceScope.launch {
                    withContext(Dispatchers.Default) {
                        firebaseMusicSource.getUrl(it)
                    }
                    playerPrepared(itemToPlay)
                }
            }
        }
    }


    override fun onPrepareFromSearch(query: String, playWhenReady: Boolean, extras: Bundle?) = Unit

    override fun onPrepareFromUri(uri: Uri, playWhenReady: Boolean, extras: Bundle?) = Unit

}















