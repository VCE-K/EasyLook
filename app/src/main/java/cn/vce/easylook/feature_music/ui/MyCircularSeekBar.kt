package cn.vce.easylook.feature_music.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.SeekBar


@SuppressLint("AppCompatCustomView")
class MyCircularSeekBar: SeekBar {

    private var paint: Paint? = null

    //画笔
    private var radius //进度条的半径
            = 0f
    private var progress //当前进度值
            = 0
    private var angle //进度条的旋转角度
            = 0f

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?): super(context, attrs, 0)


    init {
        paint = Paint()
        paint?.let {
            it.style = Paint.Style.FILL
            it.isAntiAlias = true
            it.color = Color.BLACK
        }
    }

    //初始化

    //初始化
    @Synchronized
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        //计算进度条的半径
        radius = (width / 2).toFloat()
        //进度条的高度
        val progressHeight = height / 4
        //进度条的宽度
        val progressWidth = measuredWidth - paddingLeft - paddingRight
        //进度条的进度值
        val progress = getProgress()
        //进度条的角度
        angle = 360 * progress / max.toFloat()

        //绘制进度条
        canvas.drawArc(
            RectF(
                paddingLeft.toFloat(),
                paddingTop.toFloat(),
                width.toFloat(),
                height.toFloat()
            ), -90 + angle, angle - 90, false, paint!!
        )
    }

    //重写onMeasure方法
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //计算进度条的半径和进度条的高度
        radius = (width / 2).toFloat()
        val progressHeight = height / 4
        //进度条的宽度
        val progressWidth = measuredWidth - paddingLeft - paddingRight
        //设置进度条的高度
        setMeasuredDimension(progressWidth, progressHeight)
    }

    //重写onLayout方法
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        //根据进度条的高度计算进度条的宽度
        val progressWidth = measuredWidth - paddingLeft - paddingRight
        //计算进度条的进度值
        val progress = getProgress()
        //进度条的角度
        angle = 360 * progress / max.toFloat()
    }

    //重写setProgress方法
    override fun setProgress(progress: Int) {
        this.progress = progress
        //根据进度值计算出进度条的半径
        val progressWidth = measuredWidth - paddingLeft - paddingRight
        radius = (progressWidth / 2).toFloat()
        //计算进度条的角度
        angle = 360 * progress / max.toFloat()
        //重绘进度条
        invalidate()
    }
}