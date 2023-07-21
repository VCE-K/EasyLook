package cn.vce.easylook.feature_music.exoplayer

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import cn.vce.easylook.feature_music.exoplayer.State.*
import cn.vce.easylook.feature_music.models.MusicInfo
import cn.vce.easylook.feature_music.models.MusicSourceType
import cn.vce.easylook.feature_music.models.bli.download.Audio
import cn.vce.easylook.feature_music.other.MusicConfigManager
import cn.vce.easylook.feature_music.repository.MusicRepository
import cn.vce.easylook.utils.mediaUri
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import kotlin.random.Random

class MusicSource(
    private val repository: MusicRepository
) {

    var musicInfos = mutableListOf<MusicInfo>()
    private val mShufflePlaylistSequence
        get() = musicInfos.transSongs().apply { shuffle() }

    var songs:  MutableList<MediaMetadataCompat> = mutableListOf()
        get() {
            return musicInfos.transSongs() /*if (MusicConfigManager.getPlayMode() == PlaybackStateCompat.REPEAT_MODE_ALL){
                mShufflePlaylistSequence
            }else{
                musicInfos.transSongs()
            }*/
        }

    //当前历史记录集合
    private val playedSongs = mutableListOf<MediaMetadataCompat>()
    fun fetchMediaData(
        list: MutableList<MusicInfo>
    ) {
        if (state == STATE_CREATED) {
            state = STATE_INITIALIZING
            musicInfos = list
            state = STATE_INITIALIZED
        } else if (state == STATE_INITIALIZED) {
            musicInfos = list
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
        songIndex: Int,
        fetchNext: Boolean = true
    ){
        val nextSong = songs[songIndex]
        nextSong.let {
            musicInfos[songIndex].apply {
                id?.let {
                    /*if (songUrl?.isBlank() == true || songUrl == null) {
                        repository.getMusicUrl(it)?.let{ url ->
                            this.songUrl = url
                        }
                    }*/
                    if (musicInfos[songIndex].source == MusicSourceType.BLIBLI.toString()){
                        val avId = musicInfos[songIndex].id.toInt()
                        val avInfoResponse = repository.getAvInfo(avId)
                        val cid = avInfoResponse.avData?.cid
                        cid?.let {
                            val downloadInfo = repository.getDownloadInfo(avId, it)
                            val data = downloadInfo.data
                            data?.let {
                                val url = if (it.downloadResources == null) {
                                    //https://api.bilibili.com/x/player/playurl?avid=26305734&cid=45176667&fnval=16&otype=json&qn=16 这种居然没有audio接口！
                                    data.durl?.get(0)?.url
                                } else {
                                    val resources: List<Audio>? = it.downloadResources!!.audio
                                    resources?.get(0)?.baseUrl
                                }
                                this.songUrl = url
                            }

                        }

                    }else if (musicInfos[songIndex].source == MusicSourceType.NETEASE.toString()){
                        repository.getMusicUrl(it)?.let{ url ->
                            this.songUrl = url
                        }
                    }
                }



            }
        }
        if (fetchNext){
            val previousIndex: Int = if ((songIndex - 1) < 0 ){//到达开头
                songs.size - 1
            }else {
                songIndex - 1
            }
            val nextSongIndex= if ((songIndex + 1) == songs.size ){
                0
            }else {
                songIndex + 1
            }

            if (songIndex == previousIndex && songIndex == nextSongIndex){
                return
            }else if (previousIndex == nextSongIndex){
                fetchSongUrl(nextSongIndex, false)
            }else{
                fetchSongUrl(previousIndex, false)
                fetchSongUrl(nextSongIndex, false)
            }
        }
    }


    /*********** 随机播放相关开始 ***********/
    /**
     * 随机播放具体简陋算法
     */
/*    fun List<Any>.shuffle() {
        val rand = Random
        val array = this.toMutableList()
        for (i in array.size - 1 downTo 1) {
            val j: Int = rand.nextInt(i + 1)
            // 交换 array[i] 和 array[j]
            val temp = array[i]
            array[i] = array[j]
            array[j] = temp
        }
    }*/

/*    fun getShuffleSong(): MediaMetadataCompat {
        val rand = Random
        val i: Int = rand.nextInt(songs.size)
        return songs[i]
    }*/

    fun getShuffleSong(): Int {
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
    }

    /*********** 随机播放相关结束 ***********/

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















