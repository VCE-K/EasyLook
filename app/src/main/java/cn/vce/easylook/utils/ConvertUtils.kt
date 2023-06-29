package cn.vce.easylook.utils

import cn.vce.easylook.feature_music.models.ArtistsItem


/**
 * Created by D22434 on 2018/1/17.
 */
object ConvertUtils {
    /**
     * 判断字符串是否为null或全为空白字符
     *
     * @param s 待校验字符串
     * @return `true`: null或全空白字符<br></br> `false`: 不为null且不全空白字符
     */
    private fun isSpace(s: String?): Boolean {
        if (s == null) return true
        var i = 0
        val len = s.length
        while (i < len) {
            if (!Character.isWhitespace(s[i])) {
                return false
            }
            ++i
        }
        return true
    }

    /**
     * 歌手名格式化
     *
     * @param artist
     * @return
     */
    fun getArtist(artists: List<ArtistsItem>?): String {
        var artistIds = ""
        var artistNames: String? = null
        artists?.let {
            if (it.isNotEmpty()){
                artistIds = it[0].id
                artistNames = it[0].name
                for (j in 1 until it.size - 1) {
                    artistIds += ",${it[j].id}"
                    artistNames += ",${it[j].name}"
                }
            }
        }
        return artistNames?: ""
    }

    /**
     * 歌手专辑格式化
     *
     * @param artist
     * @param album
     * @return
     */
    fun getArtistAndAlbum(artists: List<ArtistsItem>?, album: String?): String {
        var artistIds = ""
        var artistNames = ""
        artists?.let {
            if (it.isNotEmpty()){
                artistIds = it[0].id
                artistNames = it[0].name
                for (j in 1 until it.size - 1) {
                    artistIds += ",${it[j].id}"
                    artistNames += ",${it[j].name}"
                }
            }
        }
        return "$artistNames - $album"
    }
}