package cn.vce.easylook.feature_video.presentation.home

import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.vce.easylook.R
import cn.vce.easylook.base.BaseVmFragment
import cn.vce.easylook.databinding.FragmentVideoHomeBinding
import cn.vce.easylook.databinding.VideoIndexVideoCardBinding
import cn.vce.easylook.databinding.VideoIndexVideoVideoBinding
import cn.vce.easylook.feature_video.adapters.HomeBannerAdapter
import cn.vce.easylook.feature_video.models.FollowCard
import cn.vce.easylook.feature_video.models.HomePageRecommend
import cn.vce.easylook.feature_video.models.toVideoInfo
import cn.vce.easylook.feature_video.other.getVideoHolderXML
import cn.vce.easylook.feature_video.presentation.VideoHomeEvent
import cn.vce.easylook.ui.SampleCoverVideo
import com.bumptech.glide.RequestManager
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.youth.banner.indicator.RoundLinesIndicator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class VideoHomeFragment : BaseVmFragment<FragmentVideoHomeBinding>() {

    @Inject
    lateinit var glide: RequestManager
    lateinit var viewModel: VideoHomeVM
    private val gsyVideoOptionBuilder by lazy {
        GSYVideoOptionBuilder()
    }
    private val TAG = "RecyclerView2List"


    override fun initFragmentViewModel() {
        viewModel = getFragmentViewModel()
    }

    override fun initActivityViewModel() {
        mainVM = getActivityViewModel()
    }

    override fun getLayoutId(): Int? = R.layout.fragment_video_home

    override fun init(savedInstanceState: Bundle?) {
        binding.run {
            vm = viewModel

            rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                var firstVisibleItem = 0
                var lastVisibleItem = 0

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    when(val layoutManager = recyclerView.layoutManager){
                        is LinearLayoutManager -> {
                            firstVisibleItem = layoutManager.findFirstVisibleItemPosition()
                            lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                        }
                    }
                    //大于0说明有播放
                    if (GSYVideoManager.instance().playPosition >= 0) {
                        //当前播放的位置
                        val position = GSYVideoManager.instance().playPosition
                        //对应的播放列表TAG
                        if (GSYVideoManager.instance().playTag == TAG &&
                                (position < firstVisibleItem || position > lastVisibleItem)) {
                            //如果滑出去了上面和下面就是否，和今日头条一样
                            if (!GSYVideoManager.isFullState(mActivity)) {
                                GSYVideoManager.releaseAllVideos()
                                recyclerView.adapter?.notifyDataSetChanged()
                            }
                        }
                    }
                }
            })


            rv.linear().setup {
                addType<HomePageRecommend.Item>{
                    getVideoHolderXML(type)
                }
                onBind {
                    val indexItemBean = getModel<HomePageRecommend.Item>().data
                    val type = getModel<HomePageRecommend.Item>().type
                    when(itemViewType){
                        R.layout.video_index_video_video -> {
                            val binding = getBinding<VideoIndexVideoVideoBinding>()
                            when (type){
                                "followCard" -> {
                                    setItemVideoData(binding, indexItemBean.content.data, position = modelPosition)
                                }
                                "videoSmallCard" -> {
                                    setItemVideoData(binding, hData = indexItemBean, position = modelPosition)
                                }
                                "NORMAL" -> Unit
                                else -> Unit
                            }
                            val res = if (mainVM.getSavedIsNeedMute()) {
                                R.drawable.ic_no_needmute
                            } else {
                                R.drawable.ic_yes_needmute
                            }
                            binding.videoItemPlayer.needMuteImage?.setImageResource(res)
                        }
                        R.layout.video_index_video_card -> {
                            val bannerAdapter = HomeBannerAdapter(indexItemBean.itemList.toMutableList())
                            getBinding<VideoIndexVideoCardBinding>().banner.setAdapter(
                                bannerAdapter
                            ).setIndicator(RoundLinesIndicator(mActivity))
                                .setOnBannerListener { _, position ->
                                    //toast("点击: 轮播图(${position})")
                                    Bundle().apply {
                                        putSerializable("videoInfo", indexItemBean.itemList[position].data.content.data.toVideoInfo())
                                        nav().navigate(R.id.action_video_fragment_dest_to_VideoDetailFragment, this)
                                    }
                                }.setIntercept(false)
                        }
                    }
                }
                R.id.needMute.onClick {
                    when(itemViewType) {
                        R.layout.video_index_video_video -> {
                            val binding = getBinding<VideoIndexVideoVideoBinding>()
                            binding.videoItemPlayer.needMuteImage?.let { it1 -> needMuteConfig(it1, binding.videoItemPlayer) }
                        }
                    }
                }
            }
            page.onRefresh {
                viewModel.onEvent(VideoHomeEvent.LoadData)
            }.onLoadMore {
                viewModel.onEvent(VideoHomeEvent.LoadMoreData)
            }.autoRefresh()
        }
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

    fun onBackPressed(): Boolean {
        return GSYVideoManager.backFromWindowFull(activity)
    }


    private fun setItemVideoData(videoViewHolder: VideoIndexVideoVideoBinding,
                                 card: FollowCard? = null,
                                 hData: HomePageRecommend.Data? = null, position: Int){
        val data = card?:hData
        val playUrl = card?.playUrl ?: hData?.playUrl
        val title = card?.title ?: hData?.title
        val id = card?.id ?: hData?.id
        val consumption = card?.consumption ?: hData?.consumption
        val duration = card?.duration ?: hData?.duration
        val description = card?.description ?: hData?.description
        val cover = card?.cover ?: hData?.cover
        val author = card?.author ?: hData?.author

        //防止错位，离开释放
        //gsyVideoPlayer.initUIState();

        val gsyVideoPlayer = videoViewHolder.videoItemPlayer
        gsyVideoOptionBuilder
            .setIsTouchWiget(false) //.setThumbImageView(imageView)
            .setUrl(playUrl)
            .setVideoTitle(title)
            .setCacheWithPlay(false)
            .setRotateViewAuto(true)
            .setLockLand(true)
            .setPlayTag(TAG)
            //.setMapHeadData(header)
            .setShowFullAnimation(true)
            .setNeedLockFull(true)
            .setPlayPosition(position)
            .setVideoAllCallBack(object : GSYSampleCallBack() {
                override fun onPrepared(url: String, vararg objects: Any) {
                    super.onPrepared(url, *objects)
                    GSYVideoManager.instance().isNeedMute = mainVM.getSavedIsNeedMute()
                }

                override fun onQuitFullscreen(url: String, vararg objects: Any) {
                    super.onQuitFullscreen(url, *objects)
                    GSYVideoManager.instance().isNeedMute = mainVM.getSavedIsNeedMute()
                }

                override fun onEnterFullscreen(url: String, vararg objects: Any) {
                    super.onEnterFullscreen(url, *objects)
                    GSYVideoManager.instance().isNeedMute = mainVM.getSavedIsNeedMute()
                    gsyVideoPlayer.currentPlayer.titleTextView.text = objects[0] as String
                }
            }).build(gsyVideoPlayer)

        //增加title
        gsyVideoPlayer.titleTextView.visibility = View.GONE
        //设置返回键
        gsyVideoPlayer.backButton.visibility = View.GONE

        //设置全屏按键功能
        gsyVideoPlayer.fullscreenButton
            .setOnClickListener { resolveFullBtn(gsyVideoPlayer) }
        gsyVideoPlayer.loadCoverImage(cover?.feed)


        if (!TextUtils.isEmpty(description)) {
            videoViewHolder.itemMenu.visibility = View.VISIBLE
        } else {
            videoViewHolder.itemMenu.visibility = View.INVISIBLE
        }
        videoViewHolder.itemTitle.text = title
        if (null != author) {
            //用户头像
            glide.load(author!!.icon)
                .centerCrop()
                .into(videoViewHolder.itemUserCover)
        }else{
            glide.load(0)
                .centerCrop()
                .into(videoViewHolder.itemUserCover)
        }
    }

    private fun needMuteConfig(needMuteImage: ImageView, gsyVideoPlayer: SampleCoverVideo){
        if (!gsyVideoPlayer.isIfCurrentIsFullscreen) {
            //看用户选择是否竖屏静音
            val isNeedMute = if (mainVM.isNeedMuteSaved()){
                mainVM.getSavedIsNeedMute()
            }else{
                mainVM.saveIsNeedMuteSaved(isNeedMuteSaved = true)
                true
            }
            GSYVideoManager.instance().isNeedMute = !isNeedMute
            mainVM.saveIsNeedMuteSaved(!isNeedMute)
            val res = if (mainVM.getSavedIsNeedMute()) {
                R.drawable.ic_no_needmute
            } else {
                R.drawable.ic_yes_needmute
            }
            needMuteImage.setImageResource(res)
            //glide.load(res).into(needMuteImageImage)

        }
    }

    /**
     * 按比例缩放图片
     *
     * @param origin 原图
     * @param ratio 比例
     * @return 新的bitmap
     */
    private fun scaleBitmap(origin: Bitmap?, ratio: Float): Bitmap? {
        if (origin == null) {
            return null
        }
        val width = origin.width
        val height = origin.height
        val matrix = Matrix()
        matrix.preScale(ratio, ratio)
        val newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false)
        if (newBM == origin) {
            return newBM
        }
        origin.recycle()
        return newBM
    }

    /**
     * 全屏幕按键处理
     */
    private fun resolveFullBtn(standardGSYVideoPlayer: StandardGSYVideoPlayer) {
        standardGSYVideoPlayer.startWindowFullscreen(mActivity, true, true)
    }


}