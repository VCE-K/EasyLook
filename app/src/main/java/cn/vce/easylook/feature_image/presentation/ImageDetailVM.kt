package cn.vce.easylook.feature_image.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import cn.vce.easylook.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ImageDetailVM @Inject constructor(
    state: SavedStateHandle
): BaseViewModel() {
    val image = MutableLiveData<Image>()
    init {
        state.get<Image>("image")?.let {
            image.value = it
        }
    }
}