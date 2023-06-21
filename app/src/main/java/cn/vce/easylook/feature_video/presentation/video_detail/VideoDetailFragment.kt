package cn.vce.easylook.feature_video.presentation.video_detail

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.vce.easylook.R
import cn.vce.easylook.base.BaseVmFragment
import cn.vce.easylook.databinding.FragmentVideoDetailBinding
import cn.vce.easylook.databinding.ListVideoItemNormalBinding
import cn.vce.easylook.feature_music.models.PlaylistInfo
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer


class VideoDetailFragment: BaseVmFragment<FragmentVideoDetailBinding>() {

    override fun init(savedInstanceState: Bundle?) {
        initView()
    }

    companion object {
        val TAG = javaClass.simpleName
    }

    override fun getLayoutId(): Int? = R.layout.fragment_video_detail

    override fun initView() {
        binding.apply {
            val dataList = mutableListOf<PlaylistInfo>()
            for (i in 0..18) {
                dataList.add(PlaylistInfo())
            }

            listItemRecycler.linear().setup {
                addType<PlaylistInfo>(R.layout.list_video_item_normal)
                onBind {
                    val binding = getBinding<ListVideoItemNormalBinding>()
                    binding.apply {
                        val gsyVideoPlayer = videoItemPlayer
                        val url: String
                        val title: String
                        if (layoutPosition % 2 == 0) {
                            url =
                                "https://pointshow.oss-cn-hangzhou.aliyuncs.com/McTk51586843620689.mp4"
                            title = "这是title"
                        } else {
                            url = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"
                            title = "哦？Title？"
                        }

                        val header: MutableMap<String, String> = HashMap()
                        header["ee"] = "33"
                        //防止错位，离开释放
                        //gsyVideoPlayer.initUIState();

                        val gsyVideoOptionBuilder = GSYVideoOptionBuilder()
                        gsyVideoOptionBuilder
                            .setIsTouchWiget(false) //.setThumbImageView(imageView)
                            .setUrl(url)
                            .setVideoTitle(title)
                            .setCacheWithPlay(false)
                            .setRotateViewAuto(true)
                            .setLockLand(true)
                            .setPlayTag(TAG)
                            .setMapHeadData(header)
                            .setShowFullAnimation(true)
                            .setNeedLockFull(true)
                            .setPlayPosition(layoutPosition)
                            .setVideoAllCallBack(object : GSYSampleCallBack() {
                                override fun onPrepared(url: String, vararg objects: Any) {
                                    super.onPrepared(url, objects)
                                    if (!gsyVideoPlayer.isIfCurrentIsFullscreen) {
                                        //静音
                                        GSYVideoManager.instance().isNeedMute = true
                                    }
                                }

                                override fun onQuitFullscreen(url: String, vararg objects: Any) {
                                    super.onQuitFullscreen(url, objects)
                                    //全屏不静音
                                    GSYVideoManager.instance().isNeedMute = true
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
                        gsyVideoPlayer.loadCoverImageBy(R.mipmap.xxx2, R.mipmap.xxx2)
                    }
                }
            }.models = dataList

            listItemRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                var firstVisibleItem = 0
                var lastVisibleItem = 0

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val linearLayoutManager = listItemRecycler.layoutManager as LinearLayoutManager
                    firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition()
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition()
                    //大于0说明有播放
                    if (GSYVideoManager.instance().playPosition >= 0) {
                        //当前播放的位置
                        val position = GSYVideoManager.instance().playPosition
                        //对应的播放列表TAG
                        if (GSYVideoManager.instance().playTag == TAG
                            && (position < firstVisibleItem || position > lastVisibleItem)) {
                            //如果滑出去了上面和下面就是否，和今日头条一样
                            if (!GSYVideoManager.isFullState(activity)) {
                                GSYVideoManager.releaseAllVideos()
                                listItemRecycler.adapter?.notifyDataSetChanged()
                            }
                        }
                    }
                }
            })
        }
    }

    fun onBackPressed(): Boolean {
        return GSYVideoManager.backFromWindowFull(activity)
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
        standardGSYVideoPlayer.startWindowFullscreen(context, true, true)
    }

    private fun getUrl(): String? {
        val url = "android.resource://" + this.requireContext().packageName + "/" + R.raw.test
        //注意，用ijk模式播放raw视频，这个必须打开
        GSYVideoManager.instance().enableRawPlay(this.requireContext().applicationContext);
        //exo raw 支持
        //String url =  RawResourceDataSource.buildRawResourceUri(R.raw.test).toString();


        //return "https://oss.nbs.cn/M00/22/E4/wKhkDmPZ1uSAJWFwAlYRLUW4gK0892.mp3";
        return url
    }
}