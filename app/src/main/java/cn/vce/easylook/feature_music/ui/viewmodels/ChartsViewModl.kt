package cn.vce.easylook.feature_music.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.vce.easylook.feature_music.data.Repository
import com.cyl.musicapi.bean.TopListBean
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChartsViewModl
    @Inject constructor(): ViewModel() {

    init {
        loadNeteaseTopList()
    }

    private val _neteaseTopList: MutableLiveData<List<TopListBean>> = MutableLiveData()
    val neteaseTopList = _neteaseTopList

    fun loadNeteaseTopList() {
        viewModelScope.launch {
            Repository.loadNeteaseTopList()?.let {
                _neteaseTopList.postValue(it)
            }
        }
    }


}