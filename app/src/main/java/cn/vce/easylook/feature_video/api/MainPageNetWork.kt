package cn.vce.easylook.feature_video.api

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


object MainPageNetWork {

    private const val udid = "a53873ffaa4430bbb41ea178c1187e97c4b3c4a"

    private val mainPageService = ServiceCreator.create<MainPageService>()
    suspend fun getAllRec(page: String) = mainPageService.getAllRec(page, udid).await()
    suspend fun getDaily() = mainPageService.getDaily().await()


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