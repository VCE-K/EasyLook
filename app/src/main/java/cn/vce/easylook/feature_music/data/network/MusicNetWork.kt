package cn.vce.easylook.feature_music.data.network

import cn.vce.easylook.feature_music.domain.entities.Song
import cn.vce.easylook.utils.LogE
import com.cyl.musicapi.BaseApiImpl
import com.cyl.musicapi.bean.ArtistSongs
import com.cyl.musicapi.bean.TopListBean
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.suspendCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
object MusicNetWork {

    //private val rankListService = ServiceCreator.create<RankListService>()

    //拿到排行榜
    //suspend fun getTopLists() = rankListService.getTopLists().await()


    /**
     * 加载Netease网易云歌单
     */
    suspend fun loadNeteaseTopList(): List<TopListBean> {
        return suspendCoroutine { continuation ->
            BaseApiImpl.getAllNeteaseTopList(success = { result ->
                continuation.resume(result)
            }, fail = {
                continuation.resumeWithException(RuntimeException("fail is $it"))
            })
        }
    }

    suspend fun getPlaylistDetail(idx: String): ArtistSongs {
        return suspendCoroutine { continuation ->
            BaseApiImpl.getPlaylistDetail("netease", idx, { result ->
                continuation.resume(result)
            }, {
                continuation.resumeWithException(RuntimeException("fail is $it"))
            })
        }
    }


    suspend fun searchMusic(key: String, limit: Int, page: Int): MutableList<Song> {
        return suspendCoroutine { continuation ->
            val type: String = "NETEASE"
            BaseApiImpl.searchSongSingle(key, type, limit, page, success = {
                val songs = mutableListOf<Song>()
                if (it.status) {
                    try {
                        LogE("search type", type.toLowerCase())
                        /*it.data.songs?.forEach { music ->
                            music.vendor = type.toLowerCase()
                            if (music.songId != null && music.name != null) {
                                musicList.add(Song(music.id!!, music.name!!))
                            }
                        }*/

                        it.data.songs?.forEach { music ->
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
                                    artistNames = artistNames
                                )
                                songs.add(song)
                            }
                        }
                    } catch (e: Throwable) {
                        LogE("search", e.message)
                    }
                    LogE("search", "结果：" + songs.size)
                    continuation.resume(songs)
                } else {
                    LogE("search", it.msg)
                    continuation.resumeWithException(RuntimeException("fail is $it"))
                }
            })
        }
    }
    /**
     * 获取歌曲url信息
     * @param br 音乐品质
     *
     */
    suspend fun getMusicUrl(vendor: String = "netease", mid: String, br: Int = 128000): String {
        LogE("getMusicUrl $vendor $mid $br")
        return suspendCoroutine { continuation ->
            BaseApiImpl.getSongUrl(vendor, mid, br, {
                if (it.status) {
                    val url = it.data.url
                    continuation.resume(url)
                } else {
                    continuation.resumeWithException(RuntimeException("url is null"))
                }
            }, {
                continuation.resumeWithException(RuntimeException("getMusicUrl response fail"))
            })
        }
    }

    private suspend fun <T> Call<T>.await(): T{
        return suspendCoroutine { continuation ->
            enqueue(object: Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if (body != null) continuation.resume(body)
                    else continuation.resumeWithException(RuntimeException("response body is null"))
                }
                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }

            })
        }
    }
}