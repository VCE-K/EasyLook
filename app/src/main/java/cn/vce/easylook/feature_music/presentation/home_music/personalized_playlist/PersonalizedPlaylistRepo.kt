package cn.vce.easylook.feature_music.presentation.home_music.personalized_playlist

import cn.vce.easylook.EasyApp
import cn.vce.easylook.base.BaseRepository
import cn.vce.easylook.feature_music.api.MusicNetWork
import cn.vce.easylook.feature_music.models.PlaylistInfo
import cn.vce.easylook.feature_music.other.DownloadResult
import cn.vce.easylook.utils.LogE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream

object PersonalizedPlaylistRepo: BaseRepository() {

/*    *//**
     * 推荐歌单
     *//*
    suspend fun personalizedPlaylist(): Resource<MutableList<PlaylistInfo>> {
        val personalizedResponse = MusicNetWork.personalizedPlaylist()
        return if(200 == personalizedResponse.code){
            val list = mutableListOf<PlaylistInfo>()
            personalizedResponse.result?.forEach {
                val playlistInfo = PlaylistInfo(it.id.toString(),
                    name = it.name,
                    description = it.copywriter,
                    cover = it.picUrl,
                    playCount = it.playCount.toLong())

                list.add(playlistInfo)
            }
            Resource.success(list)
        }else{
            Resource.error("personalizedPlaylist code is ${personalizedResponse.code}", null)
        }
    }*/

    /**
     * 推荐歌单
     */
    suspend fun personalizedPlaylist() = withIO {
        val personalizedResponse = MusicNetWork.personalizedPlaylist()
        val list = mutableListOf<PlaylistInfo>()
        if(200 == personalizedResponse.code){
            personalizedResponse.result?.forEach {
                val playlistInfo = PlaylistInfo(it.id.toString(),
                    name = it.name,
                    description = it.copywriter,
                    cover = it.picUrl,
                    playCount = it.playCount.toLong())

                list.add(playlistInfo)
            }
        }
        list
    }
    /**
     * 下载歌曲
     */
    suspend fun downloadMusic() = flow {
        try {
            val body = MusicNetWork.downloadMusic()
            val mediaType = body.contentType()
            val contentLength = body.contentLength()
            val inputStream = body.byteStream()
            val file = File(EasyApp.context.getExternalFilesDir(null), "test.rar1")
            LogE("absolutePath:"+file.absolutePath)
            val outputStream = FileOutputStream(file)
            var currentLength = 0
            val bufferSize = 1024 * 8
            val buffer = ByteArray(bufferSize)
            val bufferedInputStream = BufferedInputStream(inputStream, bufferSize)
            var readLength: Int
            while (bufferedInputStream.read(buffer, 0, bufferSize)
                    .also { readLength = it } != -1
            ) {
                outputStream.write(buffer, 0, readLength)
                currentLength += readLength
                emit(
                    DownloadResult.loading(
                        currentLength.toLong(),
                        contentLength,
                        currentLength.toFloat() / contentLength.toFloat()
                    )
                )
            }
            bufferedInputStream.close()
            outputStream.close()
            inputStream.close()
            emit(DownloadResult.success(file))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(DownloadResult.error(e.message ?: "Unknown error"))
        }
    }.flowOn(Dispatchers.IO)



}