package cn.vce.easylook.feature_music.presentation.bottom_music_list

import android.os.Bundle
import android.view.View
import cn.vce.easylook.MainEvent
import cn.vce.easylook.MainViewModel
import cn.vce.easylook.R
import cn.vce.easylook.base.BaseBottomSheetDialogFragment
import cn.vce.easylook.databinding.FragmentPlaylistDialogBinding
import cn.vce.easylook.feature_music.models.MusicInfo
import cn.vce.easylook.feature_music.other.MusicConfigManager
import cn.vce.easylook.feature_music.other.Status
import com.drake.brv.BindingAdapter
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PlaylistDialogFragment: BaseBottomSheetDialogFragment<FragmentPlaylistDialogBinding>() {

    

    private var mBehavior: BottomSheetBehavior<*>? = null

    override fun getLayoutId(): Int? = R.layout.fragment_playlist_dialog



    override fun initActivityViewModel() {
        mainVM = getActivityViewModel()
    }

    override fun init(savedInstanceState: Bundle?) {
        initView()
    }

    override fun observe() {
        mainVM.mediaItems.observe(viewLifecycleOwner){ result ->
            when(result.status) {
                Status.SUCCESS -> {
                    binding.apply {
                        result.data?.let { songs ->
                            when (val adapter = bottomSheetRv.adapter) {
                                is BindingAdapter -> {
                                    adapter.models = songs
                                }
                            }
                        }
                    }
                }
                /*Status.ERROR -> binding.page.showError()
                Status.LOADING -> {
                    binding.page.showLoading()
                }*/
                else -> {}
            }
        }
        mainVM.playMode.observe(viewLifecycleOwner){ playMode ->
            binding.apply {
                when (playMode) {
                    MusicConfigManager.REPEAT_MODE_ALL -> {
                        ivPlayMode.setImageResource(R.drawable.ic_repeat)
                        subTitleTv.text = getString(R.string.play_mode_repeat)
                    }
                    MusicConfigManager.REPEAT_MODE_ONE -> {
                        ivPlayMode.setImageResource(R.drawable.ic_repeat_one)
                        subTitleTv.text = getString(R.string.play_mode_repeat_one)
                    }
                    MusicConfigManager.PLAY_MODE_RANDOM -> {
                        ivPlayMode.setImageResource(R.drawable.ic_shuffle)
                        subTitleTv.text = getString(R.string.play_mode_shuffle)
                    }
                }
            }
        }
    }


    override fun initView() {

        mBehavior = BottomSheetBehavior.from(binding.root.parent as View)
        //默认全屏展开
        mBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
        binding.apply{
            titleTv?.text = "当前播放列表"
            subTitleTv?.text = ""
            bottomSheetRv.linear().setup {
                addType<MusicInfo>(R.layout.item_music_dialog)
                onBind {

                }
                R.id.item.onFastClick {
                    val model = getModel<MusicInfo>()
                    mainVM.mediaItems.value?.data?.let { it1 ->
                        mainVM.onEvent(MainEvent.ClickPlay(it1, model))
                    }
                }
            }

            // 获取RecyclerView的LayoutManager
            binding.ivPlayMode.setOnClickListener {
                mainVM.onEvent(MainEvent.UpdatePlayMode)
            }
        }
    }

}