package cn.vce.easylook.feature_music.data.entities

data class Song(
    val mediaId: String = "",
    val title: String = "",
    val subtitle: String = "",
    var songUrl: String = "",
    val imageUrl: String = ""
)