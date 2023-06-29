package cn.vce.easylook.feature_music.other

import android.content.Context
import androidx.core.content.edit
import cn.vce.easylook.EasyApp

object MusicConfigManager {

    private fun sharedPreferences() =
        EasyApp.context.getSharedPreferences("Easy", Context.MODE_PRIVATE)

    fun isNeedMuteSaved() = sharedPreferences().contains("isNeedMuteSaved")

    fun getSavedIsNeedMute() = sharedPreferences().getBoolean("isNeedMuteSaved", false)

    fun saveIsNeedMuteSaved(isNeedMuteSaved: Boolean){
        sharedPreferences().edit {
            putBoolean("isNeedMuteSaved", isNeedMuteSaved)
        }
    }
    /*** 播放模式相关开始***/

    const val REPEAT_MODE_ALL = 1
    const val REPEAT_MODE_ONE = 2
    const val PLAY_MODE_RANDOM = 3

    //fun isPlayMode() = sharedPreferences().contains("playMode")

    fun getPlayMode() = sharedPreferences().getInt("playMode", 0)

    fun savePlayMode(playMode: Int){
        sharedPreferences().edit {
            putInt("playMode", playMode)
        }
    }

    /*** 播放模式相关结束***/
}