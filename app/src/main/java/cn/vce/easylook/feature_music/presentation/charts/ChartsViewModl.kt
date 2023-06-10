package cn.vce.easylook.feature_music.presentation.charts

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.vce.easylook.base.BaseViewModel
import cn.vce.easylook.feature_music.data.Repository
import cn.vce.easylook.feature_music.domain.entities.Song
import cn.vce.easylook.feature_music.other.Resource
import com.cyl.musicapi.bean.TopListBean
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ChartsViewModl
    @Inject constructor(): BaseViewModel() {

    private val _neteaseTopList = MutableLiveData<Resource<List<TopListBean>>>()
    val neteaseTopList = _neteaseTopList

    init {
        loadNeteaseTopList()
    }

    fun loadNeteaseTopList() = launch {
            _neteaseTopList.value = Resource.loading(null)
            val data = withContext(Dispatchers.IO){
                Repository.loadNeteaseTopList()
            }
            _neteaseTopList.value = Resource.success(data)
        }

}