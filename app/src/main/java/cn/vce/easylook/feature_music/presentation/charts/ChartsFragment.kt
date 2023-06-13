package cn.vce.easylook.feature_music.presentation.charts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewbinding.ViewBinding
import cn.vce.easylook.R
import cn.vce.easylook.base.BaseFragment
import cn.vce.easylook.base.BaseVmFragment
import cn.vce.easylook.databinding.FragmentChartsBinding
import cn.vce.easylook.databinding.ItemChartsBinding
import cn.vce.easylook.databinding.ItemChartsLargeBinding
import cn.vce.easylook.feature_music.domain.entities.Playlist
import cn.vce.easylook.feature_music.other.Status
import cn.vce.easylook.utils.LogE
import com.bumptech.glide.RequestManager
import com.cyl.musicapi.bean.TopListBean
import com.drake.brv.BindingAdapter
import com.drake.brv.utils.grid
import com.drake.brv.utils.setup
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.min

@AndroidEntryPoint
class ChartsFragment : BaseVmFragment<FragmentChartsBinding>() {

    private lateinit var viewModel: ChartsViewModel

    @Inject
    lateinit var glide: RequestManager


    override fun init(savedInstanceState: Bundle?) {
        setupRecyclerView()
    }

    override fun initFragmentViewModel() {
        viewModel = getFragmentViewModel()
    }

    override fun getLayoutId(): Int? = R.layout.fragment_charts

    override fun observe() {
        //列表数据补充
        viewModel.apply {
            neteaseTopList.observe(viewLifecycleOwner) { result ->
                when(result.status) {
                    Status.SUCCESS -> {
                        binding.apply {
                            allProgressBar.isVisible = false
                            result.data?.let {
                                when (val adapter = chartListRcv.adapter) {
                                    is BindingAdapter -> adapter.models = it
                                }
                            }
                        }
                    }
                    Status.ERROR -> Unit
                    Status.LOADING -> binding.allProgressBar.isVisible = true
                }
            }
        }
    }

    private fun setupRecyclerView() = binding.apply {
        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = false
            viewModel.loadNeteaseTopList()
        }

        binding.apply {
            chartListRcv.grid(3).setup {
                addType<TopListBean>{
                    if (list?.size ?: 0 == 0){
                        R.layout.item_charts
                    }else {
                        R.layout.item_charts_large
                    }
                }
                onBind {
                    val topListBean = getModel<TopListBean>()
                    when (val viewBinding = getBinding<ViewBinding>()){
                        is ItemChartsLargeBinding -> {
                            val binding = getBinding<ItemChartsLargeBinding>()
                            val stringIds = arrayListOf(R.string.song_list_item_title_1, R.string.song_list_item_title_2, R.string.song_list_item_title_3)
                            glide.load(topListBean.cover).into(binding.ivCover)
                            for (i in 0 until min(topListBean.list?.size ?: 0, 3)) {
                                val music = topListBean.list!![i]
                                var artistIds = ""
                                var artistNames = ""
                                music.artists?.let {
                                    artistIds = it[0].id
                                    artistNames = it[0].name
                                    for (j in 1 until it.size - 1) {
                                        artistIds += ",${it[j].id}"
                                        artistNames += ",${it[j].name}"
                                    }
                                }
                                when (i) {
                                    0 -> binding.tvMusic1.text = this@ChartsFragment.getString(stringIds[i], music.name, artistNames)
                                    1 -> binding.tvMusic2.text = this@ChartsFragment.getString(stringIds[i], music.name, artistNames)
                                    2 -> binding.tvMusic3.text = this@ChartsFragment.getString(stringIds[i], music.name, artistNames)
                                }
                            }
                        }
                        is ItemChartsBinding -> {
                            viewBinding.apply {
                                glide.load(topListBean.cover).into(ivCover)
                                tvTitle.text = topListBean.name
                                ivCover.setOnClickListener {

                                }
                            }

                        }
                    }
                }
                onClick(R.id.chartsItem) {
                    val bundle = Bundle().apply {
                        getModel<TopListBean>()?.apply {
                            if (id != null && name != null){
                                putSerializable("playlist", Playlist(id!!, name!!, cover, description))
                                putString("title", name)
                            }
                        }
                    }
                    nav().navigate(R.id.action_chartsFragment_to_my_music_fragment_dest, bundle)
                }
                onClick(R.id.iv_cover) {
                    val bundle = Bundle().apply {
                        getModel<TopListBean>()?.apply {
                            if (id != null && name != null){
                                putSerializable("playlist", Playlist(id!!, name!!, cover, description))
                                putString("title", name)
                            }
                        }
                    }
                    nav().navigate(R.id.action_chartsFragment_to_my_music_fragment_dest, bundle)
                }
            }

            (chartListRcv.layoutManager as GridLayoutManager).spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    val size = viewModel.neteaseTopList.value?.data?.get(position)?.list?.size ?: 0
                    return if(size > 0){
                        3
                    }else {
                        1
                    }
                }
            }

        }
    }
}