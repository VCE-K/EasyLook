package cn.vce.easylook.feature_video.presentation.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import cn.vce.easylook.base.BaseEvent
import cn.vce.easylook.base.BaseViewModel
import cn.vce.easylook.feature_video.models.Daily
import cn.vce.easylook.feature_video.models.HomePageRecommend
import cn.vce.easylook.feature_video.presentation.VideoHomeEvent
import cn.vce.easylook.utils.LogE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class VideoHomeVM: BaseViewModel() {

    var dataList = MutableLiveData<List<HomePageRecommend.Item>?>(emptyList())

    var mCurrentPosition = -1
    private var page: Int = 0
    private fun loadData(
    ) = VideoHomeRepo.getAllRec("$page")
        .catch { LogE("catch... when searching", t = it, tag = TAG) }
        .onEach {
            /*val data = mutableListOf<HomePageRecommend.Item>()
            it.itemList.forEach {
                when (it.type) {
                    "squareCardCollection" -> { //1
                        data.add(it)
                    }
                    "textCard" -> Unit //3
                    "followCard" -> Unit //2
                    "videoSmallCard" -> Unit //4
                }
            }*/
            dataList.value = it.itemList
        }.flowOn(Dispatchers.Main)
        .launchIn(viewModelScope)


    private fun loadMoreData(
    ) = VideoHomeRepo.getAllRec("$page")
        .catch {
            LogE("catch... when searching", t = it, tag = TAG)
            page--
        }
        .onEach {
            val data = dataList.value
            if (it.itemList.isEmpty()){
                page--
            }
            val newList = mutableListOf<HomePageRecommend.Item>()
            newList.addAll(data ?: emptyList()) // 如果当前列表为空，则创建一个空列表
            newList.addAll(it.itemList) // 向新列表中添加新的数据
            dataList.value = newList
        }
        .flowOn(Dispatchers.Main)
        .launchIn(viewModelScope)

    override fun onEvent(event: BaseEvent) {
        when(event) {
            is VideoHomeEvent.LoadData -> {
                page = 0
                loadData()
            }
            is VideoHomeEvent.LoadMoreData -> {
                page++
                loadMoreData()
            }
            else -> {}
        }
    }

}