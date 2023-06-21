package cn.vce.easylook

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.cyl.musicapi.BaseApiImpl
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.MaterialHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.shuyu.gsyvideoplayer.player.IjkPlayerManager
import dagger.hilt.android.HiltAndroidApp
import tv.danmaku.ijk.media.player.IjkMediaPlayer
import java.util.*

@HiltAndroidApp
class EasyApp: Application(){

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext

        IjkPlayerManager.setLogLevel(if (BuildConfig.DEBUG) IjkMediaPlayer.IJK_LOG_WARN else IjkMediaPlayer.IJK_LOG_SILENT)
        //网易云初始化
        BaseApiImpl.initWebView(context)
        setLocale()

        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout -> MaterialHeader(this) }
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, layout -> ClassicsFooter(this) }
        //setUpTheme()
    }

    private fun setUpTheme() {
        if (isDarkTheme(context)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun isDarkTheme(context: Context): Boolean {
        val flag = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return flag == Configuration.UI_MODE_NIGHT_YES
    }

    //根据系统语言切换显示文字
    private fun setLocale() {
        val configuration = resources.configuration
        val locale = Locale.getDefault()
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }
}



