package cn.vce.easylook.feature_music.presentation.bottom_music_controll

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.navigation.fragment.NavHostFragment
import androidx.viewpager2.widget.ViewPager2
import cn.vce.easylook.R
import cn.vce.easylook.base.BaseVmFragment
import cn.vce.easylook.databinding.FragmentMusicControlBottomBinding
import cn.vce.easylook.feature_music.adapters.SwipeSongAdapter
import cn.vce.easylook.feature_music.exoplayer.isPlaying
import cn.vce.easylook.feature_music.models.MusicInfo
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MusicControlBottomFragment() : BaseVmFragment<FragmentMusicControlBottomBinding>() {

/*    companion object{
        private var instance: MusicControlBottomFragment? = null
        @JvmStatic
        fun getInstance(): MusicControlBottomFragment{
            return instance ?: synchronized(this){
                    instance?: MusicControlBottomFragment().also { instance = it }
                }
            }
        }*/


    @Inject
    lateinit var swipeSongAdapter: SwipeSongAdapter

    @Inject
    lateinit var glide: RequestManager

    lateinit var curPlayingMusic: MusicInfo

    override fun init(savedInstanceState: Bundle?) {
        initView()
        binding.m = mainVM
        binding.adapter = swipeSongAdapter
    }

    override fun initActivityViewModel() {
        mainVM = getActivityViewModel()
    }

    override fun initView() {
        binding.apply {
            vpSong.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    if(mainVM.playbackState.value?.isPlaying == true) {
                        mainVM.playOrToggleSong(swipeSongAdapter.songs[position])
                    }
                    curPlayingMusic = swipeSongAdapter.songs[position]
                }
            })
            ivPlayPause.setOnClickListener(this@MusicControlBottomFragment)
            ivCurSongImage.setOnClickListener(this@MusicControlBottomFragment)
        }
    }

    override fun observe() {
         mainVM.curPlayingMusic.observe(viewLifecycleOwner){
             val data = it
             if (data == null){
                 binding.root.visibility = View.GONE
             }else{
                 binding.root.visibility = View.VISIBLE
             }
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

    override fun onClick() {
        swipeSongAdapter.setItemClickListener {
            mainVM.curPlayingMusic.value?.let {
                val bundle = Bundle().apply {
                    putSerializable("musicInfo", it)
                    putString("title", it.name)
                }
                nav().navigate(
                    R.id.globalActionToSongFragment,
                    bundle
                )
            }
        }
    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.ivPlayPause -> {
                curPlayingMusic?.let {
                    mainVM.playOrToggleSong(it, true)
                }
            }
            R.id.ivCurSongImage -> {
                mainVM.curPlayingMusic.value?.let {
                    val bundle = Bundle().apply {
                        putSerializable("musicInfo", it)
                        putString("title", it.name)
                    }
                    nav().navigate(
                        R.id.globalActionToSongFragment,
                        bundle
                    )
                }
            }
        }
    }


    override fun getLayoutId(): Int? = R.layout.fragment_music_control_bottom

}