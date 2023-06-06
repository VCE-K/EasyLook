package cn.vce.easylook.feature_music.exoplayer

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.*
import android.util.Log
import androidx.core.net.toUri
import cn.vce.easylook.feature_music.data.Repository
import cn.vce.easylook.feature_music.data.entities.Song
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import cn.vce.easylook.feature_music.data.remote.MusicDatabase
import cn.vce.easylook.feature_music.exoplayer.State.*
import cn.vce.easylook.utils.LogE
import cn.vce.easylook.utils.id
import com.google.android.exoplayer2.Player
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FirebaseMusicSource(
    private val musicDatabase: MusicDatabase
) {

    var songs = mutableListOf<MediaMetadataCompat>()
    suspend fun fetchMediaData() = withContext(Dispatchers.IO) {
        state = STATE_INITIALIZING
        val allSongs = musicDatabase.getAllSongs()
        songs = allSongs.transSongs()
        state = STATE_INITIALIZED
    }



    suspend fun getUrl(
        metadata: MediaMetadataCompat
    ){
        metadata.let {
            LogE(metadata.toSong().toString())
            metadata.toSong()?.apply {
                if (mediaId.isNotEmpty() && (songUrl == null || "null" == songUrl)) {
                    Repository.getMusicUrl(mediaId)?.let{ url ->
                        replaceSongUrl(this, url)
                    }
                }
            }
        }
    }

    private fun replaceSongUrl(mediaItem: Song, url: String){
        mediaItem.songUrl = url
        var itemSong = songs.find {
            mediaItem.mediaId == it.id
        }
        val playIndex = songs.indexOfFirst { metadata ->
            metadata.id == itemSong?.id
        }
        LogE(songs[playIndex].toSong().toString())
        songs[playIndex] = mediaItem.transSong()
        LogE(songs[playIndex].toSong().toString())
    }

    fun asMediaSource(dataSourceFactory: DefaultDataSourceFactory): ConcatenatingMediaSource {
        val concatenatingMediaSource = ConcatenatingMediaSource()
        songs.forEach { song ->
            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(song.getString(METADATA_KEY_MEDIA_URI).toUri())
            concatenatingMediaSource.addMediaSource(mediaSource)
        }
        return concatenatingMediaSource
    }

    fun asMediaItems() = songs.map { song ->
        val desc = MediaDescriptionCompat.Builder()
            .setMediaUri(song.getString(METADATA_KEY_MEDIA_URI).toUri())
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















