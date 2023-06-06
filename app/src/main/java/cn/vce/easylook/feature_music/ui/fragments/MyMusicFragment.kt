package cn.vce.easylook.feature_music.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import cn.vce.easylook.MainActivity
import cn.vce.easylook.R
import cn.vce.easylook.databinding.FragmentMyMusicBinding
import cn.vce.easylook.databinding.ListItemBinding
import cn.vce.easylook.feature_music.adapters.SongAdapter
import cn.vce.easylook.feature_music.data.entities.Song
import cn.vce.easylook.feature_music.exoplayer.FirebaseMusicSource
import cn.vce.easylook.feature_music.other.Status
import cn.vce.easylook.feature_music.ui.viewmodels.MainViewModel
import cn.vce.easylook.base.BaseFragment
import com.bumptech.glide.RequestManager
import com.drake.brv.BindingAdapter
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MyMusicFragment : BaseFragment() {

    private val mainViewModel: MainViewModel by viewModels()

    @Inject
    lateinit var songAdapter: SongAdapter

    lateinit var binding: FragmentMyMusicBinding

    @Inject
    lateinit var firebaseMusicSource: FirebaseMusicSource

    @Inject
    lateinit var glide: RequestManager
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyMusicBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        subscribeToObservers()

    }

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
                        tvSecondary.text = song.subtitle
                        glide.load(song.imageUrl).into(ivItemImage)
                    }
                }
                onClick(R.id.songItemLayout) {
                    val song = getModel<Song>()
                    mainViewModel.playOrToggleSong(song)
                }
            }
        }
    }

    private fun subscribeToObservers() {
        mainViewModel.mediaItems.observe(viewLifecycleOwner) { result ->
            when(result.status) {
                Status.SUCCESS -> {
                    binding.apply {
                        allSongsProgressBar.isVisible = false
                        result.data?.let { songs ->
                            (activity as MainActivity).swipeSongAdapter.songs = songs
                            when (val adapter = rvAllSongs.adapter) {
                                is BindingAdapter -> adapter.models = songs
                            }
                        }
                    }
                }
                Status.ERROR -> Unit
                Status.LOADING -> binding.allSongsProgressBar.isVisible = true
            }
        }
    }
}
















