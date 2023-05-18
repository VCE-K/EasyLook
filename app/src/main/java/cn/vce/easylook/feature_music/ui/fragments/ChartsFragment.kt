package cn.vce.easylook.feature_music.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewbinding.ViewBinding
import cn.vce.easylook.R
import cn.vce.easylook.databinding.FragmentChartsBinding
import cn.vce.easylook.databinding.FragmentHomeBinding
import cn.vce.easylook.databinding.ItemChartsBinding
import cn.vce.easylook.databinding.ItemChartsLargeBinding
import cn.vce.easylook.feature_music.ui.viewmodels.ChartsViewModl
import cn.vce.easylook.feature_music.ui.viewmodels.MainViewModel
import cn.vce.easylook.utils.LogE
import com.bumptech.glide.RequestManager
import com.cyl.musicapi.bean.TopListBean
import com.cyl.musicapi.playlist.MusicInfo
import com.drake.brv.BindingAdapter
import com.drake.brv.utils.grid
import com.drake.brv.utils.linear
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.min

@AndroidEntryPoint
class ChartsFragment : Fragment() {

    lateinit var binding: FragmentChartsBinding

    private val mainViewModel: MainViewModel by viewModels()

    private val viewModel: ChartsViewModl by viewModels()

    @Inject
    lateinit var glide: RequestManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    // 获取当前Fragment的NavController对象
    val navController by lazy { findNavController() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChartsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

    }


    private fun setupRecyclerView() = binding.apply {
        viewModel.apply {
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
                        // 跳转到其他Fragment
                        val bundle = Bundle().apply {
                            getModel<TopListBean>().id?.let { it -> putString("id", it) }
                        }
                        navController.navigate(R.id.action_chartsFragment_to_my_music_fragment_dest, bundle)
                    }
                }

                (chartListRcv.layoutManager as GridLayoutManager).spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        val size = neteaseTopList.value?.get(position)?.list?.size ?: 0
                        return if(size > 0){
                            3
                        }else {
                            1
                        }
                    }
                }

            }
            //列表数据补充
            neteaseTopList.observe(viewLifecycleOwner) {
                LogE(it.toString())
                when (val adapter = chartListRcv.adapter) {
                    is BindingAdapter -> adapter.models = it
                }
            }


        }
    }
}