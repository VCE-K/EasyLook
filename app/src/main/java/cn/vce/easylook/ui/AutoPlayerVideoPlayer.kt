/*
 * Copyright (c) 2020. vipyinzhiwei <vipyinzhiwei@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.vce.easylook.ui

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import cn.vce.easylook.R
import cn.vce.easylook.utils.gone
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYVideoView

/**
 * 常见列表，视频播放器。
 *
 * @author vipyinzhiwei
 * @since 2020/5/26
 */
class AutoPlayerVideoPlayer : StandardGSYVideoPlayer {

    private var start: ImageView? = null

    var mCoverImage: ImageView? = null

    var mCoverOriginUrl: String? = null

    var mCoverOriginId = 0

    var mDefaultRes = 0

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, fullFlag: Boolean?) : super(context, fullFlag)

    override fun getLayoutId() = R.layout.video_layout_cover

    override fun init(context: Context?) {
        super.init(context)
            //start = findViewById(R.id.start)
        mCoverImage = findViewById<View>(R.id.thumbImage) as ImageView
    }

    override fun touchSurfaceMoveFullLogic(absDeltaX: Float, absDeltaY: Float) {
        super.touchSurfaceMoveFullLogic(absDeltaX, absDeltaY)
        //不给触摸快进，如果需要，屏蔽下方代码即可
        mChangePosition = false

        //不给触摸音量，如果需要，屏蔽下方代码即可
        mChangeVolume = false

        //不给触摸亮度，如果需要，屏蔽下方代码即可
        mBrightness = false
    }

    override fun updateStartImage() {
        if (mStartButton is ImageView) {
            val imageView = mStartButton as ImageView
            when (mCurrentState) {
                GSYVideoView.CURRENT_STATE_PLAYING -> imageView.setImageResource(R.drawable.ic_pause_black_24dp)
                GSYVideoView.CURRENT_STATE_ERROR -> imageView.setImageResource(R.drawable.ic_play_arrow_black_24dp)
                else -> imageView.setImageResource(R.drawable.ic_play_arrow_black_24dp)
            }
        } else {
            super.updateStartImage()
        }
    }

    override fun touchDoubleUp(e: MotionEvent?) {
        super.touchDoubleUp(e)
        //不需要双击暂停
    }

    //正常
    override fun changeUiToNormal() {
        super.changeUiToNormal()
        mBottomContainer.gone()
    }

    //准备中
    override fun changeUiToPreparingShow() {
        super.changeUiToPreparingShow()
        mBottomContainer.gone()
    }

    //播放中
    override fun changeUiToPlayingShow() {
        super.changeUiToPlayingShow()
        mBottomContainer.gone()
        start?.gone()
    }

    //开始缓冲
    override fun changeUiToPlayingBufferingShow() {
        super.changeUiToPlayingBufferingShow()
    }

    //暂停
    override fun changeUiToPauseShow() {
        super.changeUiToPauseShow()
        mBottomContainer.gone()
    }

    //自动播放结束
    override fun changeUiToCompleteShow() {
        super.changeUiToCompleteShow()
    }

    //错误状态
    override fun changeUiToError() {
        super.changeUiToError()
    }

    fun loadCoverImage(url: String, res: Int) {
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

    fun loadCoverImageBy(id: Int, res: Int) {
        mCoverOriginId = id
        mDefaultRes = res
        mCoverImage!!.setImageResource(id)
    }


    override fun startWindowFullscreen(
        context: Context?,
        actionBar: Boolean,
        statusBar: Boolean
    ): GSYBaseVideoPlayer? {
        val gsyBaseVideoPlayer = super.startWindowFullscreen(context, actionBar, statusBar)
        val sampleCoverVideo = gsyBaseVideoPlayer as SampleCoverVideo
        if (mCoverOriginUrl != null) {
            sampleCoverVideo.loadCoverImage(mCoverOriginUrl, mDefaultRes)
        } else if (mCoverOriginId != 0) {
            sampleCoverVideo.loadCoverImageBy(mCoverOriginId, mDefaultRes)
        }
        return gsyBaseVideoPlayer
    }
}