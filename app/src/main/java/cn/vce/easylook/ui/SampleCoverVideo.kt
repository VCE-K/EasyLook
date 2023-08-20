package cn.vce.easylook.ui

import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import android.view.*
import android.widget.ImageView
import android.widget.SeekBar
import cn.vce.easylook.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.shuyu.gsyvideoplayer.utils.CommonUtil
import com.shuyu.gsyvideoplayer.utils.Debuger
import com.shuyu.gsyvideoplayer.utils.GSYVideoType
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer

/**
 * 带封面
 * Created by guoshuyu on 2017/9/3.
 */
class SampleCoverVideo : StandardGSYVideoPlayer {
    var mCoverImage: ImageView? = null
    var mCoverOriginUrl: String? = null
    var mCoverOriginId = 0
    var mDefaultRes = 0

    var needMuteImage: ImageView? = null

    constructor(context: Context?, fullFlag: Boolean?) : super(context, fullFlag) {}
    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}

    override fun init(context: Context) {
        super.init(context)
        mCoverImage = findViewById<View>(R.id.thumbImage) as ImageView
        needMuteImage = findViewById<View>(R.id.needMute) as ImageView
        if (mThumbImageViewLayout != null &&
            (mCurrentState == -1 || mCurrentState == CURRENT_STATE_NORMAL || mCurrentState == CURRENT_STATE_ERROR)) {
            mThumbImageViewLayout.visibility = VISIBLE
        }
        /*needMuteImage?.setOnClickListener{
            //拿到当前是否静音
            GSYVideoManager.instance().isNeedMute
            val res = if (GSYVideoManager.instance().isNeedMute) {
                R.drawable.ic_yes_needmute
            } else {
                R.drawable.ic_no_needmute
            }
            val needMuteImageImage = findViewById<View>(R.id.needMute) as ImageView
            Glide.with(context.applicationContext).setDefaultRequestOptions(
                RequestOptions()
                    .placeholder(R.drawable.ic_image)
                    .error(R.drawable.ic_image)
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
            )
                .load(res)
                .into(needMuteImageImage)
        }*/
    }

    override fun getLayoutId(): Int {
        return R.layout.video_layout_cover
    }

    fun loadCoverImage(url: String? = "", res: Int = R.drawable.ic_image) {
        mCoverOriginUrl = url
        mDefaultRes = res
        Glide.with(context.applicationContext)
            .setDefaultRequestOptions(
                RequestOptions()
                    .frame(1000000)
                    .centerCrop()
                    .error(res)
                    .placeholder(res)
            )
            .load(url)
            .into(mCoverImage!!)
    }

    fun loadCoverImageBy(id: Int, res: Int = R.drawable.ic_image) {
        mCoverOriginId = id
        mDefaultRes = res
        mCoverImage!!.setImageResource(id)
    }

    override fun startWindowFullscreen(
        context: Context,
        actionBar: Boolean,
        statusBar: Boolean
    ): GSYBaseVideoPlayer {
        val gsyBaseVideoPlayer = super.startWindowFullscreen(context, actionBar, statusBar)
        val sampleCoverVideo = gsyBaseVideoPlayer as SampleCoverVideo
        if (mCoverOriginUrl != null) {
            sampleCoverVideo.loadCoverImage(mCoverOriginUrl, mDefaultRes)
        } else if (mCoverOriginId != 0) {
            sampleCoverVideo.loadCoverImageBy(mCoverOriginId, mDefaultRes)
        }
        return gsyBaseVideoPlayer
    }

    override fun showSmallVideo(
        size: Point,
        actionBar: Boolean,
        statusBar: Boolean
    ): GSYBaseVideoPlayer {
        //下面这里替换成你自己的强制转化
        val sampleCoverVideo = super.showSmallVideo(size, actionBar, statusBar) as SampleCoverVideo
        sampleCoverVideo.mStartButton.visibility = GONE
        sampleCoverVideo.mStartButton = null
        return sampleCoverVideo
    }

    override fun cloneParams(from: GSYBaseVideoPlayer, to: GSYBaseVideoPlayer) {
        super.cloneParams(from, to)
        val sf = from as SampleCoverVideo
        val st = to as SampleCoverVideo
        st.mShowFullAnimation = sf.mShowFullAnimation
    }

    /**
     * 退出window层播放全屏效果
     */
    override fun clearFullscreenLayout() {
        if (!mFullAnimEnd) {
            return
        }
        mIfCurrentIsFullscreen = false
        var delay = 0
        // ------- ！！！如果不需要旋转屏幕，可以不调用！！！-------
        // 不需要屏幕旋转，还需要设置 setNeedOrientationUtils(false)
        if (mOrientationUtils != null) {
            delay = mOrientationUtils.backToProtVideo()
            mOrientationUtils.isEnable = false
            if (mOrientationUtils != null) {
                mOrientationUtils.releaseListener()
                mOrientationUtils = null
            }
        }
        if (!mShowFullAnimation) {
            delay = 0
        }
        val vp =
            CommonUtil.scanForActivity(context).findViewById<ViewGroup>(Window.ID_ANDROID_CONTENT)
        val oldF = vp.findViewById<View>(fullId)
        if (oldF != null) {
            //此处fix bug#265，推出全屏的时候，虚拟按键问题
            val gsyVideoPlayer = oldF as SampleCoverVideo
            gsyVideoPlayer.mIfCurrentIsFullscreen = false
        }
        if (delay == 0) {
            backToNormal()
        } else {
            postDelayed({ backToNormal() }, delay.toLong())
        }
    }

    /******************* 下方两个重载方法，在播放开始前不屏蔽封面，不需要可屏蔽  */
    override fun onSurfaceUpdated(surface: Surface) {
        super.onSurfaceUpdated(surface)
        if (mThumbImageViewLayout != null && mThumbImageViewLayout.visibility == VISIBLE) {
            mThumbImageViewLayout.visibility = INVISIBLE
        }
    }

    override fun setViewShowState(view: View, visibility: Int) {
        if (view === mThumbImageViewLayout && visibility != VISIBLE) {
            return
        }
        super.setViewShowState(view, visibility)
    }

    override fun onSurfaceAvailable(surface: Surface) {
        super.onSurfaceAvailable(surface)
        if (GSYVideoType.getRenderType() != GSYVideoType.TEXTURE) {
            if (mThumbImageViewLayout != null && mThumbImageViewLayout.visibility == VISIBLE) {
                mThumbImageViewLayout.visibility = INVISIBLE
            }
        }
    }

    /******************* 下方重载方法，在播放开始不显示底部进度和按键，不需要可屏蔽  */
    protected var byStartedClick = false
    override fun onClickUiToggle(e: MotionEvent) {
        if (mIfCurrentIsFullscreen && mLockCurScreen && mNeedLockFull) {
            setViewShowState(mLockScreen, VISIBLE)
            return
        }
        byStartedClick = true
        super.onClickUiToggle(e)
    }

    override fun changeUiToNormal() {
        super.changeUiToNormal()
        byStartedClick = false
    }

    override fun changeUiToPreparingShow() {
        super.changeUiToPreparingShow()
        Debuger.printfLog("Sample changeUiToPreparingShow")
        setViewShowState(mBottomContainer, INVISIBLE)
        setViewShowState(mStartButton, INVISIBLE)
    }

    override fun changeUiToPlayingBufferingShow() {
        super.changeUiToPlayingBufferingShow()
        Debuger.printfLog("Sample changeUiToPlayingBufferingShow")
        if (!byStartedClick) {
            setViewShowState(mBottomContainer, INVISIBLE)
            setViewShowState(mStartButton, INVISIBLE)
        }
    }

    override fun changeUiToPlayingShow() {
        super.changeUiToPlayingShow()
        Debuger.printfLog("Sample changeUiToPlayingShow")
        if (!byStartedClick) {
            setViewShowState(mBottomContainer, INVISIBLE)
            setViewShowState(mStartButton, INVISIBLE)
        }
    }

    override fun startAfterPrepared() {
        super.startAfterPrepared()
        Debuger.printfLog("Sample startAfterPrepared")
        setViewShowState(mBottomContainer, INVISIBLE)
        setViewShowState(mStartButton, INVISIBLE)
        setViewShowState(mBottomProgressBar, VISIBLE)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
        byStartedClick = true
        super.onStartTrackingTouch(seekBar)
    }

    fun setNeedMuteImage(isNeedMute: Boolean) {
        val res = if (isNeedMute) {
            R.drawable.ic_no_needmute
        } else {
            R.drawable.ic_yes_needmute
        }
        val needMuteImageImage = findViewById<View>(R.id.needMute) as ImageView
        Glide.with(context.applicationContext).setDefaultRequestOptions(
            RequestOptions()
                .placeholder(R.drawable.ic_image)
                .error(R.drawable.ic_image)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
        )
            .load(res)
            .into(needMuteImageImage)
    }
}