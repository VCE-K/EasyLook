package cn.vce.easylook.feature_video.presentation.home.detail

import android.os.Bundle
import cn.vce.easylook.R
import cn.vce.easylook.base.BaseVmFragment
import cn.vce.easylook.databinding.FragmentRefreshLayoutBinding
import cn.vce.easylook.databinding.ItemFollowCardTypeBinding
import cn.vce.easylook.extension.conversionVideoDuration
import cn.vce.easylook.extension.load
import cn.vce.easylook.feature_video.models.Daily
import cn.vce.easylook.feature_video.models.toVideoInfo
import cn.vce.easylook.utils.gone
import cn.vce.easylook.utils.visible
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment: BaseVmFragment<FragmentRefreshLayoutBinding>() {

    private lateinit var viewModel: DailyViewModel


    override fun init(savedInstanceState: Bundle?) {
        initView()
    }

    override fun initView() {
        binding.apply {
            page.onRefresh {
                viewModel.onEvent(DailyEvent.Search(this::addData))
            }/*.onLoadMore {
                viewModel.onEvent(DailyEvent.Search {
                    addData(it) { it.isNotEmpty() }
                })
            }*/.autoRefresh()
            recyclerView.linear().setup {
                addType<Daily.Item>(R.layout.item_follow_card_type)
                onBind {
                    val binding = getBinding<ItemFollowCardTypeBinding>()
                    val item = getModel<Daily.Item>()
                    val ivVideo = binding.ivVideo
                    val ivAvatar = binding.ivAvatar
                    val tvVideoDuration = binding.tvVideoDuration
                    val tvDescription = binding.tvDescription
                    val tvTitle = binding.tvTitle
                    val ivChoiceness = binding.ivChoiceness

                    ivVideo.load(item.data.content.data.cover.feed, 4f)
                    ivAvatar.load(item.data.header.icon ?: "")
                    tvVideoDuration.text = item.data.content.data.duration.conversionVideoDuration()
                    tvDescription.text = item.data.header.description
                    tvTitle.text = item.data.header.title
                    if (item.data.content.data.library == "DAILY") ivChoiceness.visible() else ivChoiceness.gone()
                    itemView.setOnClickListener {
                        val bundle = Bundle().apply {
                            putSerializable("videoInfo", item.toVideoInfo())
                        }
                        nav().navigate(R.id.action_video_fragment_dest_to_VideoDetailFragment, bundle)
                    }
                }

            }
        }
    }


    override fun initFragmentViewModel() {
        viewModel = getFragmentViewModel()
    }

    override fun getLayoutId(): Int? = R.layout.fragment_refresh_layout

}