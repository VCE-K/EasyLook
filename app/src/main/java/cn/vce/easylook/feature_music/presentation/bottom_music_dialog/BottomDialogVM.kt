package cn.vce.easylook.feature_music.presentation.bottom_music_dialog

import cn.vce.easylook.base.BaseEvent
import cn.vce.easylook.base.BaseViewModel
import cn.vce.easylook.feature_music.repository.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class BottomDialogVM @Inject constructor(
    private val repository: MusicRepository
): BaseViewModel() {

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    override fun onEvent(event: BaseEvent) {
        when(event){
            is BottomDialogEvent.SaveMusicToPlaylist -> {
                launch {
                    try {
                        event.m.apply {
                            repository.insertMusicInfo(this)
                        }
                        _eventFlow.emit(
                            UiEvent.SaveMusic
                        )
                    }catch (e: java.lang.Exception){
                        _eventFlow.emit(
                            UiEvent.ShwoSnackbar(
                                e.message ?: "Couldn't save music"
                            )
                        )
                    }
                }
            }
            is BottomDialogEvent.RestoreMusicToPlaylist -> {
                launch {
                    event.m.apply {
                        repository.deleteMusicInfo(this)
                    }
                }
            }
        }
    }

    sealed class UiEvent{
        data class ShwoSnackbar(val message: String): UiEvent()

        object SaveMusic: UiEvent()
    }
}