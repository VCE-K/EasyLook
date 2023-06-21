package cn.vce.easylook.feature_music.presentation.music_search

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import cn.vce.easylook.base.BaseViewModel
import cn.vce.easylook.base.BaseEvent
import cn.vce.easylook.feature_music.models.MusicInfo
import cn.vce.easylook.feature_music.other.Resource
import cn.vce.easylook.feature_music.repository.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MusicSearchVM @Inject constructor(
    private val repository: MusicRepository
): BaseViewModel() {

    private val limit = 15
    private var mOffset = 0
    private val _query =  MutableLiveData<String>()

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
                value = Resource.loading(value?.data)
                launch {
                    value = try {
                        val result = withContext(Dispatchers.IO){
                            repository.searchMusic(query, limit, mOffset)
                        }
                        Resource.success(result.toMutableList() ?: mutableListOf())
                    } catch (e: Exception) {
                        Resource.error(e.message ?: "Unknown error", value?.data ?: mutableListOf())
                    }
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

        }
    }
}
