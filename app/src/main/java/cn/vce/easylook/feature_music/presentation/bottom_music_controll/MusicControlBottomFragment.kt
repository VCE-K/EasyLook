package cn.vce.easylook.feature_music.presentation.bottom_music_controll

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import cn.vce.easylook.R
import cn.vce.easylook.base.BaseVmFragment
import cn.vce.easylook.databinding.FragmentMusicControlBottomBinding
import cn.vce.easylook.feature_music.adapters.SwipeSongAdapter
import cn.vce.easylook.feature_music.domain.entities.Song
import cn.vce.easylook.feature_music.exoplayer.isPlaying
import cn.vce.easylook.feature_music.exoplayer.toSong
import cn.vce.easylook.feature_music.other.Status
import cn.vce.easylook.feature_music.presentation.MainViewModel
import cn.vce.easylook.utils.LogE
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MusicControlBottomFragment : BaseVmFragment<FragmentMusicControlBottomBinding>() {

    private lateinit var mainVM: MainViewModel

    @Inject
    lateinit var swipeSongAdapter: SwipeSongAdapter

    @Inject
    lateinit var glide: RequestManager

    private val playingSong
            get() = mainVM.curPlayingSong.value?.toSong()

    override fun init(savedInstanceState: Bundle?) {
        initView()
    }

    override fun initView() {
        binding.apply {
            vpSong.adapter = swipeSongAdapter
            vpSong.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    if(mainVM.playbackState.value?.isPlaying == true) {
                        mainVM.playOrToggleSong(swipeSongAdapter.songs[position])
                    }
                }
            })

            ivPlayPause.setOnClickListener {
                mainVM.curPlayingSong?.value.let {
                    it?.toSong()?.let { it1 ->
                        mainVM.playOrToggleSong(it1, true)
                    }
                }
            }

            ivCurSongImage.setOnClickListener {

                playingSong?.let { song ->
                    val bundle = Bundle().apply {
                        putSerializable("song", song)
                        putString("title", song.title)
                    }
                    navController.navigate(
                        R.id.globalActionToSongFragment,
                        bundle
                    )
                }
            }

            swipeSongAdapter.setItemClickListener {
                playingSong?.let { song ->
                    val bundle = Bundle().apply {
                        putSerializable("song", song)
                        putString("title", song.title)
                    }
                    navController.navigate(
                        R.id.globalActionToSongFragment,
                        bundle
                    )
                }
            }
        }
    }

    override fun observe() {
        //根据播放状态显示按钮
        binding.apply {
            mainVM.mediaItems.observe(viewLifecycleOwner) {
                it?.let { result ->
                    when (result.status) {
                        Status.SUCCESS -> {
                            result.data?.let { songs ->
                                swipeSongAdapter.songs = songs
                                if (songs.isNotEmpty()) {
                                    glide.load((playingSong ?: songs[0]).imageUrl)
                                        .into(ivCurSongImage)
                                }
                                switchViewPagerToCurrentSong(playingSong ?: return@observe)
                            }
                        }
                        Status.ERROR -> Unit
                        Status.LOADING -> Unit
                    }
                }
            }

            mainVM.curPlayingSong.observe(viewLifecycleOwner) {
                if (it == null) return@observe
                glide.load(playingSong?.imageUrl).into(ivCurSongImage)
                switchViewPagerToCurrentSong(playingSong ?: return@observe)
            }

            mainVM.playbackState.observe(viewLifecycleOwner) {
                ivPlayPause.setImageResource(
                    if (it?.isPlaying == true) R.drawable.ic_pause else R.drawable.ic_play
                )
            }
        }
    }

    override fun initViewModel() {
        mainVM = getActivityViewModel()
    }

    override fun getLayoutId(): Int? = R.layout.fragment_music_control_bottom


    private fun switchViewPagerToCurrentSong(song: Song) {
        binding.apply {
            val newItemIndex = swipeSongAdapter.songs.indexOfFirst {
                song.mediaId == it.mediaId
            }
            if (newItemIndex != -1) {
                vpSong.currentItem = newItemIndex
                LogE("vpSong.currentItem::${vpSong.currentItem}")
            }
        }
    }
}