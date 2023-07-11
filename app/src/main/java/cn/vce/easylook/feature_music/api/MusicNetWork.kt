package cn.vce.easylook.feature_music.api

import cn.vce.easylook.feature_music.models.*
import cn.vce.easylook.utils.LogE
import cn.vce.easylook.utils.convertList
import cn.vce.easylook.utils.convertObject
import com.cyl.musicapi.BaseApiImpl
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


object MusicNetWork {

    //private val rankListService = ServiceCreator.create<RankListService>()

    //拿到排行榜
    //suspend fun getTopLists() = rankListService.getTopLists().await()

    private const val type: String = "NETEASE"
    /**
     * 加载Netease网易云歌单
     */
    suspend fun loadNeteaseTopList(): List<TopListBean> {
        return suspendCoroutine { continuation ->
            BaseApiImpl.getAllNeteaseTopList(success = { result ->
                val data = convertList(result, TopListBean::class.java)
                if (data != null) {
                    data?.forEach{
                        it.list?.forEach { musicInfo ->
                            musicInfo.pid = it.id?: ""
                        }
                    }
                    continuation.resume(data)
                }else{
                    continuation.resumeWithException(RuntimeException("loadNeteaseTopList data is null"))
                }
            }, fail = {
                continuation.resumeWithException(RuntimeException("loadNeteaseTopList fail is $it"))
            })
        }
    }

    suspend fun getPlaylistDetail(pid: String): ArtistSongs {
        return suspendCoroutine { continuation ->
            BaseApiImpl.getPlaylistDetail(type.toLowerCase(), pid, { result ->
                LogE("getPlaylistDetail::$result")
                val data = convertObject(result, ArtistSongs::class.java)
                if (data != null) {
                    continuation.resume(data)
                }else{
                    continuation.resumeWithException(RuntimeException("getPlaylistDetail data is null"))
                }
            }, {
                continuation.resumeWithException(RuntimeException("getPlaylistDetail fail is $it"))
            })
        }
    }



    suspend fun searchMusic(key: String, limit: Int, page: Int): List<MusicInfo> {
        return suspendCoroutine { continuation ->
            //"NETEASE"
            BaseApiImpl.searchSongSingle(key, type, limit, page, success = {
                if (it.status) {
                    LogE("search type", type.toLowerCase())
                    if (it.data.songs?.isNotEmpty() == true) {
                        val data = convertList(it.data.songs, MusicInfo::class.java)
                        if (data != null) {
                            data.forEach {
                                it.pid = PlaylistType.WEBSEARCH.toString()
                            }
                            continuation.resume(data)
                        }else{
                            continuation.resumeWithException(RuntimeException("getPlaylistDetail data is null"))
                        }
                    }else{
                        continuation.resumeWithException(RuntimeException("searchMusic is $it"))
                    }
                } else {
                    continuation.resumeWithException(RuntimeException("searchMusic msg is ${it.msg}"))
                }
            })
        }
    }
    /**
     * 获取歌曲url信息
     * @param br 音乐品质
     *
     */
    suspend fun getMusicUrl(vendor: String = type.toLowerCase(), mid: String, br: Int = 128000): String {
        LogE("getMusicUrl $vendor $mid $br")
        return suspendCoroutine { continuation ->
            BaseApiImpl.getSongUrl(vendor, mid, br, {
                if (it.status) {
                    val url = it.data.url
                    continuation.resume(url)
                } else {
                    continuation.resumeWithException(RuntimeException("getMusicUrl url is null"))
                }
            }, {
                continuation.resumeWithException(RuntimeException("getMusicUrl fail"))
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