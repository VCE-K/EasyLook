package cn.vce.easylook.feature_music.presentation.bottom_music_list

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import cn.vce.easylook.utils.toast

class ConstraintScroLayout: androidx.constraintlayout.widget.ConstraintLayout {
    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?): super(context, attrs)

    private var startY: Float = 0f


    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return super.dispatchTouchEvent(ev)
        if (ev != null) {
            toast("View dispatchTouchEvent:"+ev.action)
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        ev?.apply {
            when(ev.action){
                MotionEvent.ACTION_DOWN -> {
                    startY = y //记录开始点击方位，便于记录上滑和下滑
                    return false
                }
                MotionEvent.ACTION_MOVE -> {
                    return true
                }
                MotionEvent.ACTION_UP -> {

                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }
}