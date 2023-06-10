package cn.vce.easylook.feature_music.other

import android.content.SharedPreferences

object MusicConfigManager {

    lateinit var sharedPreferences: SharedPreferences


    fun isPlaceSaved() = sharedPreferences.contains("place")


}