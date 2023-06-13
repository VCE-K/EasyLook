package cn.vce.easylook.feature_music.presentation.music_search

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import cn.vce.easylook.base.BaseViewModel
import cn.vce.easylook.feature_music.data.Repository
import cn.vce.easylook.feature_music.domain.entities.Song
import cn.vce.easylook.feature_music.exoplayer.MusicSource
import cn.vce.easylook.feature_music.other.Resource
import cn.vce.easylook.feature_music.presentation.music_list.MusicListEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MusicSearchVM @Inject constructor(
    private val musicSource: MusicSource
): BaseViewModel() {

    private val limit = 15
    private var mOffset = 0
    private val _query =  MutableLiveData<String>()

    /*private val _songs = Transformations.switchMap(_query) { query ->
        Repository.searchMusic(query, limit, mOffset)
    }*/

    private val _songs = MediatorLiveData<Resource<MutableList<Song>>>().apply {
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
                            Repository.searchMusic(query, limit, mOffset)
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

    fun onEvent(event: MusicListEvent) {
        when (event) {
            is MusicListEvent.PlayList -> {
                _songs.value?.data?.toMutableList()?.apply {
                    viewModelScope.launch {
                        //正在播放的歌单是不是现在显示的歌单
                        val id = get(0).mediaId
                        val fetchFlag = (musicSource.playlistId == id)
                        if ( !fetchFlag ) { //不是同一个那就刷新数据
                            musicSource.fetchMediaData {
                                return@fetchMediaData this@apply
                            }
                            id?.let {
                                musicSource.playlistId = id
                            }
                        }
                        withContext(Dispatchers.Main) {
                            event.action(!fetchFlag)
                        }
                    }
                }
            }
        }
    }
}
