package cn.vce.easylook.feature_music.presentation.home_music.music_local

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import cn.vce.easylook.utils.LogE

class RecyclerScroView: RecyclerView {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?): super(context, attrs)

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val intercptFlag = super.onInterceptTouchEvent(ev)
        LogE("onInterceptTouchEvent==${intercptFlag}==" +ev.action)
        return intercptFlag
    }
}