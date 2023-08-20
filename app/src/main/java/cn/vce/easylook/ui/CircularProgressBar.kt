package cn.vce.easylook.ui

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import cn.vce.easylook.R

open class CircularProgressBar(context: Context, @Nullable attrs: AttributeSet?, defStyleAttr: Int) :
    View(context, attrs, defStyleAttr) {
    /**
     * 半径
     */
    private val mRadius: Int

    /**
     * 进度条宽度
     */
    private val mStrokeWidth: Int

    /**
     * 进度条背景颜色
     */
    private val mProgressbarBgColor: Int

    /**
     * 进度条进度颜色
     */
    private val mProgressColor: Int

    /**
     * 开始角度
     */
    private val mStartAngle = 0

    /**
     * 当前角度
     */
    private var mCurrentAngle = 0f

    /**
     * 结束角度
     */
    private val mEndAngle = 360

    /**
     * 最大进度
     */
    private val mMaxProgress: Float

    /**
     * 当前进度
     */
    private var mCurrentProgress: Float

    /**
     * 文字
     */
    private var mText: String

    /**
     * 文字颜色
     */
    private var mTextColor: Int

    /**
     * 文字大小
     */
    private var mTextSize: Int

    /**
     * 动画的执行时长
     */
    private val mDuration: Long = 1000

    /**
     * 是否执行动画
     */
    private var isAnimation = false

    constructor(context: Context) : this(context, null) {}
    constructor(context: Context, @Nullable attrs: AttributeSet?) : this(context, attrs, 0) {}

    init {
        val array: TypedArray =
            context.obtainStyledAttributes(attrs, R.styleable.CircularProgressBar)
        mRadius = array.getDimensionPixelSize(R.styleable.CircularProgressBar_radius, 80)
        mStrokeWidth = array.getDimensionPixelSize(R.styleable.CircularProgressBar_strokeWidth, 8)
        mProgressbarBgColor = array.getColor(
            R.styleable.CircularProgressBar_progressbarBackgroundColor,
            ContextCompat.getColor(context, R.color.teal_700)
        )
        mProgressColor = array.getColor(
            R.styleable.CircularProgressBar_progressbarColor,
            ContextCompat.getColor(context, R.color.teal_200)
        )
        mMaxProgress = array.getInt(R.styleable.CircularProgressBar_maxProgress, 100).toFloat()
        mCurrentProgress = array.getInt(R.styleable.CircularProgressBar_progress, 0).toFloat()
        val text: String? = array.getString(R.styleable.CircularProgressBar_text)
        mText = text ?: ""
        mTextColor = array.getColor(
            R.styleable.CircularProgressBar_textColor,
            ContextCompat.getColor(context, R.color.black)
        )
        mTextSize = array.getDimensionPixelSize(
            R.styleable.CircularProgressBar_textSize, TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_SP, 14F, resources.displayMetrics).toInt()
        )
        array.recycle()
    }

    //测量范围
    protected override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var width = 0 // 圆环的宽度
        when (MeasureSpec.getMode(widthMeasureSpec)) {
            MeasureSpec.UNSPECIFIED -> Unit //MeasureSpec.UNSPECIFIED，表示宽度没有限制
            MeasureSpec.AT_MOST ->  { //MeasureSpec.AT_MOST，表示宽度上有一个最大限制
                width = mRadius * 2
            }
            MeasureSpec.EXACTLY ->{ // MeasureSpec.EXACTLY，表示宽度已经精确指定
                width = MeasureSpec.getSize(widthMeasureSpec)
            }
        }
        // 将计算得到的宽度和高度应用到 View 上，因为是圆环，所以宽高一致
        setMeasuredDimension(width, width)
    }

    //绘制
    protected override fun onDraw(canvas: Canvas) {
        val centerX = width / 2
        val rectF = RectF() //矩形，正方形也是矩形的一种
        rectF.left = mStrokeWidth.toFloat()
        rectF.top = mStrokeWidth.toFloat()
        rectF.right = centerX * 2 - mStrokeWidth.toFloat()
        rectF.bottom = centerX * 2 - mStrokeWidth.toFloat()

        //绘制进度条背景
        drawProgressbarBg(canvas, rectF)
        //绘制进度
        drawProgress(canvas, rectF)
        //绘制中心文本
        drawCenterText(canvas, centerX)
    }

    /**
     * 绘制进度条背景
     */
    private fun drawProgressbarBg(canvas: Canvas, rectF: RectF) {
        val mPaint = Paint()
        //画笔的填充样式，Paint.Style.STROKE 描边
        mPaint.style = Paint.Style.STROKE
        //圆弧的宽度
        mPaint.strokeWidth = mStrokeWidth.toFloat()
        //抗锯齿
        mPaint.isAntiAlias = true
        //画笔的颜色
        mPaint.color = mProgressbarBgColor
        //画笔的样式 Paint.Cap.Round 圆形
        mPaint.strokeCap = Paint.Cap.ROUND
        //开始画圆弧
        canvas.drawArc(rectF, mStartAngle.toFloat(), mEndAngle.toFloat(), false, mPaint)
    }
    /**
     * 绘制进度
     */
    private fun drawProgress(canvas: Canvas, rectF: RectF) {
        val paint = Paint()
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = mStrokeWidth.toFloat()
        paint.color = mProgressColor
        paint.isAntiAlias = true
        paint.strokeCap = Paint.Cap.ROUND
        if (!isAnimation) {
            mCurrentAngle = 360 * (mCurrentProgress / mMaxProgress)
        }
        canvas.drawArc(rectF, mStartAngle.toFloat(), mCurrentAngle, false, paint)
    }

    /**
     * 绘制中心文字
     */
    private fun drawCenterText(canvas: Canvas, centerX: Int) {
        val paint = Paint()
        paint.isAntiAlias = true
        paint.color = mTextColor
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = mTextSize.toFloat()
        val textBounds = Rect()
        paint.getTextBounds(mText, 0, mText.length, textBounds)
        canvas.drawText(mText, centerX.toFloat(),
            (textBounds.height() / 2 + height / 2).toFloat(), paint)
    }

    /**
     * 设置当前进度
     */
    public fun setProgress(progress: Float) {
        if (progress < 0) {
            throw IllegalArgumentException("Progress value can not be less than 0");
        }
        if (progress > mMaxProgress) {
            mCurrentProgress = mMaxProgress
        }else{
            mCurrentProgress = progress
        }
        mCurrentAngle = 360 * (mCurrentProgress / mMaxProgress)
        setAnimator(0f, mCurrentAngle)
    }

    /**
     * 设置文本
     */
    public fun setText(text: String) {
        mText = text
    }

    /**
     * 设置文本的颜色
     */
    public fun setTextColor(color: Int) {
        if (color <= 0) {
            throw IllegalArgumentException("Color value can not be less than 0")
        }
        mTextColor = color
    }

    /**
     * 设置文本的大小
     */
    public fun setTextSize(textSize: Float) {
        if (textSize <= 0) {
            throw IllegalArgumentException("textSize can not be less than 0")
        }
        mTextSize = textSize.toInt()
    }


    /**
     * 设置动画
     *
     * @param start  开始位置
     * @param target 结束位置
     */
    private fun setAnimator(start: Float, target: Float) {
        isAnimation = true
        val animator = ValueAnimator.ofFloat(start, target)
        animator.duration = mDuration;
        animator.setTarget(mCurrentAngle);
        //动画更新监听
        animator.addUpdateListener{
            mCurrentAngle = it.animatedValue as Float
            invalidate()
        }
        animator.start();
    }

}