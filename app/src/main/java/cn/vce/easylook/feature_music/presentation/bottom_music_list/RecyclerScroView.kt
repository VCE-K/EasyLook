package cn.vce.easylook.feature_music.presentation.bottom_music_list

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import cn.vce.easylook.utils.toast
import com.drake.brv.layoutmanager.HoverLinearLayoutManager

class RecyclerScroView: androidx.recyclerview.widget.RecyclerView {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?): super(context, attrs)

    private var startY: Float = 0f

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev != null) {
            toast("View dispatchTouchEvent:"+ev.action)
        }
        return super.dispatchTouchEvent(ev)
    }
}