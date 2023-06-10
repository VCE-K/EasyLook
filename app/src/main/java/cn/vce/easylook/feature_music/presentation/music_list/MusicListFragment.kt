package cn.vce.easylook.feature_music.presentation.music_list

import android.os.Bundle
import androidx.core.view.isVisible
import cn.vce.easylook.R
import cn.vce.easylook.base.BaseVmFragment
import cn.vce.easylook.databinding.FragmentMusicListBinding
import cn.vce.easylook.databinding.ListItemBinding
import cn.vce.easylook.feature_music.adapters.SongAdapter
import cn.vce.easylook.feature_music.domain.entities.Song
import cn.vce.easylook.feature_music.other.Status
import cn.vce.easylook.feature_music.presentation.MainViewModel
import com.bumptech.glide.RequestManager
import com.drake.brv.BindingAdapter
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MusicListFragment : BaseVmFragment<FragmentMusicListBinding>() {


    private lateinit var viewModel: MusicListViewModel

    private lateinit var mainViewModel: MainViewModel

    @Inject
    lateinit var songAdapter: SongAdapter


    @Inject
    lateinit var glide: RequestManager

    override fun init(savedInstanceState: Bundle?) {
        initView()
    }

    override fun initView() {
        setupRecyclerView()
    }


    override fun observe() {
        subscribeToObservers()
    }

    override fun initFragmentViewModel() {
        mainViewModel = getActivityViewModel()
        viewModel = getFragmentViewModel()
    }

    override fun getLayoutId(): Int? = R.layout.fragment_music_list

    private fun setupRecyclerView() = binding.apply {
        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = false
        }
        rvAllSongs.apply {
            linear().setup {
                addType<Song>(R.layout.list_item)
                onBind {
                    val bind = getBinding<ListItemBinding>()
                    val song = getModel<Song>()
                    bind.apply {
                        tvPrimary.text = song.title
                        tvSecondary.text = song.artistNames + " - " +song.subtitle
                        glide.load(song.imageUrl).into(ivItemImage)
                    }
                }
                onClick(R.id.songItemLayout) {
                    val song = getModel<Song>()
                    viewModel.onEvent(MusicListEvent.PlayList {
                        // 在主线程中执行代码
                        if (it) {
                            mainViewModel.subscribe()//获取新的数据
                        }
                        mainViewModel.playOrToggleSong(song)
                    })
                }
            }
        }
    }

    private fun subscribeToObservers() {
        viewModel.mediaItems.observe(viewLifecycleOwner) { result ->
            when(result.status) {
                Status.SUCCESS -> {
                    binding.apply {
                        allProgressBar.isVisible = false
                        result.data?.let { songs ->
                            when (val adapter = rvAllSongs.adapter) {
                                is BindingAdapter -> adapter.models = songs
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
















