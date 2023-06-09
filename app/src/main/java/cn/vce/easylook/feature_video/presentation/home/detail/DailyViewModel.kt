/*
 * Copyright (c) 2020. vipyinzhiwei <vipyinzhiwei@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.vce.easylook.feature_video.presentation.home.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.vce.easylook.base.BaseEvent
import cn.vce.easylook.base.BaseViewModel
import cn.vce.easylook.feature_video.models.Daily
import cn.vce.easylook.feature_video.repository.MainPageRepository
import cn.vce.easylook.utils.LogE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

class DailyViewModel: BaseViewModel() {

    fun loadData(
        callback: (List<Daily.Item>) -> Unit
    ) = DailyRepo.getDaily()
        .catch { LogE("catch... when searching", t = it, tag = TAG) }
        .onEach { callback.invoke(it.itemList) }
        .flowOn(Dispatchers.Main)
        .launchIn(viewModelScope)

    override fun onEvent(event: BaseEvent) {
        when(event) {
            is DailyEvent.Search -> {
                DailyRepo.getDaily()
                    .catch {
                        LogE("catch... when searching", t = it, tag = TAG)

                    }
                    .onEach {
                        LogE(it.toString(), tag = TAG)
                        val dataList = mutableListOf<Daily.Item>()
                        it.itemList.forEach {
                            when (it.type) {
                                "followCard" -> {
                                    when (it.data.dataType) {
                                        "FollowCard" -> {
                                            dataList.add(it)
                                        }
                                        else -> {}
                                    }
                                }
                            }
                        }
                        event.callback.invoke(dataList)
                    }
                    .flowOn(Dispatchers.Main)
                    .launchIn(viewModelScope)
            }
            else -> {}
        }
    }

}
