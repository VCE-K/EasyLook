package cn.vce.easylook.base

import androidx.lifecycle.liveData
import cn.vce.easylook.http.ApiException
import kotlinx.coroutines.*
import org.json.JSONException
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * des 基础数据层
 * @date 2020/5/18
 * @author zs
 *
 * @param coroutineScope 注入viewModel的coroutineScope用于协程管理
 * @param errorLiveData 业务出错或者爆发异常，由errorLiveData通知视图层去处理
 */
open class BaseRepository {

    /**
     * 在协程作用域中切换至IO线程
     */
    protected suspend fun <T> withIO(block: suspend () -> T): T {
        return withContext(Dispatchers.IO) {
            block.invoke()
        }
    }

    protected fun <T> fire(context: CoroutineDispatcher = Dispatchers.IO, block: suspend() -> Result<T>) =
        liveData<Result<T>>(context) {
            val result = try {
                block()
            }catch (e: Exception){
                Result.failure<T>(e)
            }
            emit(result)
        }

}