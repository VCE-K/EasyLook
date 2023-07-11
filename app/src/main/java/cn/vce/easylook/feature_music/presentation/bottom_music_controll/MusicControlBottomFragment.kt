package cn.vce.easylook.feature_music.presentation.bottom_music_controll

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import cn.vce.easylook.R
import cn.vce.easylook.base.BaseVmFragment
import cn.vce.easylook.databinding.FragmentMusicControlBottomBinding
import cn.vce.easylook.feature_music.adapters.SwipeSongAdapter
import cn.vce.easylook.feature_music.exoplayer.isPlaying
import cn.vce.easylook.feature_music.exoplayer.toMusicInfo
import cn.vce.easylook.feature_music.models.MusicInfo
import cn.vce.easylook.feature_music.other.Status
import cn.vce.easylook.utils.LogE
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MusicControlBottomFragment : BaseVmFragment<FragmentMusicControlBottomBinding>() {


    @Inject
    lateinit var swipeSongAdapter: SwipeSongAdapter

    @Inject
    lateinit var glide: RequestManager


    private var curPlayingMusic: MusicInfo? = null

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
                    }else {
                        curPlayingMusic = swipeSongAdapter.songs[position]
                    }
                }
            })

            ivPlayPause.setOnClickListener {
                curPlayingMusic?.let {
                    mainVM.playOrToggleSong(it, true)
                }
            }

            ivCurSongImage.setOnClickListener {
                curPlayingMusic?.let {
                    val bundle = Bundle().apply {
                        putSerializable("musicInfo", it)
                        putString("title", it.name)
                    }
                    navController.navigate(
                        R.id.globalActionToSongFragment,
                        bundle
                    )
                }
            }

            swipeSongAdapter.setItemClickListener {
                curPlayingMusic?.let {
                    val bundle = Bundle().apply {
                        putSerializable("musicInfo", it)
                        putString("title", it.name)
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
                                    curPlayingMusic?.let {
                                        glide.load(it.album?.cover)
                                            .into(ivCurSongImage)
                                        switchViewPagerToCurrentSong(curPlayingMusic ?: return@observe)
                                    }
                                }
                            }
                        }
                        Status.ERROR -> Unit
                        Status.LOADING -> Unit
                    }
                }
            }

            mainVM.curPlayingSong.observe(viewLifecycleOwner) {
                if (it == null) {
                    curPlayingMusic = it
                    showNowPlaying(null)
                    return@observe
                }
                curPlayingMusic = it.toMusicInfo()
                showNowPlaying(curPlayingMusic)
                glide.load(curPlayingMusic?.album?.cover).into(ivCurSongImage)
                switchViewPagerToCurrentSong(curPlayingMusic ?: return@observe)
            }

            mainVM.playbackState.observe(viewLifecycleOwner) {
                ivPlayPause.setImageResource(
                    if (it?.isPlaying == true) R.drawable.ic_pause else R.drawable.ic_play
                )
            }
        }
    }
    fun showNowPlaying(music: MusicInfo?) {
        if (music != null) {
            binding.root.visibility = View.VISIBLE
        } else {
            binding.root.visibility = View.GONE
        }

    }

/*    private fun hideBottomBar() {
        val fragmentView: View? = musicControlFrag?.view
        if (fragmentView != null) {
            fragmentView.visibility = View.INVISIBLE
        }
        *//*musicControlFrag?.let {
            val fragmentManager = supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.hide(it)
                .commit()
        }*//*
    }

    private fun showBottomBar() {
        val fragmentView: View? = musicControlFrag?.view
        if (fragmentView != null) {
            fragmentView.visibility = View.VISIBLE
        }
        *//*musicControlFrag?.let {
            val fragmentManager = supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.show(it)
                .commit()
        }*//*
    }*/

    override fun initViewModel() {
        mainVM = getActivityViewModel()
    }

    override fun getLayoutId(): Int? = R.layout.fragment_music_control_bottom


    private fun switchViewPagerToCurrentSong(musicInfo: MusicInfo) {
        binding.apply {
            val newItemIndex = swipeSongAdapter.songs.indexOfFirst {
                musicInfo.id == it.id
            }
            if (newItemIndex != -1) {
                vpSong.setCurrentItem(newItemIndex, false)
                LogE("vpSong.currentItem::${newItemIndex} - ${vpSong.currentItem}")
                curPlayingMusic = musicInfo
            }
        }
    }
}