package cn.vce.easylook.feature_music.presentation.home_music.music_local

import android.os.Bundle
import androidx.core.widget.doAfterTextChanged
import cn.vce.easylook.MainEvent
import cn.vce.easylook.MainViewModel
import cn.vce.easylook.R
import cn.vce.easylook.base.BaseVmFragment
import cn.vce.easylook.databinding.FragmentMusicLocalBinding
import cn.vce.easylook.feature_music.models.MusicInfo
import cn.vce.easylook.feature_music.models.PlaylistInfo
import cn.vce.easylook.feature_music.models.PlaylistWithMusicInfo
import cn.vce.easylook.feature_music.models.TopListBean
import cn.vce.easylook.feature_music.presentation.bottom_music_controll.MusicControlBottomFragment
import cn.vce.easylook.feature_music.presentation.bottom_music_dialog.BottomDialogFragment
import cn.vce.easylook.feature_music.presentation.home_music.charts.ChartsEvent
import cn.vce.easylook.utils.toast
import com.drake.brv.item.ItemExpand
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.linear
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.drake.net.utils.scope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class MusicLocalFragment: BaseVmFragment<FragmentMusicLocalBinding>() {

    
    private lateinit var viewModel: MusicLocalVM

    override fun initFragmentViewModel() {
        mainVM = getActivityViewModel()
        viewModel = getFragmentViewModel()
    }

    override fun init(savedInstanceState: Bundle?) {
        initView()
        binding.run {
            //lifecycleOwner = this@MusicLocalFragment
            m = viewModel
            v = this@MusicLocalFragment // 数据请求完成绑定点击事件
            tc = MusicLocalEvent.TextChange
        }

        val musicControlFrag = MusicControlBottomFragment()
        childFragmentManager.beginTransaction()
            .replace(R.id.musicControl, musicControlFrag)
            .commit()
    }

    override fun initView() {
        setupRecyclerView()
    }

    private fun setupRecyclerView() = binding.apply {
        playlistRv.linear().setup {
            addType<PlaylistInfo>(R.layout.item_playlist)
            onClick(R.id.item) {
                if (!toggleMode) {
                    toggle()
                }
                setChecked(layoutPosition, true)
            }
            onChecked { position, isChecked, isAllChecked ->
                val model = getModel<PlaylistInfo>(position)
                model.checked = isChecked
                model.id?.let {
                    viewModel.onEvent(
                        MusicLocalEvent.SwitchPlaylist(
                            it, position
                        )
                    )
                }
                model.notifyChange() // 通知UI跟随数据变化
            }
        }


        val adapter = playlistRv.bindingAdapter
        adapter.singleMode = true
        // 单选模式不应该支持全选
        playlistRv.isEnabled = !adapter.singleMode

        page.onRefresh {
            scope {
                withContext(Dispatchers.Main) {
                    viewModel.onEvent(MusicLocalEvent.FetchData)
                }
            }
        }.autoRefresh()

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
    override fun getLayoutId(): Int?  = R.layout.fragment_music_local
}
