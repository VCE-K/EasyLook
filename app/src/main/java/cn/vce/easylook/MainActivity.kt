package cn.vce.easylook

import android.content.Intent
import android.os.*
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import cn.vce.easylook.base.BaseVmActivity
import cn.vce.easylook.databinding.ActivityMainBinding
import cn.vce.easylook.feature_music.exoplayer.isPlaying
import cn.vce.easylook.feature_music.exoplayer.isPrepared
import cn.vce.easylook.feature_music.other.Status
import cn.vce.easylook.feature_video.presentation.video_detail.VideoDetailFragment
import cn.vce.easylook.utils.toast
import com.bumptech.glide.RequestManager
import com.drake.statusbar.immersive
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.permissionx.guolindev.PermissionX
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import android.Manifest

@AndroidEntryPoint
class MainActivity : BaseVmActivity<ActivityMainBinding>() {


    private lateinit var mainViewModel: MainViewModel

    private lateinit var drawerLayout: DrawerLayout

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
        permissionsRequest()
    }
    private val handler = Handler(Looper.getMainLooper())


    override fun observe() {
        subscribeToObservers()
    }

    override fun initView() {
        immersive(binding.toolbar, true)
        setupNavigationDrawer()
        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav)
        appBarConfiguration = AppBarConfiguration(binding.navView.menu, binding.drawerLayout)
        binding.toolbar.setupWithNavController(
            navController,
            appBarConfiguration
        )

        navController.addOnDestinationChangedListener { _, destination, _ ->
            println("menuItem:$destination")
            binding.toolbar.subtitle =
                (destination as FragmentNavigator.Destination).className.substringAfterLast('.')
            when(destination.id) {
               R.id.video_fragment_dest -> {
                   isBackFlage = true
               }
               R.id.music_fragment_dest -> {
                   isBackFlage = true
               }
               R.id.nav_exit -> {
                   //退出
                   val intent = Intent("cn.vce.easylook.FORCE_OFFLINE")
                   sendBroadcast(intent)
                }
               else -> {
                   isBackFlage = false
               }
           }
        }

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
    }

    /**
     * 动态权限请求
     */
    private fun permissionsRequest() {
        val requestList = ArrayList<String>()
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestList.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }else{
            requestList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            requestList.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }*/
        requestList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        requestList.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        if (requestList.isNotEmpty()) {
            PermissionX.init(this)
                .permissions(requestList)
                .onExplainRequestReason { scope, deniedList ->
                    val message = "需要您同意以下权限才能正常使用"
                    //scope.showRequestReasonDialog(deniedList, message, "Allow", "Deny")
                    scope.showRequestReasonDialog(deniedList, message, "允许", "拒绝")
                }
                .request { allGranted, grantedList, deniedList ->
                    if (allGranted) {
                        Toast.makeText(this, "所有申请的权限都已通过", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "您拒绝了如下权限：$deniedList", Toast.LENGTH_SHORT).show()
                    }
                }
        }
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
                            mainViewModel.onEvent(MainEvent.InitPlayMode)
                            mainViewModel.onEvent(MainEvent.InitPlaylist(flag = false))
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

            mainViewModel.playbackState.observe(this@MainActivity) {
                if (mainViewModel.initPlaylistFlag.value == false){
                    if(it?.isPlaying == true){
                        //初始化的后半部分
                        mainViewModel.onEvent(MainEvent.InitPlaylist(flag = true))
                    }
                }
            }
        }
    }


}

