package cn.vce.easylook.feature_music.presentation.home_music.music_local

import android.annotation.SuppressLint
import android.database.Cursor
import android.provider.BaseColumns
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import cn.vce.easylook.EasyApp
import cn.vce.easylook.base.BaseEvent
import cn.vce.easylook.base.BaseViewModel
import cn.vce.easylook.feature_music.models.*
import cn.vce.easylook.feature_music.repository.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MusicLocalVM @Inject constructor(
    private val repository: MusicRepository
): BaseViewModel() {


    private val SELECTION = MediaStore.Audio.AudioColumns.SIZE + " >= ? AND " + MediaStore.Audio.AudioColumns.DURATION + " >= ?"

    private val playlistWithMusicInfos =  MutableLiveData<List<PlaylistWithMusicInfo>?>()

    val playlistInfos = playlistWithMusicInfos.switchMap {
        val data = it?.map{ item ->
            item.playlist
        }
        liveData { emit(data) }
    }

    val songs = MutableLiveData<List<MusicInfo>?>()

    val filterSongs = MutableLiveData<List<MusicInfo>?>()

    val etSearchText = MutableLiveData<String>()

    private val pid = MutableLiveData<String>()

    val parentPosition = MutableLiveData<Int>()


    init {
        onEvent(MusicLocalEvent.FetchData)
    }
    @SuppressLint("Range")
    override fun onEvent(event: BaseEvent) {
        when(event){
            is MusicLocalEvent.FetchData -> {
                getPlaylistJob?.cancel()
                getPlaylistJob = repository.getAllPlaylistWithMusicInfo()
                    .onEach { data ->
                        playlistWithMusicInfos.value  = data
                    }.launchIn(viewModelScope)
            }
            is MusicLocalEvent.SwitchPlaylist -> {
                if (event.pid != pid.value){
                    launch {
                        if (event.pid == PlaylistType.LOCAL.toString()) {
                            onEvent(MusicLocalEvent.InitLocalMusic)
                        } else {
                            songs.value = emptyList()
                            parentPosition.value = event.position
                            pid.value = event.pid
                            val data = repository.getMusicInfos(event.pid)
                            songs.value = data
                        }
                        //一定要放launch最后面，不然有走getMusicInfos之前先走下面这句了
                        onEvent(MusicLocalEvent.TextChange)
                    }
                }
            }
            is MusicLocalEvent.TextChange -> {
                val input = etSearchText.value?:""
                val data = songs.value?.filter { v ->
                    when {
                        input.isBlank() -> true
                        v is MusicInfo -> v.name?.contains(input, true) ?: false
                        else -> true
                    }
                }
                filterSongs.value = data
            }
            is MusicLocalEvent.InitLocalMusic -> {
                val filterTime: Long = 1 * 1000
                val filterSize: Long = 1 * 1024
                songs.value = emptyList()
                val data: Cursor = EasyApp.context.contentResolver.query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    arrayOf(
                        BaseColumns._ID,
                        MediaStore.Audio.AudioColumns.IS_MUSIC,
                        MediaStore.Audio.AudioColumns.TITLE,
                        MediaStore.Audio.AudioColumns.ARTIST,
                        MediaStore.Audio.AudioColumns.ALBUM,
                        MediaStore.Audio.AudioColumns.ALBUM_ID,
                        MediaStore.Audio.AudioColumns.DATA,
                        MediaStore.Audio.AudioColumns.DISPLAY_NAME,
                        MediaStore.Audio.AudioColumns.SIZE,
                        MediaStore.Audio.AudioColumns.DURATION
                    ),
                    SELECTION,
                    arrayOf(filterSize.toString(), filterTime.toString()),
                    MediaStore.Audio.Media.DEFAULT_SORT_ORDER
                ) ?: return

                val musicList = mutableListOf<MusicInfo>()
                while (data.moveToNext()) {
                    val duration: Long =
                        data.getLong(data.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    if (duration < filterTime) {
                        continue
                    }
                    val fileSize: Long =
                        data.getLong(data.getColumnIndex(MediaStore.Audio.Media.SIZE))
                    if (fileSize < filterSize) {
                        continue
                    }
                    val id: Long = data.getLong(data.getColumnIndex(BaseColumns._ID))
                    val title: String =
                        data.getString(data.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE))
                    val artist: String =
                        data.getString(data.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST))
                    val album: String =
                        data.getString(data.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM))
                    val albumId: Long =
                        data.getLong(data.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID))
                    val path: String = /*"file://" +*/
                        data.getString(data.getColumnIndex(MediaStore.Audio.AudioColumns.DATA))
                    val fileName: String =
                        data.getString(data.getColumnIndex(MediaStore.Audio.AudioColumns.DISPLAY_NAME))
                    val music = MusicInfo(id = id.toString(), name = title,artists= listOf(
                        ArtistsItem(id = "", name = artist)
                    ) , album = Album(id = albumId.toString(), name = album), quality = null,
                        songUrl = path, pid = PlaylistType.LOCAL.toString(), source = PlaylistType.LOCAL.toString())
                    musicList.add(music)
                }
                // 释放资源
                data.close()
                songs.value = musicList
            }
        }
    }

}
