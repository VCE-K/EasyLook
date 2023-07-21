package cn.vce.easylook.feature_music.presentation.home_music.charts

import android.os.Bundle
import android.widget.FrameLayout
import androidx.core.widget.doAfterTextChanged
import cn.vce.easylook.MainEvent
import cn.vce.easylook.R
import cn.vce.easylook.base.BaseVmFragment
import cn.vce.easylook.databinding.FragmentChartsBinding
import cn.vce.easylook.feature_music.models.MusicInfo
import cn.vce.easylook.feature_music.models.TopListBean
import cn.vce.easylook.feature_music.presentation.bottom_music_controll.MusicControlBottomFragment
import cn.vce.easylook.feature_music.presentation.bottom_music_dialog.BottomDialogFragment
import cn.vce.easylook.feature_music.presentation.home_music.music_local.MusicLocalEvent
import com.bumptech.glide.RequestManager
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.drake.net.Get
import com.drake.net.component.Progress
import com.drake.net.interfaces.ProgressListener
import com.drake.net.utils.scope
import com.drake.net.utils.scopeNetLife
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class ChartsFragment : BaseVmFragment<FragmentChartsBinding>() {

    private lateinit var viewModel: ChartsViewModel

    @Inject
    lateinit var glide: RequestManager


    override fun init(savedInstanceState: Bundle?) {
        setupRecyclerView()
        binding.run {
            lifecycleOwner = this@ChartsFragment
            m = viewModel
            v = this@ChartsFragment // 数据请求完成绑定点击事件
            tc = ChartsEvent.TextChange
        }

        val musicControlFrag = MusicControlBottomFragment()
        val fragmentTransaction = childFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.musicControl, musicControlFrag)
        fragmentTransaction.commit()

    }

    override fun initFragmentViewModel() {
        viewModel = getFragmentViewModel()
    }

    override fun initActivityViewModel() {
        mainVM = getActivityViewModel()
    }

    override fun getLayoutId(): Int? = R.layout.fragment_charts

    private fun setupRecyclerView() = binding.apply {

        chartListRv.linear().setup {
            addType<TopListBean>(R.layout.item_charts)
            onClick(R.id.item) {
                if (!toggleMode) {
                    toggle()
                }
                setChecked(layoutPosition, true)
            }
            onChecked { position, isChecked, isAllChecked ->
                val model = getModel<TopListBean>(position)
                model.checked = isChecked
                model.id?.let {
                    viewModel.onEvent(
                        ChartsEvent.SwitchCharts(
                            it, position
                        )
                    )
                }
                model.notifyChange() // 通知UI跟随数据变化
            }
        }


        val adapter = chartListRv.bindingAdapter
        adapter.singleMode = true
        // 单选模式不应该支持全选
        chartListRv.isEnabled = !adapter.singleMode


        page.onRefresh {
            scope {
                withContext(Dispatchers.Main) {
                    viewModel.onEvent(ChartsEvent.FetchData)
                }
            }
        }.showLoading()

        //歌单子列表，展示歌曲集合
        songListRv.apply {
            linear().setup {
                addType<MusicInfo>(R.layout.list_music_item)
                onClick(R.id.songItemLayout) {
                    val song = getModel<MusicInfo>()
                    viewModel.songs.value?.let {
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

}