package cn.vce.easylook.feature_music.repository

import android.content.ContentValues
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.RequiresApi
import cn.vce.easylook.EasyApp
import cn.vce.easylook.R
import cn.vce.easylook.base.BaseRepository
import cn.vce.easylook.feature_music.api.MusicNetWork
import cn.vce.easylook.feature_music.db.MusicConfigManager
import cn.vce.easylook.feature_music.db.MusicDatabase
import cn.vce.easylook.feature_music.models.*
import cn.vce.easylook.feature_music.models.bli.download.Audio
import cn.vce.easylook.feature_music.other.DownloadResult
import cn.vce.easylook.feature_music.other.LRUCacheLyric
import cn.vce.easylook.utils.getReadFileName
import cn.vce.easylook.utils.getString
import cn.vce.easylook.utils.title
import cn.vce.easylook.utils.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.ResponseBody
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File

class MusicRepository(
    private val db: MusicDatabase
): BaseRepository(){

    /*BLIBLI start*/
    suspend fun getAvInfo(avId: Int) = MusicNetWork.getAvInfo(avId)

    suspend fun getDownloadInfo(avId: Int, cid: Int) = MusicNetWork.getDownloadInfo(avId, cid)

    /*BLIBLI end*/

    /**
     * 获取歌曲url信息
     * @param br 音乐品质
     */
    suspend fun getMusicUrl(m: MusicInfo) {

        try {
            m.apply {
                if (songUrl?.isNotEmpty() == true){
                    return
                }
                if (cp) {
                    throw RuntimeException(getString(R.string.song_no_copyright_free))
                }
                id?.let {
                    if (source == MusicSourceType.BLIBLI.toString()){
                        val avId = id.toInt()
                        val avInfoResponse = getAvInfo(avId)
                        val cid = avInfoResponse.avData?.cid
                        cid?.let {
                            val downloadInfo = getDownloadInfo(avId, it)
                            val data = downloadInfo.data
                            data?.let {
                                val url = if (it.downloadResources == null) {
                                    //https://api.bilibili.com/x/player/playurl?avid=26305734&cid=45176667&fnval=16&otype=json&qn=16 这种居然没有audio接口！
                                    data.durl?.get(0)?.url
                                } else {
                                    val resources: List<Audio>? = it.downloadResources!!.audio
                                    resources?.get(0)?.baseUrl
                                }
                                songUrl = url
                            }
                        }
                    }else if (source == MusicSourceType.NETEASE.toString()){
                        val url = getNeteaseMusicUrl(id)
                        songUrl = url
                    }
                }
            }
        }catch (e: Throwable){
            e.printStackTrace()
            val message = if (e.message != null){
                m.name + e.message
            }else{
                getString(R.string.unknown_error)
            }
            //toast(message)
        }
    }

    suspend fun getNeteaseMusicUrl(mid: String)= MusicNetWork.getMusicUrl(mid = mid)

    suspend fun getLyricInfo(mid: String): String {
        //先去缓存进行查找
        return LRUCacheLyric.getInstance()[mid]?:MusicNetWork.getLyricInfo(mid = mid).also {
            //放入缓存
            LRUCacheLyric.getInstance().put(mid, it)
            val lrcText = it
            val array: Array<String> =
                lrcText.split("\\n".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
            val sb = StringBuffer()
            for (i in array.indices){
                var isFirst = false
                for (j in 0 until array[i].length){
                    val c = array[i][j]
                    if (!isFirst && c == ','){
                        isFirst = true
                        sb.append("]")
                    }else if (j == array[i].length-1 && c == ']'){
                        sb.append("\n")
                    }else{
                        sb.append(c)
                    }
                }
            }
            return sb.toString()
        }
    }
    //数据库操作
    fun getAllPlaylistWithMusicInfo(): Flow<List<PlaylistWithMusicInfo>> = db.musicDao.getAllPlaylistWithMusicInfo()

    suspend fun getMusicInfos(pid: String) = withIO {
        db.musicDao.getMusicInfos(pid)
    }

    suspend fun getPlaylist(pid: String): PlaylistInfo = withIO {
        db.musicDao.getPlaylist(pid)
    }
    suspend fun insertPlaylistInfo(p: PlaylistInfo) = withIO {
        db.musicDao.insertPlaylistInfo(p)
    }
    suspend fun insertMusicInfo(m: MusicInfo) = withIO {
        val musicInfoCopy = m.copy(timestamp = System.currentTimeMillis())
        if (musicInfoCopy.source != PlaylistType.LOCAL.toString()){//除了本地音乐，网易云音乐两个小时歌曲，B站一会就过期了
            musicInfoCopy.songUrl = ""
        }
        db.musicDao.insertMusicInfo(musicInfoCopy)
    }
    suspend fun deleteMusicInfo(m: MusicInfo) = withIO {
        db.musicDao.deleteMusicInfo(m)
    }

    suspend fun downloadMusic(m: MusicInfo): Flow<DownloadResult<File>> = flow {
        getMusicUrl(m)
        m.songUrl?.let {
            val lengthBody = MusicNetWork.downloadMusic(url = it)//第一次访问是拿长度而已
            val filename = if (it.lastIndexOf(".") >= 0){
                val type = it.substring(it.lastIndexOf("."))
                (m.name?:"") + type
            } else {
                m.name?:""
            }
            var fileNameOther: String
            var currentLength: Long
            getReadFileName(filename, lengthBody.contentLength()).let {
                fileNameOther = it.name?:filename
                currentLength = it.currentLength!!
            }
            //range表示下载范围
            val range = "bytes="+ currentLength + "-" + lengthBody.contentLength()
            val body = MusicNetWork.downloadMusic(range,it)
            lengthBody.close()
            downloadMusicFile(body, fileNameOther)
        }?: flow {
            emit(DownloadResult.error("${m.name}歌曲获取链接无效，下载失败"))
        }
    }


    private fun downloadMusicFile(body: ResponseBody, fileName: String) = flow {
        try {
            val contentResolver = EasyApp.context.contentResolver
            val mediaType = body.contentType()
            var currentLength = 0
            val contentLength = body.contentLength()
            val inputStream = body.byteStream()
            val bis = BufferedInputStream(inputStream)

            val values = ContentValues()
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            values.put(MediaStore.MediaColumns.MIME_TYPE, mediaType.toString())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_MUSIC)
            } else {
                values.put(MediaStore.MediaColumns.DATA,
                    "${Environment.getExternalStorageDirectory().path}/${Environment.DIRECTORY_MUSIC}/$fileName")
            }
            //MediaStore.Audio.Media.EXTERNAL_CONTENT_URI表明是音乐
            val uri = contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values)
            if (uri != null) {
                val outputStream = contentResolver.openOutputStream(uri)
                if (outputStream != null) {
                    val bos = BufferedOutputStream(outputStream)
                    val buffer = ByteArray(1024)
                    var bytes = bis.read(buffer)
                    while (bytes >= 0) {
                        bos.write(buffer, 0 , bytes)
                        bos.flush()
                        bytes = bis.read(buffer)

                        currentLength += bytes
                        emit(
                            DownloadResult.loading<File>(
                                currentLength.toLong(),
                                contentLength,
                                currentLength.toFloat() / contentLength.toFloat(),
                                fileName
                            )
                        )
                    }
                    bos.close()
                }
            }
            emit(DownloadResult.success<File>())
            bis.close()
        } catch(e: Exception) {
            e.printStackTrace()
            emit(DownloadResult.error(e.message?:"下载异常"))
        }
    }.flowOn(Dispatchers.IO)

    fun getPlayMode() = MusicConfigManager.getPlayMode()

    fun savePlayMode(playMode: Int) = MusicConfigManager.savePlayMode(playMode)

    fun isAllGranted() = MusicConfigManager.isAllGranted()
    fun getAllGranted() = MusicConfigManager.getAllGranted()
    fun saveAllGranted(allGranted: Boolean) = MusicConfigManager.saveAllGranted(allGranted)

}
