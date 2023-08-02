package cn.vce.easylook.feature_music.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import cn.vce.easylook.MainActivity
import cn.vce.easylook.R
import kotlin.concurrent.thread

class DownloadService: Service() {

    private val mIBinder: DownloadBinder = DownloadBinder()
    inner class DownloadBinder: Binder(){
        fun startDownload() {
            Log.d("MyService", "startDownload executed")
        }
        fun getProgress(): Int {
            Log.d("MyService", "getProgress executed")
            return 0
        }
    }

    override fun onBind(intent: Intent): IBinder {
        return mIBinder
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("MyService", "onCreate executed")
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("my_service", "前台Service通知", NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }
        val intent = Intent(this, MainActivity::class.java)
        val pi = PendingIntent.getActivity(this, 0, intent, 0)
        val notification = NotificationCompat.Builder(this, "my_service")
            .setContentTitle("This is content title")
            .setContentText("This is content text")
            .setSmallIcon(R.drawable.logo_no_fill)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.logo_no_fill))
            .setContentIntent(pi)
            .build()
        startForeground(1, notification)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d("MyService", "onStartCommand executed")
        thread {
            // 处理具体的逻辑
            stopSelf()
        }
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
}