package cn.vce.easylook.feature_video.presentation.video_detail

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import cn.vce.easylook.R
import cn.vce.easylook.base.BaseVmFragment
import cn.vce.easylook.databinding.ListVideoItemNormalBinding
import cn.vce.easylook.feature_music.models.PlaylistInfo
import cn.vce.easylook.extension.load
import cn.vce.easylook.ui.SampleCoverVideo
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VideoDetailFragment: BaseVmFragment<ListVideoItemNormalBinding>() {

    private lateinit var viewModel: VideoDetailVM

    private val gsyVideoOptionBuilder by lazy {
        GSYVideoOptionBuilder()
    }
    override fun init(savedInstanceState: Bundle?) {
        initView()
    }

    override fun initFragmentViewModel() {
        viewModel = getFragmentViewModel()
    }

    companion object {
        val TAG = javaClass.simpleName
    }

    override fun getLayoutId(): Int? = R.layout.list_video_item_normal


    override fun initView() {
        binding.apply {
            val dataList = mutableListOf<PlaylistInfo>()
            for (i in 0..18) {
                dataList.add(PlaylistInfo())
            }
            viewModel.videoInfo.observe(viewLifecycleOwner) {
                val data = it
                //(id, playUrl, title, description, category, library, consumption, cover, author, webUrl
                val gsyVideoPlayer = videoItemPlayer
                val url: String = data.playUrl
                val title: String = data.title
                //val layoutPosition = 1

                /*val header: MutableMap<String, String> = HashMap()
                header["ee"] = "33"*/
                //防止错位，离开释放
                //gsyVideoPlayer.initUIState();
                gsyVideoOptionBuilder
                    .setIsTouchWiget(false) //.setThumbImageView(imageView) 是否可以滑动界面改变进度，声音等 默认true
                    .setUrl(url)
                    .setVideoTitle(title)
                    .setCacheWithPlay(false)
                    .setRotateViewAuto(true)
                    .setLockLand(true)//一全屏就锁屏横屏，默认false竖屏，可配合setRotateViewAuto使用
                    .setPlayTag(TAG)
                    //.setMapHeadData(header)
                    .setShowFullAnimation(true)
                    .setNeedLockFull(true)
                    //.setPlayPosition(layoutPosition)//设置播放位置防止错位
                    .setVideoAllCallBack(object : GSYSampleCallBack() {
                        override fun onPrepared(url: String, vararg objects: Any) {
                            super.onPrepared(url, objects)
                            needMuteConfig(gsyVideoPlayer)
                        }

                        override fun onQuitFullscreen(url: String, vararg objects: Any) {
                            super.onQuitFullscreen(url, objects)
                            //全屏不静音
                            needMuteConfig(gsyVideoPlayer)
                        }

                        override fun onEnterFullscreen(url: String, vararg objects: Any) {
                            super.onEnterFullscreen(url, objects)
                            GSYVideoManager.instance().isNeedMute = false
                            gsyVideoPlayer.currentPlayer.titleTextView.text = objects[0] as String
                        }
                    }).build(gsyVideoPlayer)

                //增加title
                gsyVideoPlayer.titleTextView.visibility = View.GONE
                //设置返回键
                gsyVideoPlayer.backButton.visibility = View.GONE
                //设置全屏按键功能
                gsyVideoPlayer.fullscreenButton
                    .setOnClickListener {
                        resolveFullBtn(gsyVideoPlayer)
                    }
                gsyVideoPlayer.loadCoverImage(data.cover.detail, R.mipmap.xxx2)
            }
        }
    }


    fun onBackPressed(): Boolean {
        return GSYVideoManager.backFromWindowFull(requireActivity())
    }

    override fun onPause() {
        super.onPause()
        GSYVideoManager.onPause()
    }

    override fun onResume() {
        super.onResume()
        GSYVideoManager.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        GSYVideoManager.releaseAllVideos()
    }
    /**
     * 全屏幕按键处理
     */
    private fun resolveFullBtn(standardGSYVideoPlayer: StandardGSYVideoPlayer) {
        standardGSYVideoPlayer.startWindowFullscreen(requireActivity(), true, true)
    }


    private fun needMuteConfig(gsyVideoPlayer: SampleCoverVideo){
        if (!gsyVideoPlayer.isIfCurrentIsFullscreen) {
            //看用户选择是否竖屏静音
            val isNeedMute = if (viewModel.isNeedMuteSaved()){
                viewModel.getSavedIsNeedMute()
            }else{
                viewModel.saveIsNeedMuteSaved(isNeedMuteSaved = true)
                true
            }
            GSYVideoManager.instance().isNeedMute = isNeedMute
            val needMuteImageImage = requireActivity().findViewById<View>(R.id.needMute) as ImageView
            val res = if (isNeedMute) {
                R.drawable.ic_yes_needmute
            } else {
                R.drawable.ic_no_needmute
            }
            needMuteImageImage.load(res, 0f)
            needMuteImageImage.setOnClickListener {
                viewModel.saveIsNeedMuteSaved(!viewModel.getSavedIsNeedMute())
                GSYVideoManager.instance().isNeedMute = viewModel.getSavedIsNeedMute()
                val res = if (GSYVideoManager.instance().isNeedMute) {
                    R.drawable.ic_yes_needmute
                } else {
                    R.drawable.ic_no_needmute
                }
                needMuteImageImage.load(res, 0f)
            }
        }
    }
}