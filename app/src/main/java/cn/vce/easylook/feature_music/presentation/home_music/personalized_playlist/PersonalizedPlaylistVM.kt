package cn.vce.easylook.feature_music.presentation.home_music.personalized_playlist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cn.vce.easylook.base.BaseEvent
import cn.vce.easylook.base.BaseViewModel
import cn.vce.easylook.feature_music.models.MusicInfo
import cn.vce.easylook.feature_music.models.MusicSourceType
import cn.vce.easylook.feature_music.models.PlaylistInfo
import cn.vce.easylook.feature_music.other.Status
import cn.vce.easylook.feature_music.presentation.home_music.charts.ChartsRepo
import cn.vce.easylook.feature_music.repository.MusicRepository
import cn.vce.easylook.utils.LogE
import cn.vce.easylook.utils.convertMusicList
import cn.vce.easylook.utils.toast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.text.NumberFormat
import javax.inject.Inject

@HiltViewModel
class PersonalizedPlaylistVM
    @Inject constructor(
    private val repository: MusicRepository
): BaseViewModel() {


    val playlistInfos = MutableLiveData<List<PlaylistInfo>?>()

    val songs = MutableLiveData<List<MusicInfo>?>()

    val filterSongs = MutableLiveData<List<MusicInfo>?>()

    val etSearchText = MutableLiveData<String>()

    private val pid = MutableLiveData<String>()

    val parentPosition = MutableLiveData<Int>()

    init {
        onEvent(PersonalizedPlaylistEvent.FetchData)
    }

    override fun onEvent(event: BaseEvent) {
        when(event){
            is PersonalizedPlaylistEvent.FetchData -> {
                launch({
                    playlistInfos.value = PersonalizedPlaylistRepo.personalizedPlaylist()
                },{
                    playlistInfos.value = null
                })
            }
            is PersonalizedPlaylistEvent.SwitchPlaylist -> {
                if (event.pid != pid.value){
                    pid.value = event.pid
                    parentPosition.value = event.position
                    launch {
                        songs.value = emptyList()
                        val playlistDetail = ChartsRepo.getPlaylistDetail(event.pid)
                        val data = playlistDetail?.run {
                            convertMusicList(songs, MusicSourceType.NETEASE.toString())
                        }
                        songs.value = data
                        onEvent(PersonalizedPlaylistEvent.TextChange)
                    }
                    /*getPlaylistJob?.cancel()
                    getPlaylistJob = ChartsRepo.getPlaylistDetail(event.pid)
                        .catch { LogE("catch... when searching", t = it, tag = TAG) }
                        .onEach {
                            val data = convertList(it?.songs, MusicInfo::class.java)?.map{
                                it.pid = ""
                                it
                            }
                            songs.value = data
                            onEvent(PersonalizedPlaylistEvent.TextChange)
                        }
                        .flowOn(Dispatchers.Main)
                        .launchIn(viewModelScope)*/
                }
            }

            is PersonalizedPlaylistEvent.TextChange -> {
                val input = etSearchText.value?:""
                if (input == "11"){
                    launch {
                        val downloadResult = PersonalizedPlaylistRepo.downloadMusic()
                        downloadResult.catch { LogE("catch... when searching", t = it) }
                            .onEach {
                                when(it.status){
                                    Status.SUCCESS -> {
                                        val process = it.process?:0f
                                        if (process > 0.9){
                                            if (process >= 1){
                                                LogE("下载文件:$it")
                                            }
                                            val num = NumberFormat.getPercentInstance()
                                            num.maximumFractionDigits = 2
                                            LogE("下载进度"+
                                                    num.format(it.process))
                                        }
                                    }
                                    Status.SUCCESS -> {
                                        val path = it.data?.path
                                        val filename = it.data?.name
                                        LogE("下载文件:${filename}成功,下载路径:$path")
                                        LogE("下载文件:$it")
                                    }
                                    else -> Unit
                                }
                            }
                            .flowOn(Dispatchers.Main)
                            .launchIn(viewModelScope)
                    }
                }
                input?.run {
                    val data = songs.value?.filter { v ->
                        when {
                            input.isBlank() -> true
                            v is MusicInfo -> v.name?.contains(input, true) ?: false
                            else -> true
                        }
                    }
                    filterSongs.value = data
                }
            }
        }
    }

}