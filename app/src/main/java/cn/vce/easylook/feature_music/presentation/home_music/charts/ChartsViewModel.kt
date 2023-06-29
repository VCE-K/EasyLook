package cn.vce.easylook.feature_music.presentation.home_music.charts

import androidx.lifecycle.MutableLiveData
import cn.vce.easylook.base.BaseViewModel
import cn.vce.easylook.base.BaseEvent
import cn.vce.easylook.base.VmError
import cn.vce.easylook.feature_music.models.TopListBean
import cn.vce.easylook.feature_music.other.Resource
import cn.vce.easylook.feature_music.repository.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ChartsViewModel
    @Inject constructor(
        private val repository: MusicRepository
    ): BaseViewModel() {

    private val _neteaseTopList = MutableLiveData<Resource<List<TopListBean>>>()
    val neteaseTopList = _neteaseTopList

    init {
        loadNeteaseTopList()
    }

    fun loadNeteaseTopList() = launch {
            _neteaseTopList.value = Resource.loading(null)
            val data = withContext(Dispatchers.IO){
                repository.loadNeteaseTopList()
            }
            _neteaseTopList.value = Resource.success(data)
        }



    override fun onEvent(event: BaseEvent) {

    }

}