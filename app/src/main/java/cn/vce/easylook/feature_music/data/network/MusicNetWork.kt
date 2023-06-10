package cn.vce.easylook.feature_music.data.network

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