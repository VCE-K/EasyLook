package cn.vce.easylook.feature_music.presentation.playlist_detail

import android.os.Bundle
import androidx.core.view.isVisible
import cn.vce.easylook.R
import cn.vce.easylook.base.BaseVmFragment
import cn.vce.easylook.databinding.FragmentPlaylistDetailBinding
import cn.vce.easylook.databinding.ListItemBinding
import cn.vce.easylook.feature_music.models.MusicInfo
import cn.vce.easylook.feature_music.other.Status
import cn.vce.easylook.MainEvent
import cn.vce.easylook.MainViewModel
import cn.vce.easylook.feature_music.presentation.bottom_dialog.BottomDialogFragment
import cn.vce.easylook.utils.ConvertUtils
import com.bumptech.glide.RequestManager
import com.drake.brv.BindingAdapter
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class PlaylistDetailFragment : BaseVmFragment<FragmentPlaylistDetailBinding>() {


    private lateinit var viewModel: PlaylistDetailVM

    private lateinit var mainVM: MainViewModel

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
        mainVM = getActivityViewModel()
        viewModel = getFragmentViewModel()
    }

    override fun getLayoutId(): Int? = R.layout.fragment_playlist_detail

    private fun setupRecyclerView() = binding.apply {
        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = false
        }
        rvAllSongs.apply {
            linear().setup {
                addType<MusicInfo>(R.layout.list_item)
                onBind {
                    val bind = getBinding<ListItemBinding>()
                    val musicInfo = getModel<MusicInfo>()
                    bind.apply {
                        tvPrimary.text = musicInfo.name
                        tvSecondary.text = musicInfo.artists?.let {
                            ConvertUtils.getArtistAndAlbum(musicInfo.artists, musicInfo.album?.name)
                        }
                        glide.load(musicInfo.album?.cover).into(ivItemImage)
                    }
                }
                onClick(R.id.songItemLayout) {
                    val song = getModel<MusicInfo>()
                    viewModel.mediaItems.value?.data?.let {
                        mainVM.onEvent(MainEvent.ClickPlay(it, song))
                    }
                }
                onClick(R.id.options){
                    val musicInfo = getModel<MusicInfo>()
                    BottomDialogFragment().show(mActivity, musicInfo)
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
















