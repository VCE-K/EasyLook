package cn.vce.easylook.feature_music.data

import androidx.lifecycle.liveData
import cn.vce.easylook.feature_music.data.network.MusicNetWork
import cn.vce.easylook.feature_music.domain.entities.PlayListAndSongs
import cn.vce.easylook.feature_music.domain.entities.Playlist
import cn.vce.easylook.feature_music.domain.entities.Song
import com.cyl.musicapi.bean.ArtistItem
import com.cyl.musicapi.bean.ArtistSongs
import com.cyl.musicapi.bean.TopListBean
import com.cyl.musicapi.playlist.MusicInfo
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

object Repository {

    /*fun getTopLists() = fire(Dispatchers.IO) {
        val topListsResponse = MusicNetWork.getTopLists()
        if( topListsResponse.response.code == 0) {
            Result.success(topListsResponse)
        }else {
            Result.failure(RuntimeException("response code is ${topListsResponse.response.code}"))
        }
    }
*/
    /**
     * 加载Netease网易云歌单
     */
    suspend fun loadNeteaseTopList() = MusicNetWork.loadNeteaseTopList()


    /**
     * 加载Netease网易云歌单详情
     */
    fun getPlaylistDetail(id: String) = fire(Dispatchers.IO){
        val  artistSongs: ArtistSongs = MusicNetWork.getPlaylistDetail(id)
        var playList: Playlist? = null
        val songs = mutableListOf<Song>()
        artistSongs?.let {
            val detail: ArtistItem = it.detail
            val musicList: List<MusicInfo> = it.songs
            detail?.apply {
                playList = Playlist(id, name, cover, desc)
            }
            musicList.forEach { music ->
                music.id?.let {
                    var artistIds = ""
                    var artistNames = ""
                    music.artists?.let {
                        artistIds = it[0].id
                        artistNames = it[0].name
                        for (j in 1 until it.size - 1) {
                            artistIds += ",${it[j].id}"
                            artistNames += ",${it[j].name}"
                        }
                    }
                    val coverUrl = music.album?.cover
                    val subtitle = music.album?.name
                    val song = Song(
                        mediaId = music.id ?: "",
                        title = music.name ?: "",
                        subtitle = subtitle ?: "",
                        songUrl = "",
                        imageUrl = coverUrl ?: "",
                        artistNames = artistNames,
                        detail.id
                    )
                    songs.add(song)
                }
            }
        }

        if(songs.size > 0 && playList != null) {
            var playListAndSongs: PlayListAndSongs = PlayListAndSongs(playList!!, songs)
            Result.success(playListAndSongs)
        }else{
            Result.failure(RuntimeException("playListAndSongs is $artistSongs"))
        }
    }


    /**
     * 获取歌曲url信息
     * @param br 音乐品质
     */
    suspend fun getMusicUrl(mid: String) = MusicNetWork.getMusicUrl(mid =  mid)



    private fun <T> fire(context: CoroutineDispatcher, block: suspend() -> Result<T>) =
        liveData(Dispatchers.IO) {
            val result = try {
                block()
            }catch (e: Exception){
                Result.failure<T>(e)
            }
            emit(result)
        }
}
