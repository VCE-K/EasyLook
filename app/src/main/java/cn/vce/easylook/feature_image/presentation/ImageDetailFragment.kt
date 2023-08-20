package cn.vce.easylook.feature_image.presentation

import android.os.Bundle
import cn.vce.easylook.R
import cn.vce.easylook.base.BaseVmFragment
import cn.vce.easylook.databinding.FragmentImageDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ImageDetailFragment : BaseVmFragment<FragmentImageDetailBinding>() {

    lateinit var vm: ImageDetailVM
    override fun initFragmentViewModel() {
        vm = getFragmentViewModel()
    }

    override fun init(savedInstanceState: Bundle?) {
        binding.vm = vm
    }

    override fun getLayoutId(): Int? = R.layout.fragment_image_detail
}

