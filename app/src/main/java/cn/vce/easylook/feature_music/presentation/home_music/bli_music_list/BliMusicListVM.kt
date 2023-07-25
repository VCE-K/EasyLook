package cn.vce.easylook.feature_music.presentation.home_music.bli_music_list

import androidx.databinding.BaseObservable
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import cn.vce.easylook.BR
import cn.vce.easylook.base.BaseEvent
import cn.vce.easylook.base.BaseViewModel
import cn.vce.easylook.base.ObservableViewModel
import cn.vce.easylook.feature_music.models.bli.HotSong
import cn.vce.easylook.feature_music.presentation.home_music.charts.ChartsEvent
import cn.vce.easylook.feature_music.repository.MusicRepository
import cn.vce.easylook.utils.LogE
import cn.vce.easylook.utils.getTimeParam
import cn.vce.easylook.utils.toast
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class BliMusicListVM @Inject constructor(
    private val repository: MusicRepository
): BaseViewModel() {

    val parentList = MutableLiveData(mutableListOf<Cate>().apply {
        add(Cate(28, "原创音乐"))
        add(Cate(31, "翻唱"))
        add(Cate(30, "VOCALOID·UTAU"))
        add(Cate(194, "电音"))
        add(Cate(59, "演奏"))
        add(Cate(193, "MV"))
        add(Cate(29, "音乐现场"))
        add(Cate(130, "音乐综合"))
    })


    val childList = MutableLiveData<List<HotSong>?>()

    val etSearchText = MutableLiveData<String?>()
    
    val filterSongs = MutableLiveData<List<HotSong>?>()

    val parentPosition = MutableLiveData<Int>()


    private val cateId = MutableLiveData<Int>()

    private var childPage = 1
    private val childPagesize = 30

    private val childTimeFrom = 30
    private val childTimeTo = 0


    override fun onEvent(event: BaseEvent) {
        when(event){
            is BliMusicListEvent.SwitchCharts -> {
                if (event.cataId != cateId.value){
                    cateId.value = event.cataId
                    parentPosition.value = event.position
                    launch {
                        childPage = 1
                        childList.value = emptyList()
                        childList.value = BliMusicListRepo.getTrendingPlaylist(event.cataId,
                            childPage,
                            childPagesize,
                            getTimeParam(childTimeFrom),
                            getTimeParam(childTimeTo))
                        onEvent(ChartsEvent.TextChange)
                    }
                }
            }
            is BliMusicListEvent.FetchChildData -> {
                cateId.value?.let {
                    parentPosition.value?.let {  parentPosition ->
                        onEvent(BliMusicListEvent.SwitchCharts(it, parentPosition))
                    }
                }
            }
            is BliMusicListEvent.FetchMoreChildData -> {
                launch {
                    cateId.value?.let {
                        childPage++
                        val data = BliMusicListRepo.getTrendingPlaylist(
                            it,
                            childPage,
                            childPagesize,
                            getTimeParam(childTimeFrom),
                            getTimeParam(childTimeTo)
                        )
                        toast(childPage.toString())
                        childList.value = (childList.value?.plus(data))?.toMutableList()
                        onEvent(ChartsEvent.TextChange)
                    }
                }
            }
            is BliMusicListEvent.TextChange -> {
                val input = etSearchText.value
                input?.run {
                    val data = childList.value?.filter { v ->
                        when {
                            input.isBlank() -> true
                            v is HotSong -> v.title?.contains(input, true) ?: false
                            else -> true
                        }
                    }
                    filterSongs.value = data
                }
            }
        }
    }

}


data class Cate(
       val id: Int,
       val name: String,
       var checked: Boolean = false,
       var visibility: Boolean = false
) : BaseObservable()