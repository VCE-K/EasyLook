package cn.vce.easylook.feature_music.api

import cn.vce.easylook.R
import cn.vce.easylook.feature_music.models.*
import cn.vce.easylook.utils.LogE
import cn.vce.easylook.utils.convertList
import cn.vce.easylook.utils.convertObject
import cn.vce.easylook.utils.getString
import com.cyl.musicapi.BaseApiImpl
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Query
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


object MusicNetWork {


    //blibli相关
    private val bliMusicService = ServiceCreator.createBli<BliMusicService>()
    suspend fun getTrendingPlaylist(cateId: Int,
                                    page: Int,
                                    pageSize: Int,
                                    timeFrom: String,
                                    timeTo: String) = bliMusicService.getTrendingPlaylist(cateId, page, pageSize, timeFrom, timeTo).await()

    suspend fun getAvInfo(avId: Int) = bliMusicService.getAvInfo(avId).await()

    suspend fun getDownloadInfo(avId: Int, cid: Int) = bliMusicService.getDownloadInfo(avId, cid).await()

    //网易云相关

    private val rankListService = ServiceCreator.create<RankListService>()

    //获取推荐歌单
    suspend fun personalizedPlaylist() = rankListService.personalizedPlaylist().await()


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
                    continuation.resumeWithException(RuntimeException(
                        String.format(getString(R.string.data_isNull),
                            getString(R.string.playlist))))
                }
            }, fail = {
                continuation.resumeWithException(RuntimeException(getString(R.string.error_connection)))
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
                    continuation.resumeWithException(RuntimeException(
                        String.format(getString(R.string.data_isNull),
                            getString(R.string.playlist_songs))))
                }
            }, {
                continuation.resumeWithException(RuntimeException(getString(R.string.error_connection)))
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
                                it.source = type.toUpperCase()
                            }
                            continuation.resume(data)
                        }else{
                            continuation.resumeWithException(RuntimeException(
                                String.format(getString(R.string.data_isNull),
                                    getString(R.string.music_home_tab_search))))
                        }
                    }else{
                        continuation.resumeWithException(RuntimeException(it.msg))
                    }
                } else {
                    continuation.resumeWithException(RuntimeException(getString(R.string.error_connection)))
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
                    if (url != null){
                        continuation.resume(url)
                    }else{
                        continuation.resumeWithException(RuntimeException(getString(R.string.song_no_copyright_free)))
                    }
                } else {
                    continuation.resumeWithException(RuntimeException(it.msg))
                }
            }, {
                continuation.resumeWithException(RuntimeException(getString(R.string.error_connection)))
            })
        }
    }

    /*BaseApiImpl.getLyricInfo(vendor, mid) {
        if (it.status) {
            val lyricInfo = it.data.lyric
            val lyric = StringBuilder()
            lyricInfo.forEach {
                lyric.append(it)
                lyric.append("\n")
            }
            it.data.translate.forEach {
                lyric.append(it)
                lyric.append("\n")
            }
            //保存文件
            val save = FileUtils.writeText(mLyricPath, lyric.toString())
            LogUtil.e("保存网络歌词：$save")
            Observable.fromArray(lyric)
            result.onNext(lyric.toString())
            result.onComplete()
        } else {
            result.onError(Throwable(it.msg))
        }
    }*/

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