package cn.vce.easylook.feature_music.db

import androidx.room.TypeConverter
import cn.vce.easylook.feature_music.models.ArtistsItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromString(value: String): List<ArtistsItem>? {
        val listType = object : TypeToken<List<ArtistsItem>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: List<ArtistsItem>?): String {
        return Gson().toJson(list)
    }
}