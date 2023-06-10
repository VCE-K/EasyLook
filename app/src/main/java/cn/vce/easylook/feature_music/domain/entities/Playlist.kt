package cn.vce.easylook.feature_music.domain.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity
data class Playlist(
    @PrimaryKey
    val pid: String,
    val name: String,
    val cover: String? = null,
    val desc: String? = null
): Serializable

