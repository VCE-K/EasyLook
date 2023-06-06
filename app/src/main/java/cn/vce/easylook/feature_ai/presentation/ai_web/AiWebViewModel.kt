package cn.vce.easylook.feature_ai.presentation.ai_web

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AiWebViewModel @Inject constructor(
    private val state: SavedStateHandle
): ViewModel(){

    private val _aiUrl = MutableLiveData<String>()
    val aiUrl = _aiUrl
    init {
        _aiUrl.value = ""
        state.get<String>("aiUrl")?.let {
            _aiUrl.value = it
        }
    }
}