package cn.vce.easylook.feature_music.exoplayer

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import cn.vce.easylook.R
import cn.vce.easylook.feature_music.exoplayer.State.*
import cn.vce.easylook.feature_music.models.MusicInfo
import cn.vce.easylook.feature_music.models.MusicSourceType
import cn.vce.easylook.feature_music.models.bli.download.Audio
import cn.vce.easylook.feature_music.repository.MusicRepository
import cn.vce.easylook.utils.*
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import java.io.File
import java.net.URI
import kotlin.random.Random

class MusicSource(
    private val repository: MusicRepository
) {

    var musicInfos = mutableListOf<MusicInfo>()
    private var mShufflePlaylistSequence: MutableList<MediaMetadataCompat> = mutableListOf()
    val songList: MutableList<MediaMetadataCompat>
        get() {
            val repeatMode = repository.getPlayMode()
            return if (repeatMode == PlaybackStateCompat.REPEAT_MODE_ALL) {
                mShufflePlaylistSequence
                musicInfos.transSongs()
            }else{
                musicInfos.transSongs()
            }
        }


    //当前历史记录集合
    private val playedSongs = mutableListOf<MediaMetadataCompat>()
    fun fetchMediaData(
        list: MutableList<MusicInfo>
    ) {
        if (state == STATE_CREATED) {
            state = STATE_INITIALIZING
            musicInfos = list
            mShufflePlaylistSequence = musicInfos.transSongs().also { it.shuffle() }
            state = STATE_INITIALIZED
        } else if (state == STATE_INITIALIZED) {
            musicInfos = list
            mShufflePlaylistSequence = musicInfos.transSongs().also { it.shuffle() }
        }
        //清理已经播放过的歌曲清单
        playedSongs.clear()
    }

    /**
     * nextSong: 要拿到url的媒体
     * fetchNext： 是否继续更新媒体
     * previousFlag： false继续往下更新， true往上更新
     */
    suspend fun fetchSongUrl(
        previousIndex: Int? = null,
        songIndex: Int,
        nextIndex: Int? = null,
        fetchNext: Boolean = true
    ){
        val nextSong = songList[songIndex]
        nextSong.let {
            val m = musicInfos.find {
                it.id == nextSong.id
            }
            m?.apply {
                repository.getMusicUrl(this)
            }
        }
        if (fetchNext){
            if (previousIndex != null && previousIndex != -1 && previousIndex < songList.size) {
                fetchSongUrl(songIndex = previousIndex, fetchNext = false)
            }else if (previousIndex != null && previousIndex == -1) {
                fetchSongUrl(songIndex = 0, fetchNext = false)
            }

            if (nextIndex != null && nextIndex != -1 && nextIndex < songList.size) {
                fetchSongUrl(songIndex = nextIndex, fetchNext = false)
            }else if (nextIndex != null && nextIndex == -1) {
                fetchSongUrl(songIndex = (songList.size - 1), fetchNext = false)
            }
            /*val previousIndex: Int = if ((songIndex - 1) < 0 ){//到达开头
                songList.size - 1
            }else {
                songIndex - 1
            }
            val nextSongIndex= if ((songIndex + 1) == songList.size ){
                0
            }else {
                songIndex + 1
            }

            if (songIndex == previousIndex && songIndex == nextSongIndex){
                return
            }else if (previousIndex == nextSongIndex){
                fetchSongUrl(songIndex = nextSongIndex,fetchNext = false)
            }else{
                fetchSongUrl(songIndex =previousIndex, fetchNext = false)
                fetchSongUrl(songIndex = nextSongIndex,  fetchNext = false)
            }*/
        }
    }


    /*********** 随机播放相关开始 ***********/
    /**
     * 随机播放具体简陋算法
     */

    fun getPreviousShuffleSong(currentIndex: Int): MediaMetadataCompat {
        var previousIndex: Int
        val currentSong = songList[currentIndex]
        //看看现在播放的这首歌有没有播放过
        val index = playedSongs.indexOfFirst {
            currentSong.id == it.id
        }
        return if (index == -1){
            previousIndex = playedSongs.size - 1
            playedSongs[previousIndex]
        }else{
            previousIndex = if (index == 0){
                0
            }else{
                index - 1
            }
            playedSongs[previousIndex]
        }
    }

    /*fun getShuffleSong(): Int {
        if (songs.isEmpty()) {
            // 没有可播放的歌曲
            //return "No songs available"
        }
        if (playedSongs.size == songs.size) {
            // 所有歌曲都已播放
            // 这里可以选择重新随机播放或者结束播放
            playedSongs.clear()
        }
        var nextSong: MediaMetadataCompat

        var randomIndex = Random.nextInt(songs.size)
        do {
            nextSong = songs[randomIndex]
            randomIndex = Random.nextInt(songs.size)
            //播放过的肯定有url了，一模一样
        } while (playedSongs.contains(nextSong))
        playedSongs.add(nextSong)
        return randomIndex
    }*/

    /*********** 随机播放相关结束 ***********/

    fun asMediaSource(dataSourceFactory: DefaultDataSourceFactory): ConcatenatingMediaSource {
        val concatenatingMediaSource = ConcatenatingMediaSource()
        songList.forEach { song ->
            var mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(song.mediaUri)
            concatenatingMediaSource.addMediaSource(mediaSource)
        }
        return concatenatingMediaSource
    }

    fun asMediaItems(): MutableList<MediaBrowserCompat.MediaItem> {
        return songList.map { song ->
            val desc = MediaDescriptionCompat.Builder()
                .setMediaUri(song.mediaUri)
                .setTitle(song.description.title)
                .setSubtitle(song.description.subtitle)
                .setMediaId(song.description.mediaId)
                .setIconUri(song.description.iconUri)
                .build()
            MediaBrowserCompat.MediaItem(desc, FLAG_PLAYABLE)
        }.toMutableList()
    }

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















