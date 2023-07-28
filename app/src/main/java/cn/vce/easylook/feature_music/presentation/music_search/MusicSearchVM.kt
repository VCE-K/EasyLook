package cn.vce.easylook.feature_music.presentation.music_search

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import cn.vce.easylook.base.BaseViewModel
import cn.vce.easylook.base.BaseEvent
import cn.vce.easylook.feature_music.models.MusicInfo
import cn.vce.easylook.feature_music.other.Resource
import cn.vce.easylook.feature_music.repository.MusicRepository
import cn.vce.easylook.utils.LogE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MusicSearchVM @Inject constructor(
    private val repository: MusicRepository
): BaseViewModel() {

    private var page = 1
    private val pageSize = 15
    val query =  MutableLiveData<String>()

    val songs = MutableLiveData<Resource<List<MusicInfo>>>(Resource.success(null))

    fun refreshQuery(query: String) {
        this.query.value = query
        page = 0
        if (query.isNullOrEmpty()) {
            // 如果查询为空，则清空列表
            songs.value = Resource.success(null)
        } else {
            // 发起搜索请求
            launch {
                songs.postValue(Resource.loading(null))
                LogE("查询：$query")
                val result = withContext(Dispatchers.IO){
                    MusicSearchRepo.searchMusic(query, pageSize, page)
                }
                songs.postValue(Resource.success(result))
            }
        }
    }

    override fun onEvent(event: BaseEvent) {
        when (event) {
            is MusicSearchEvent.RefreshSearchEvent -> {
                query.value?.let {
                    // 发起搜索请求
                    launch {
                        LogE("查询：${query.value}")
                        val result = withContext(Dispatchers.IO){
                            MusicSearchRepo.searchMusic(it, pageSize, ++page)
                        }
                        songs.postValue(Resource.success(songs.value?.data?.plus(result)))
                    }
                }
            }
        }
    }
}
