package cn.vce.easylook.feature_music.presentation.home_music

import android.content.Context
import android.gesture.GestureOverlayView.ORIENTATION_HORIZONTAL
import android.graphics.Rect
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager2.widget.ViewPager2
import cn.vce.easylook.utils.LogE
import java.lang.Math.abs
import kotlin.math.absoluteValue

class ConstraintSlideLayout: ConstraintLayout {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?): super(context, attrs)


    private var mViewPager2: ViewPager2? = null
    private var disallowParentInterceptDownEvent = true
    private var startX = 0
    private var startY = 0



    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {


        LogE("onInterceptTouchEvent:"+ev.action)
        /*when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = ev.x.toInt()
                startY = ev.y.toInt()
                //val isViewPagePointFlag = isPointInView(startX.toFloat(), startY.toFloat(), (mViewPager2 as View))

                //parent.requestDisallowInterceptTouchEvent(!disallowParentInterceptDownEvent)
            }
            MotionEvent.ACTION_MOVE -> {
                val endX = ev.x.toInt()
                val endY = ev.y.toInt()
                val disX = abs(endX - startX)
                val disY = abs(endY - startY)

                if (mViewPager2!!.orientation == ViewPager2.ORIENTATION_VERTICAL) {
                    onVerticalActionMove(endY, disX, disY)
                } else if (mViewPager2!!.orientation == ViewPager2.ORIENTATION_HORIZONTAL) {
                    onHorizontalActionMove(endX, disX, disY)
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> parent.requestDisallowInterceptTouchEvent(
                false
            )
        }*/
        return super.onInterceptTouchEvent(ev)
    }

    private fun onHorizontalActionMove(endX: Int, disX: Int, disY: Int) {
        if (mViewPager2?.adapter == null) {
            return
        }
        if (disX > disY) {
            val currentItem = mViewPager2?.currentItem
            val itemCount = mViewPager2?.adapter!!.itemCount
            if (currentItem == 0 && endX - startX > 0) {
                parent.requestDisallowInterceptTouchEvent(false)
            } else {
                parent.requestDisallowInterceptTouchEvent(currentItem != itemCount - 1
                        || endX - startX >= 0)
            }
        } else if (disY > disX) {
            parent.requestDisallowInterceptTouchEvent(false)
        }
    }

    private fun onVerticalActionMove(endY: Int, disX: Int, disY: Int) {
        if (mViewPager2?.adapter == null) {
            return
        }
        val currentItem = mViewPager2?.currentItem
        val itemCount = mViewPager2?.adapter!!.itemCount
        if (disY > disX) {
            if (currentItem == 0 && endY - startY > 0) {
                parent.requestDisallowInterceptTouchEvent(false)
            } else {
                parent.requestDisallowInterceptTouchEvent(currentItem != itemCount - 1
                        || endY - startY >= 0)
            }
        } else if (disX > disY) {
            parent.requestDisallowInterceptTouchEvent(false)
        }
    }

    /**
     * 设置是否允许在当前View的{@link MotionEvent#ACTION_DOWN}事件中禁止父View对事件的拦截，该方法
     * 用于解决CoordinatorLayout+CollapsingToolbarLayout在嵌套ViewPager2Container时引起的滑动冲突问题。
     *
     * 设置是否允许在ViewPager2Container的{@link MotionEvent#ACTION_DOWN}事件中禁止父View对事件的拦截，该方法
     * 用于解决CoordinatorLayout+CollapsingToolbarLayout在嵌套ViewPager2Container时引起的滑动冲突问题。
     *
     * @param disallowParentInterceptDownEvent 是否允许ViewPager2Container在{@link MotionEvent#ACTION_DOWN}事件中禁止父View拦截事件，默认值为false
     *   true 不允许ViewPager2Container在{@link MotionEvent#ACTION_DOWN}时间中禁止父View的时间拦截，
     *   设置disallowIntercept为true可以解决CoordinatorLayout+CollapsingToolbarLayout的滑动冲突
     *   false 允许ViewPager2Container在{@link MotionEvent#ACTION_DOWN}时间中禁止父View的时间拦截，
     */
    fun disallowParentInterceptDownEvent(disallowParentInterceptDownEvent: Boolean) {
        this.disallowParentInterceptDownEvent = disallowParentInterceptDownEvent
    }



    private fun isPointInView(x: Float, y: Float, view: View): Boolean {
        val rect = Rect()
        view.getGlobalVisibleRect(rect)
        return rect.contains(x.toInt(), y.toInt())
    }
}