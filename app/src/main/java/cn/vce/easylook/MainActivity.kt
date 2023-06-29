package cn.vce.easylook

import android.app.Activity
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.transition.Explode
import android.view.View
import android.view.Window
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import cn.vce.easylook.base.BaseVmActivity
import cn.vce.easylook.databinding.ActivityMainBinding
import cn.vce.easylook.feature_music.adapters.SwipeSongAdapter
import cn.vce.easylook.feature_music.other.Status
import cn.vce.easylook.feature_music.presentation.bottom_music_controll.MusicControlBottomFragment
import cn.vce.easylook.feature_video.presentation.video_detail.VideoDetailFragment
import cn.vce.easylook.utils.toast
import com.bumptech.glide.RequestManager
import com.drake.statusbar.immersive
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : BaseVmActivity<ActivityMainBinding>() {


    private lateinit var mainViewModel: MainViewModel

    private lateinit var drawerLayout: DrawerLayout

    private var musicControlFrag: MusicControlBottomFragment? = null

    @Inject
    lateinit var swipeSongAdapter: SwipeSongAdapter

    @Inject
    lateinit var glide: RequestManager
    
    private lateinit var appBarConfiguration: AppBarConfiguration


    private var backPressTime = 0L

    private var isBackFlage = false
    override fun initViewModel() {
        mainViewModel = getActivityViewModel()
    }
    override fun init(savedInstanceState: Bundle?) {
        //在这里调用请求权限什么的
        initView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // 设置一个exit transition
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
            window.enterTransition = Explode()
            window.exitTransition = Explode()
        }
        super.onCreate(savedInstanceState)
    }

    override fun observe() {
        subscribeToObservers()
    }

    override fun initView() {
        immersive(binding.toolbar, true)
        setupNavigationDrawer()
        setSupportActionBar(binding.toolbar)
        val navController: NavController = findNavController(R.id.nav)
        appBarConfiguration = AppBarConfiguration(binding.navView.menu, binding.drawerLayout)

        /*appBarConfiguration =
            AppBarConfiguration.Builder(
                R.id.music_fragment_dest,
                R.id.video_fragment_dest,
                //R.id.novel_fragment_dest,
                R.id.ai_fragment_dest
            ).setDrawerLayout(drawerLayout)
                .build()*/

        setupActionBarWithNavController(navController, appBarConfiguration)

        //动画
        /*binding.apply {
            val animationView = animationView
            animationView.addAnimatorListener(object : AnimatorListenerAdapter(){
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    animationView.visibility = View.GONE
                }
            })
            animationView.speed = 3f
            animationView.playAnimation()//播放
        }*/
        binding.toolbar.setupWithNavController(
            navController,
            appBarConfiguration
        )


        binding.navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.toolbar.subtitle =
                (destination as FragmentNavigator.Destination).className.substringAfterLast('.')
            when(destination.id) {
                R.id.video_fragment_dest -> {
                    isBackFlage = true
                    hideBottomBar()
                }
                R.id.music_fragment_dest -> {
                    isBackFlage = true
                    showBottomBar()
                }
                R.id.songFragment -> {
                    isBackFlage = true
                    hideBottomBar()
                }
                else -> {
                    isBackFlage = false
                    hideBottomBar()
                }
            }
        }
        musicControlFrag = supportFragmentManager
            .findFragmentById(R.id.musicControl) as MusicControlBottomFragment
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav) as NavHostFragment
            when(val fragment = navHostFragment.childFragmentManager.fragments[0]){
                is VideoDetailFragment -> {
                    if (fragment.onBackPressed()) return
                }
                else -> {
                    super.onBackPressed()
                    return
                }
            }
            processBackPressed()
        }
    }


    private fun processBackPressed() {
        val now = System.currentTimeMillis()
        if (now - backPressTime > 2000 && isBackFlage) {
            toast(String.format(getString(R.string.press_again_to_exit), getString(R.string.app_name)))
            backPressTime = now
        } else{
            super.onBackPressed()
        }
    }

    private fun hideBottomBar() {
        musicControlFrag?.let {
            val fragmentManager = supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.hide(it)
                .commit()
        }
    }

    private fun showBottomBar() {
        musicControlFrag?.let {
            val fragmentManager = supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.show(it)
                .commit()
        }
    }

    override fun getLayoutId(): Int? = R.layout.activity_main
    /**
     *onSupportNavigateUp是一个用于处理导航回上一页的事件的回调函数。它通常用于在支持ActionBar的Activity中使用，用于处理用户点击返回按钮时的导航行为。
     */
    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav).navigateUp(appBarConfiguration) ||
                super.onSupportNavigateUp()
    }

    private fun setupNavigationDrawer() {
        drawerLayout = (findViewById<DrawerLayout>(R.id.drawer_layout))
            .apply {
                setStatusBarBackground(R.color.colorPrimaryDark)
            }
    }


    private fun subscribeToObservers() {
        binding.apply {
            mainViewModel.isConnected.observe(this@MainActivity) {
                it?.getContentIfNotHandled()?.let { result ->
                    when (result.status) {
                        Status.ERROR -> Snackbar.make(
                            drawerLayout,
                            result.message ?: "An unknown error occured",
                            Snackbar.LENGTH_LONG
                        ).show()
                        Status.SUCCESS -> {
                            mainViewModel.onEvent(MainEvent.InitingPlayMode)
                        }
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

