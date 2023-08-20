package cn.vce.easylook.feature_music.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import cn.vce.easylook.MainActivity
import cn.vce.easylook.R
import cn.vce.easylook.feature_music.models.MusicInfo
import cn.vce.easylook.feature_music.other.Constants
import cn.vce.easylook.feature_music.other.Constants.DOWN_NOTIFICATION_CHANNEL_ID
import cn.vce.easylook.feature_music.other.Constants.DOWN_NOTIFICATION_ID
import cn.vce.easylook.feature_music.other.Status
import cn.vce.easylook.feature_music.repository.MusicRepository
import cn.vce.easylook.utils.LogE
import cn.vce.easylook.utils.toast
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.io.File
import java.lang.Thread.sleep
import java.text.NumberFormat
import javax.inject.Inject
import kotlin.concurrent.thread


/**
 * 启动服务自动变成前台服务
 */
@AndroidEntryPoint
class DownloadService: Service() {
    @Inject
    lateinit var repository: MusicRepository
    @Inject
    lateinit var glide: RequestManager

    private val mIBinder: DownloadBinder = DownloadBinder()

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private lateinit var notification: Notification

    inner class DownloadBinder: Binder(){

        fun startDownload(m: MusicInfo) {
            Log.d("MyService", "startDownload executed")
            m?.run {
                if (songUrl == null || songUrl!!.isEmpty()){
                    toast("${m.name}歌曲获取链接无效，下载失败")
                }
                val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel = NotificationChannel(DOWN_NOTIFICATION_CHANNEL_ID, "前台下载Service通知", NotificationManager.IMPORTANCE_DEFAULT)
                    channel.setSound(null, null)//无声音
                    channel.enableVibration(false)//无震动
                    manager.createNotificationChannel(channel)
                }
                val intent = Intent(this@DownloadService, MainActivity::class.java)
                val pi = PendingIntent.getActivity(this@DownloadService, 0, intent, 0)
                val builder = NotificationCompat.Builder(this@DownloadService, DOWN_NOTIFICATION_CHANNEL_ID)
                notification = builder
                    .setContentTitle(m.name)
                    .setSmallIcon(R.drawable.logo_no_fill)
                    .setContentIntent(pi)
                    .build()
                // 使用Glide加载图像
                glide.asBitmap()
                    .load(m.album?.cover)
                    .into(object : CustomTarget<Bitmap?>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap?>?
                        ) {
                            // 图像加载成功后，将其设置为通知小图标
                            builder.setLargeIcon(
                                BitmapFactory.decodeResource(
                                    resources,
                                    R.drawable.logo_no_fill
                                )
                            ).setLargeIcon(resource) // 将图像设置为通知的大图标
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {

                        }
                    })

                //设置任务栏中下载进程显示的views
                val downloadMusic = repository.downloadMusic(m)
                //前台服务启动
                startForeground(DOWN_NOTIFICATION_ID, notification)
                downloadMusic.catch { LogE("catch... when searching", t = it) }
                    .onEach {
                        when(it.status){
                            Status.SUCCESS -> {
                                LogE("下载文件:$it")
                                toast("下载歌曲：${m.name}成功")
                                val num = NumberFormat.getPercentInstance()
                                num.maximumFractionDigits = 2
                                builder.setProgress(1, 1 , false)
                                builder.setContentText("已下载" + num.format(1))
                                notification = builder.build()
                                manager.notify(DOWN_NOTIFICATION_ID, notification)
                                stopForeground(true)
                                manager.cancel(DOWN_NOTIFICATION_ID)
                                thread {
                                    stopSelf()
                                }
                            }
                            Status.LOADING -> {
                                val num = NumberFormat.getPercentInstance()
                                num.maximumFractionDigits = 2
                                LogE("下载歌曲${m.name}进度:"+
                                        num.format(it.process))
                                // 更新进度
                                it.process?.let { it1 ->
                                    builder.setProgress(10000, (it1 * 10000).toInt() , false)
                                    builder.setContentText("已下载" + num.format(it.process))
                                    notification = builder.build()
                                    manager.notify(DOWN_NOTIFICATION_ID, notification)
                                }
                            }
                            Status.ERROR -> {
                                toast(it.message!!)
                                manager.cancel(DOWN_NOTIFICATION_ID)
                                stopForeground(true)
                                thread {
                                    stopSelf()
                                }
                            }
                        }
                    }
                    .flowOn(Dispatchers.Main)
                    .launchIn(serviceScope)
            }
        }
    }

    override fun onBind(intent: Intent): IBinder {
        return mIBinder
    }

    override fun onCreate() {
        super.onCreate()
        // 处理具体的逻辑
        Log.d("MyService", "onCreate executed")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d("MyService", "onStartCommand executed")
        return super.onStartCommand(intent, flags, startId)
    }


    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
    }

    //安装下载后的apk文件
    private fun install(file: File, context: Context){
        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.action = Intent.ACTION_VIEW
        intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive")
        context.startActivity(intent)
    }
}