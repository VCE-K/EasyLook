package cn.vce.easylook.feature_music.presentation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager2.widget.ViewPager2
import cn.vce.easylook.R
import cn.vce.easylook.base.BaseVmActivity
import cn.vce.easylook.databinding.ActivityMainBinding
import cn.vce.easylook.feature_music.adapters.SwipeSongAdapter
import cn.vce.easylook.feature_music.domain.entities.Playlist
import cn.vce.easylook.feature_music.domain.entities.Song
import cn.vce.easylook.feature_music.exoplayer.isPlaying
import cn.vce.easylook.feature_music.exoplayer.toSong
import cn.vce.easylook.feature_music.other.Status
import cn.vce.easylook.utils.LogE
import com.bumptech.glide.RequestManager
import com.cyl.musicapi.bean.TopListBean
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseVmActivity<ActivityMainBinding>() {


    private lateinit var mainViewModel: MainViewModel
    
    private lateinit var drawerLayout: DrawerLayout

    @Inject
    lateinit var swipeSongAdapter: SwipeSongAdapter

    @Inject
    lateinit var glide: RequestManager


    private var curPlayingSong: Song? = null

    private var playbackState: PlaybackStateCompat? = null
    
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun initViewModel() {
        mainViewModel = getActivityViewModel()
    }
    override fun init(savedInstanceState: Bundle?) {
        //在这里调用请求权限什么的
        initView()
    }

    override fun observe() {
        super.observe()
        subscribeToObservers()
    }

    override fun initView() {
        super.initView()
        setupNavigationDrawer()
        setSupportActionBar(binding.toolbar)
        val navController: NavController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration =
            AppBarConfiguration.Builder(
                R.id.music_fragment_dest,
                R.id.video_fragment_dest,
                R.id.novel_fragment_dest,
                R.id.ai_fragment_dest
            )
                .setDrawerLayout(drawerLayout)
                .build()
        setupActionBarWithNavController(navController, appBarConfiguration)

        //动画
        binding.apply {
            val animationView = animationView
            animationView.addAnimatorListener(object : AnimatorListenerAdapter(){
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    animationView.visibility = View.GONE
                }
            })
            animationView.speed = 1.7f
            animationView.playAnimation()//播放
        }

        binding.navView.setupWithNavController(navController)
        binding.musicPlayer.visibility = View.GONE

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id) {
                R.id.video_fragment_dest -> hideBottomBar()
                R.id.music_fragment_dest -> showBottomBar()
                R.id.songFragment -> hideBottomBar()
                else -> hideBottomBar()
            }
        }


        binding.apply {
            vpSong.adapter = swipeSongAdapter
            vpSong.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    if(playbackState?.isPlaying == true) {
                        mainViewModel.playOrToggleSong(swipeSongAdapter.songs[position])
                    } else {
                        curPlayingSong = swipeSongAdapter.songs[position]
                    }
                }
            })

            ivPlayPause.setOnClickListener {
                curPlayingSong?.let {
                    mainViewModel.playOrToggleSong(it, true)
                }
            }

            swipeSongAdapter.setItemClickListener {
                val bundle = Bundle().apply {
                    swipeSongAdapter.songs[vpSong.currentItem]?.apply {
                        putSerializable("song", it)
                        putString("title", it.title)
                    }
                }
                navController.navigate(
                    R.id.globalActionToSongFragment,
                    bundle
                )
            }
        }
    }

    //其实什么用都没有
    override fun getLayoutId(): Int? = R.layout.activity_main


    override fun onBackPressed() {
        //获取hostFragment
        val mMainNavFragment: Fragment? =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        //获取当前所在的fragment
        val fragment =
            mMainNavFragment?.childFragmentManager?.primaryNavigationFragment
        super.onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp(appBarConfiguration) ||
                super.onSupportNavigateUp()
    }

    private fun setupNavigationDrawer() {
        drawerLayout = (findViewById<DrawerLayout>(R.id.drawer_layout))
            .apply {
                setStatusBarBackground(R.color.colorPrimaryDark)
            }
    }

    private fun hideBottomBar() {
        binding.apply {
            musicPlayer.isVisible = false
        }
    }

    private fun showBottomBar() {
        binding.apply {
            musicPlayer.isVisible = true
        }
    }

    private fun switchViewPagerToCurrentSong(song: Song) {
        binding.apply {
            val newItemIndex = swipeSongAdapter.songs.indexOfFirst {
                song.mediaId == it.mediaId
            }
            if (newItemIndex != -1) {
                vpSong.currentItem = newItemIndex
                LogE("vpSong.currentItem::${vpSong.currentItem}")
                curPlayingSong = song
            }
        }
    }

    private fun subscribeToObservers() {
        binding.apply {
            mainViewModel.mediaItems.observe(this@MainActivity) {
                it?.let { result ->
                    when (result.status) {
                        Status.SUCCESS -> {
                            result.data?.let { songs ->
                                swipeSongAdapter.songs = songs
                                if (songs.isNotEmpty()) {
                                    glide.load((curPlayingSong ?: songs[0]).imageUrl)
                                        .into(ivCurSongImage)
                                }
                                switchViewPagerToCurrentSong(curPlayingSong ?: return@observe)
                            }
                        }
                        Status.ERROR -> Unit
                        Status.LOADING -> Unit
                    }
                }
            }
            mainViewModel.curPlayingSong.observe(this@MainActivity) {
                if (it == null) return@observe

                curPlayingSong = it.toSong()
                glide.load(curPlayingSong?.imageUrl).into(ivCurSongImage)
                switchViewPagerToCurrentSong(curPlayingSong ?: return@observe)
            }
            //根据播放状态显示按钮
            mainViewModel.playbackState.observe(this@MainActivity) {
                playbackState = it
                ivPlayPause.setImageResource(
                    if (playbackState?.isPlaying == true) R.drawable.ic_pause else R.drawable.ic_play
                )
            }
            mainViewModel.isConnected.observe(this@MainActivity) {
                it?.getContentIfNotHandled()?.let { result ->
                    when (result.status) {
                        Status.ERROR -> Snackbar.make(
                            drawerLayout,
                            result.message ?: "An unknown error occured",
                            Snackbar.LENGTH_LONG
                        ).show()
                        else -> Unit
                    }
                }
            }
            mainViewModel.networkError.observe(this@MainActivity) {
                it?.getContentIfNotHandled()?.let { result ->
                    when (result.status) {
                        Status.ERROR -> Snackbar.make(
                            drawerLayout,
                            result.message ?: "An unknown error occured",
                            Snackbar.LENGTH_LONG
                        ).show()
                        else -> Unit
                    }
                }
            }
        }
    }


}

