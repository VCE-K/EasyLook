package cn.vce.easylook.feature_video.other

import android.content.Context
import androidx.core.content.edit
import cn.vce.easylook.EasyApp

object VideoConfigManager {

    private fun sharedPreferences() =
        EasyApp.context.getSharedPreferences("Easy", Context.MODE_PRIVATE)

    fun isNeedMuteSaved() = sharedPreferences().contains("isNeedMuteSaved")

    fun getSavedIsNeedMute() = sharedPreferences().getBoolean("isNeedMuteSaved", false)

    fun saveIsNeedMuteSaved(isNeedMuteSaved: Boolean){
        sharedPreferences().edit {
            putBoolean("isNeedMuteSaved", isNeedMuteSaved)
        }
    }

}