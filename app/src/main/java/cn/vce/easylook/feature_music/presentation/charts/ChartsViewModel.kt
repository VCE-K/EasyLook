package cn.vce.easylook.feature_music.presentation.charts

import androidx.lifecycle.MutableLiveData
import cn.vce.easylook.base.BaseViewModel
import cn.vce.easylook.feature_music.data.Repository
import cn.vce.easylook.feature_music.other.Resource
import com.cyl.musicapi.bean.TopListBean
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ChartsViewModel
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