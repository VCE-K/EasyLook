package cn.vce.easylook.feature_music.presentation.home_music.music_local

import android.os.Bundle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import cn.vce.easylook.MainEvent
import cn.vce.easylook.MainViewModel
import cn.vce.easylook.R
import cn.vce.easylook.base.BaseVmFragment
import cn.vce.easylook.databinding.FragmentMusicLocalBinding
import cn.vce.easylook.feature_music.models.MusicInfo
import cn.vce.easylook.feature_music.models.PlaylistWithMusicInfo
import cn.vce.easylook.feature_music.other.Status
import cn.vce.easylook.feature_music.presentation.bottom_dialog.BottomDialogFragment
import cn.vce.easylook.utils.toast
import com.drake.brv.BindingAdapter
import com.drake.brv.item.ItemExpand
import com.drake.brv.listener.DefaultItemTouchCallback
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MusicLocalFragment: BaseVmFragment<FragmentMusicLocalBinding>() {

    private lateinit var mainVM: MainViewModel
    private lateinit var viewModel: MusicLocalVM


    private val total = 1
    override fun initFragmentViewModel() {
        viewModel = getFragmentViewModel()
    }

    override fun initActivityViewModel() {
        mainVM = getActivityViewModel()
    }

    override fun init(savedInstanceState: Bundle?) {
        initView()
    }

    override fun observe() {
        super.observe()
        binding.apply {
            /*viewModel.playlistWithMusicInfos.observe(viewLifecycleOwner) { result ->
                when(result.status) {
                    Status.SUCCESS -> {
                        binding.apply {
                            result.data?.let {
                                when (val adapter = rv.adapter) {
                                    is BindingAdapter -> {
                                        adapter.models = viewModel.playlistWithMusicInfos.value?.data
                                        binding.page.showContent()
                                    }
                                }
                            }
                        }
                    }
                    Status.ERROR -> binding.page.showError()
                    Status.LOADING -> {
                        binding.page.showLoading()
                    }
                }
            }*/
        }
    }
    override fun initView() {

        binding.rv.linear().setup {
            addType<PlaylistWithMusicInfo>(R.layout.item_layout_view)
            addType<MusicInfo>(R.layout.list_music_item)
            R.id.item.onFastClick {
                when (itemViewType) {
                    R.layout.item_layout_view-> {
                        val changeCount =
                            if (getModel<ItemExpand>().itemExpand) "折叠 ${expandOrCollapse()} 条" else "展开 ${expandOrCollapse()} 条"
                        toast(changeCount)
                    }
                    R.layout.list_music_item -> {
                        val song = getModel<MusicInfo>()
                        val parentPosition = findParentPosition()
                        if (parentPosition != -1) {
                            val playlistWithMusicInfo = getModel<PlaylistWithMusicInfo>(parentPosition)
                            playlistWithMusicInfo.itemSublist?.let {
                                mainVM.onEvent(MainEvent.ClickPlay(it as List<MusicInfo>, song))
                            }
                        }
                    }
                }
            }
            R.id.options.onFastClick {
                val musicInfo = getModel<MusicInfo>()
                BottomDialogFragment().show(mActivity, musicInfo)
            }
        }

        binding.page.onRefresh {
            viewModel.onEvent(MusicLocalEvent.RefreshSearchEvent {
                //这里因为Flow和歌单本来就没有onLoadMore的缘故，一直第一页
                index = 1
                addData(it)
            })
        }.onLoadMore {
            addData(null){
                index < total
            }
        }.autoRefresh()
    }

    override fun getLayoutId(): Int?  = R.layout.fragment_music_local
}
