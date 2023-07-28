package cn.vce.easylook.feature_music.presentation.home_music.bli_music_list

import android.os.Bundle
import cn.vce.easylook.MainEvent
import cn.vce.easylook.R
import cn.vce.easylook.base.BaseVmFragment
import cn.vce.easylook.databinding.FragmentBliMusicListBinding
import cn.vce.easylook.feature_music.exoplayer.transMusicInfo
import cn.vce.easylook.feature_music.exoplayer.transMusicInfos
import cn.vce.easylook.feature_music.models.bli.HotSong
import cn.vce.easylook.feature_music.presentation.bottom_music_controll.MusicControlBottomFragment
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BliMusicListFragment : BaseVmFragment<FragmentBliMusicListBinding>() {

    private lateinit var viewModel: BliMusicListVM
    override fun initActivityViewModel() {
        mainVM = getActivityViewModel()
    }

    override fun initFragmentViewModel() {
        viewModel = getFragmentViewModel()
    }

    override fun init(savedInstanceState: Bundle?) {
        setupRecyclerView()

        binding.m = viewModel
        binding.v = this
        binding.tc = BliMusicListEvent.TextChange

        val musicControlFrag = MusicControlBottomFragment()
        val fragmentTransaction = childFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.musicControl, musicControlFrag)
        fragmentTransaction.commit()
    }

    override fun getLayoutId(): Int? = R.layout.fragment_bli_music_list

    private fun setupRecyclerView() = binding.apply {

        parentRv.linear().setup {
            addType<Cate>(R.layout.item_bli_cata)
            onClick(R.id.item) {
                if (!toggleMode) {
                    toggle()
                }
                setChecked(layoutPosition, true)
            }
            onChecked { position, isChecked, _ ->
                val model = getModel<Cate>(position)
                model.checked = isChecked

                if (isChecked){
                    model.id?.let {
                        viewModel.onEvent(
                            BliMusicListEvent.SwitchCharts(
                                it, position
                            )
                        )
                    }
                }
                model.notifyChange() // 通知UI跟随数据变化
            }
        }


        val adapter = parentRv.bindingAdapter
        adapter.singleMode = true
        // 单选模式不应该支持全选
        parentRv.isEnabled = !adapter.singleMode

        childRv.apply {
            linear().setup {
                addType<HotSong>(R.layout.hot_song_item)
                onClick(R.id.item) {
                    val hotSong = getModel<HotSong>()
                    viewModel.childList.value?.let {
                        val musicInfos = viewModel.childList.value?.run {
                            transMusicInfos().toList()
                        }
                        musicInfos?.run {
                            hotSong.transMusicInfo()?.run {
                                mainVM.onEvent(MainEvent.ClickPlay(musicInfos, this))
                            }
                        }
                    }
                }
            }
        }
        childPage.onRefresh {
            viewModel.onEvent(BliMusicListEvent.FetchChildData)
            finish()
        }.onLoadMore {
            viewModel.onEvent(BliMusicListEvent.FetchMoreChildData)
        }

    }

}