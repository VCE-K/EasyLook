package cn.vce.easylook.feature_music.db

import android.content.Context
import androidx.core.content.edit
import cn.vce.easylook.EasyApp

object MusicConfigManager {

    private fun sharedPreferences() =
        EasyApp.context.getSharedPreferences("Easy", Context.MODE_PRIVATE)

    /*** 播放模式相关开始***/

    const val REPEAT_MODE_ALL = 0
    const val REPEAT_MODE_ONE = 1
    const val PLAY_MODE_RANDOM = 2

    //fun isPlayMode() = sharedPreferences().contains("playMode")

    fun getPlayMode() = sharedPreferences().getInt("playMode", 0)

    fun savePlayMode(playMode: Int){
        sharedPreferences().edit {
            putInt("playMode", playMode)
        }
    }

    /*** 播放模式相关结束***/


    /*权限申请相关开始*/
    fun isAllGranted() = sharedPreferences().contains("allGranted")
    fun getAllGranted() = sharedPreferences().getBoolean("allGranted", false)

    fun saveAllGranted(allGranted: Boolean){
        sharedPreferences().edit {
            putBoolean("allGranted", allGranted)
        }
    }
    /*权限申请相关结束*/
}