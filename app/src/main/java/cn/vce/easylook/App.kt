package cn.vce.easylook

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp
import java.util.*

@HiltAndroidApp
class App: Application(){

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        setLocale()
    }
    //根据系统语言切换显示文字
    private fun setLocale() {
        val configuration = resources.configuration
        val locale = Locale.getDefault()
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }
}


