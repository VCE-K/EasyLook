package cn.vce.easylook.feature_music.ui.viewmodels

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
import androidx.lifecycle.*
import cn.vce.easylook.feature_music.data.Repository
import cn.vce.easylook.feature_music.data.entities.Song
import cn.vce.easylook.feature_music.exoplayer.*
import cn.vce.easylook.feature_music.other.Constants.MEDIA_ROOT_ID
import cn.vce.easylook.feature_music.other.Resource
import cn.vce.easylook.utils.LogE
import com.cyl.musicapi.bean.ArtistItem
import com.cyl.musicapi.playlist.MusicInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection,
    private val firebaseMusicSource: FirebaseMusicSource,
    private val state: SavedStateHandle
) : ViewModel() {
    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)


    private val _mediaItems = MutableLiveData<Resource<List<Song>>>()
    val mediaItems: LiveData<Resource<List<Song>>> = _mediaItems

    val isConnected = musicServiceConnection.isConnected
    val networkError = musicServiceConnection.networkError
    val curPlayingSong = musicServiceConnection.curPlayingSong
    val playbackState = musicServiceConnection.playbackState

    init {
        initializing()

        state.get<String>("id")?.let {
            getPlaylistDetail(it)
        }
    }

    private fun initializing() {
        _mediaItems.postValue(Resource.loading(null))
        musicServiceConnection.subscribe(MEDIA_ROOT_ID, object : MediaBrowserCompat.SubscriptionCallback() {
            override fun onChildrenLoaded(
                parentId: String,
                children: MutableList<MediaBrowserCompat.MediaItem>
            ) {
                super.onChildrenLoaded(parentId, children)
                val items = children.map {
                    Song(
                        it.mediaId!!,
                        it.description.title.toString(),
                        it.description.subtitle.toString(),
                        it.description.mediaUri.toString(),
                        it.description.iconUri.toString()
                    )
                }
                _mediaItems.postValue(Resource.success(items))
            }
        })
    }

    private fun getPlaylistDetail(idx: String) {
        viewModelScope.launch {
            Repository.getPlaylistDetail(idx)?.let {
                val detail: ArtistItem = it.detail
                val musicList: List<MusicInfo> = it.songs
                val songs =  mutableListOf<Song>()
                musicList.forEach { music ->
                    music.id?.let {
                        val coverUrl = music.album?.cover
                        val song = Song(music.id ?: "", music.name ?: "", "", "", coverUrl ?: "")
                        songs.add(song)
                    }
                }
                firebaseMusicSource.songs = firebaseMusicSource.transSongs(songs)
                initializing()
            }
        }
    }


    fun skipToNextSong() {
        musicServiceConnection.transportControls.skipToNext()
    }

    fun skipToPreviousSong() {
        musicServiceConnection.transportControls.skipToPrevious()
    }

    fun seekTo(pos: Long) {
        musicServiceConnection.transportControls.seekTo(pos)
    }

    fun playOrToggleSong(mediaItem: Song, toggle: Boolean = false) {
        mediaItem.mediaId.let {
            viewModelScope.launch {
                if(mediaItem.songUrl.isEmpty()) {
                    val url = Repository.getMusicUrl(it)
                    url?.let {
                        replaceSongUrl(mediaItem, it)
                    }
                }
                val isPrepared = playbackState.value?.isPrepared ?: false
                if(isPrepared && mediaItem.mediaId ==
                    curPlayingSong.value?.getString(METADATA_KEY_MEDIA_ID)) {
                    playbackState.value?.let { playbackState ->
                        when {
                            playbackState.isPlaying -> if(toggle) musicServiceConnection.transportControls.pause()
                            playbackState.isPlayEnabled -> musicServiceConnection.transportControls.play()
                            else -> Unit
                        }
                    }
                } else {
                    musicServiceConnection.transportControls.playFromMediaId(mediaItem.mediaId, null)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        serviceScope.cancel()
        musicServiceConnection.unsubscribe(MEDIA_ROOT_ID, object : MediaBrowserCompat.SubscriptionCallback() {})
    }


    private fun replaceSongUrl(mediaItem: Song, url: String){
        mediaItem.songUrl = url
        var itemSong = firebaseMusicSource.songs.find { it ->
            mediaItem.mediaId == it.description.mediaId
        }
        var mutableSongList = mutableListOf<MediaMetadataCompat>().apply {
            addAll(firebaseMusicSource.songs)
        }
        val playIndex = firebaseMusicSource.songs.indexOf(itemSong)
        mutableSongList[playIndex] = firebaseMusicSource.transSong(mediaItem)
        firebaseMusicSource.songs = mutableSongList
    }
}

















