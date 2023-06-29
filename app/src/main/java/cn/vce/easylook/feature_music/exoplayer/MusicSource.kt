package cn.vce.easylook.feature_music.exoplayer

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import cn.vce.easylook.feature_music.models.MusicInfo
import cn.vce.easylook.feature_music.exoplayer.State.*
import cn.vce.easylook.feature_music.repository.MusicRepository
import cn.vce.easylook.utils.mediaUri
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.random.Random

class MusicSource(
    private val repository: MusicRepository
) {

    var musicInfos = mutableListOf<MusicInfo>()
    var songs = mutableListOf<MediaMetadataCompat>()
        get() = musicInfos.transSongs()
    suspend fun fetchMediaData(
        list: MutableList<MusicInfo>
    ) = withContext(Dispatchers.IO) {
        if (state == STATE_CREATED) {
            state = STATE_INITIALIZING
            musicInfos = list
            state = STATE_INITIALIZED
        } else if (state == STATE_INITIALIZED) {
            musicInfos = list
        }
    }

    /**
     * nextSong: 要拿到url的媒体
     * fetchNext： 是否继续更新媒体
     * previousFlag： false继续往下更新， true往上更新
     */
    suspend fun fetchSongUrl(
        songIndex: Int,
        fetchNext: Boolean = true,
        previousFlag: Boolean = false
    ){
        val nextSong = songs[songIndex]
        nextSong.let {
            musicInfos[songIndex].apply {
                songId?:id?.let {
                    if (songUrl?.isBlank() == true || songUrl == null) {
                        repository.getMusicUrl(it)?.let{ url ->
                            replaceSongUrl(this, url)
                        }
                    }
                }
            }
        }
        if (fetchNext){
            var nextSongIndex: Int = if (previousFlag){
                if ((songIndex - 1) < 0 ){//到达开头
                    songs.size - 1
                }else {
                    songIndex - 1
                }
            } else{
                if ((songIndex + 1) == songs.size ){
                    0
                }else {
                    songIndex + 1
                }
            }
            fetchSongUrl(nextSongIndex, false)
        }
    }


    /*********** 随机播放相关开始 ***********/
    /**
     * 随机播放具体简陋算法
     */
    fun List<Any>.shuffle() {
        val rand = Random
        val array = this.toMutableList()
        for (i in array.size - 1 downTo 1) {
            val j: Int = rand.nextInt(i + 1)
            // 交换 array[i] 和 array[j]
            val temp = array[i]
            array[i] = array[j]
            array[j] = temp
        }
    }



    fun getShuffleSong(): MediaMetadataCompat {
        val rand = Random
        val i: Int = rand.nextInt(songs.size)
        return songs[i]
    }

    /*********** 随机播放相关结束 ***********/
    private fun replaceSongUrl(musicInfo: MusicInfo, url: String){
        musicInfo.songUrl = url
        val playIndex = musicInfos.indexOfFirst { item ->
            (item.songId ?: item.id) == (musicInfo.songId ?: musicInfo.id)
        }
        musicInfos[playIndex] = musicInfo
    }

    fun asMediaSource(dataSourceFactory: DefaultDataSourceFactory): ConcatenatingMediaSource {
        val concatenatingMediaSource = ConcatenatingMediaSource()
        songs.forEach { song ->
            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(song.mediaUri)
            concatenatingMediaSource.addMediaSource(mediaSource)
        }
        return concatenatingMediaSource
    }

    fun asMediaItems() = songs.map { song ->
        val desc = MediaDescriptionCompat.Builder()
            .setMediaUri(song.mediaUri)
            .setTitle(song.description.title)
            .setSubtitle(song.description.subtitle)
            .setMediaId(song.description.mediaId)
            .setIconUri(song.description.iconUri)
            .build()
        MediaBrowserCompat.MediaItem(desc, FLAG_PLAYABLE)
    }.toMutableList()

    private val onReadyListeners = mutableListOf<(Boolean) -> Unit>()

    private var state: State = STATE_CREATED
        @Synchronized
        set(value) {
            if (value == STATE_INITIALIZED || value == STATE_ERROR) {//初始化好了或者错误都会走下面，但是只有STATE_INITIALIZED才会走回调
                synchronized(onReadyListeners) {
                    field = value
                    onReadyListeners.forEach { listener ->
                        listener(state == STATE_INITIALIZED)
                    }
                }
            } else {
                field = value
            }
        }

    fun whenReady(action: (Boolean) -> Unit): Boolean {
        return if (state == STATE_CREATED || state == STATE_INITIALIZING) {
            onReadyListeners += action
            false
        } else {
            action(state == STATE_INITIALIZED)
            true
        }
    }
}

enum class State {
    STATE_CREATED,
    STATE_INITIALIZING,
    STATE_INITIALIZED,
    STATE_ERROR
}















