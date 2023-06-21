package cn.vce.easylook.feature_music.presentation.music_local

import android.os.Bundle
import androidx.core.view.isVisible
import cn.vce.easylook.R
import cn.vce.easylook.base.BaseVmFragment
import cn.vce.easylook.databinding.FragmentMusicLocalBinding
import cn.vce.easylook.databinding.ItemLayoutViewBinding
import cn.vce.easylook.feature_music.models.PlaylistInfo
import cn.vce.easylook.feature_music.other.Status
import com.drake.brv.BindingAdapter
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MusicLocalFragment: BaseVmFragment<FragmentMusicLocalBinding>() {

    private lateinit var viewModel: MusicLocalVM

    override fun initFragmentViewModel() {
        viewModel = getFragmentViewModel()
    }

    override fun init(savedInstanceState: Bundle?) {
        initView()
    }

    override fun observe() {
        super.observe()
        binding.apply {
            viewModel.playlists.observe(viewLifecycleOwner) { result ->
                when(result.status) {
                    Status.SUCCESS -> {
                        binding.apply {
                            progress.isVisible = false
                            result.data?.let {
                                when (val adapter = playlistRv.adapter) {
                                    is BindingAdapter -> adapter.models = viewModel.playlists.value?.data
                                }
                            }
                        }
                    }
                    Status.ERROR -> Unit
                    Status.LOADING -> binding.progress.isVisible = true
                }
            }
        }
    }

    override fun initView() {
        binding.playlistRv.linear().setup {
            addType<PlaylistInfo>(R.layout.item_layout_view)
            onBind {
                val playlistInfo = getModel<PlaylistInfo>()
                val binding = getBinding<ItemLayoutViewBinding>()
                binding.apply {
                    tvName.text = playlistInfo.name
                    tvDesc.text =  "${playlistInfo.total}首歌曲"
                }
            }
        }
    }

    override fun getLayoutId(): Int?  = R.layout.fragment_music_local


}
