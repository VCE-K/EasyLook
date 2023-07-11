package cn.vce.easylook.feature_music.presentation.bottom_music_list

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.drake.brv.PageRefreshLayout
import com.drake.brv.layoutmanager.HoverLinearLayoutManager

class PageScroRefreshLayout: PageRefreshLayout {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?): super(context, attrs)

    private var startY: Float = 0f
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        ev?.apply {
            when(ev.action){
                MotionEvent.ACTION_DOWN -> {
                    startY = y //记录开始点击方位，便于记录上滑和下滑
                }
                MotionEvent.ACTION_MOVE -> {
                    if (y > startY){//下滑
                        return false
                    }else{//上滑
                        return false
                    }
                }
                MotionEvent.ACTION_UP -> {

                }
            }
        }

        return super.onInterceptTouchEvent(ev)
    }

    override fun dispatchTouchEvent(e: MotionEvent?): Boolean {
        return super.dispatchTouchEvent(e)
    }

    override fun setOnTouchListener(l: OnTouchListener?) {
        super.setOnTouchListener(l)
    }
}