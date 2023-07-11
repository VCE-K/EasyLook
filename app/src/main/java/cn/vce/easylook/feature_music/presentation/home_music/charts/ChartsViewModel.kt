package cn.vce.easylook.feature_music.presentation.home_music.charts

import android.database.Observable
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import cn.vce.easylook.base.BaseViewModel
import cn.vce.easylook.base.BaseEvent
import cn.vce.easylook.base.ObservableViewModel
import cn.vce.easylook.base.VmError
import cn.vce.easylook.feature_music.api.MusicNetWork
import cn.vce.easylook.feature_music.models.ArtistSongs
import cn.vce.easylook.feature_music.models.MusicInfo
import cn.vce.easylook.feature_music.models.PlaylistInfo
import cn.vce.easylook.feature_music.models.TopListBean
import cn.vce.easylook.feature_music.other.Resource
import cn.vce.easylook.feature_music.presentation.home_music.music_local.MusicLocalEvent
import cn.vce.easylook.feature_music.repository.MusicRepository
import cn.vce.easylook.utils.LogE
import cn.vce.easylook.utils.convertList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ChartsViewModel
    @Inject constructor(
        private val repository: MusicRepository
    ): BaseViewModel() {

    private val pid = MutableLiveData<String>()

    val neteaseTopList = MutableLiveData<List<TopListBean>>()

    val songs = MutableLiveData<List<MusicInfo>?>()

    val etSearchText = MutableLiveData<String?>()


    val filterSongs = MutableLiveData<List<MusicInfo>?>()

    val parentPosition = MutableLiveData<Int>()

    fun loadNeteaseTopList() = launch {
        neteaseTopList.value = repository.loadNeteaseTopList()
    }

    override fun onEvent(event: BaseEvent) {
        when(event){
            is ChartsEvent.SwitchCharts -> {
                if (event.pid != pid.value){
                    pid.value = event.pid
                    parentPosition.value = event.position

                    getPlaylistJob?.cancel()
                    getPlaylistJob = ChartsRepo.getPlaylistDetail(event.pid)
                        .catch { LogE("catch... when searching", t = it, tag = TAG) }
                        .onEach {
                            val data = convertList(it?.songs, MusicInfo::class.java)?.map{
                                it.pid = ""
                                it
                            }
                            songs.value = data
                            onEvent(ChartsEvent.TextChange)
                        }
                        .flowOn(Dispatchers.Main)
                        .launchIn(viewModelScope)
                }
            }
            is ChartsEvent.TextChange -> {
                val input = etSearchText.value
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