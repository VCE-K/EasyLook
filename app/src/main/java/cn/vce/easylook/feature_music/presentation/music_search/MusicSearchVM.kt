package cn.vce.easylook.feature_music.presentation.music_search

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
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

    private val limit = 15
    private val _query =  MutableLiveData<String>()
    val query = _query
    /*private val _songs = Transformations.switchMap(_query) { query ->
        Repository.searchMusic(query, limit, mOffset)
    }*/

    private val _songs = MediatorLiveData<Resource<MutableList<MusicInfo>>>().apply {
        value = Resource.loading(null)
        addSource(_query) { query ->
            if (query.isNullOrEmpty()) {
                // 如果查询为空，则清空列表
                value = Resource.success(mutableListOf())
            } else {
                // 发起搜索请求
                value = Resource.loading(null)
                launch {
                    LogE("查询：$query")
                    val data = try {
                        val result = withContext(Dispatchers.IO){
                            repository.searchMusic(query, limit, 0)
                        }
                        Resource.success(result.toMutableList())
                    } catch (e: Exception) {
                        Resource.error(e.message ?: "Unknown error", value?.data ?: mutableListOf())
                    }
                    postValue(data)
                }
            }
        }
    }


    val songs = _songs

    fun refreshQuery(query: String) {
        _query.value = query
    }

    override fun onEvent(event: BaseEvent) {
        when (event) {
            is MusicSearchEvent.RefreshSearchEvent -> {
                _query.value?.let {
                    // 发起搜索请求
                    launch {
                        LogE("查询：${_query.value}")
                        val result = withContext(Dispatchers.IO){
                            repository.searchMusic(it, limit, event.mOffset)
                        }
                        event.callback.invoke(result)
                    }
                }
            }
        }
    }
}
