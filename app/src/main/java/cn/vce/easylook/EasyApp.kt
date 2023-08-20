package cn.vce.easylook

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.cyl.musicapi.BaseApiImpl
import com.drake.brv.PageRefreshLayout
import com.drake.brv.utils.BRV
import com.drake.statelayout.StateConfig
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

        // 初始化BindingAdapter的默认绑定ID
        BRV.modelId = BR.m
        // 禁止错误缺省页启用下拉刷新
        PageRefreshLayout.refreshEnableWhenError = false

        /**
         *  推荐在Application中进行全局配置缺省页, 当然同样每个页面可以单独指定缺省页.
         *  具体查看 https://github.com/liangjingkanji/StateLayout
         */
        StateConfig.apply {
            emptyLayout = R.layout.layout_empty
            errorLayout = R.layout.layout_error
            loadingLayout = R.layout.layout_loading

            setRetryIds(R.id.msg, R.id.iv)

            onLoading {
                // 此生命周期可以拿到LoadingLayout创建的视图对象, 可以进行动画设置或点击事件.
            }
        }
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout -> MaterialHeader(this) }
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, layout -> ClassicsFooter(this) }

        IjkPlayerManager.setLogLevel(if (BuildConfig.DEBUG) IjkMediaPlayer.IJK_LOG_WARN else IjkMediaPlayer.IJK_LOG_SILENT)
        //网易云初始化
        BaseApiImpl.initWebView(context)
        setLocale()

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



