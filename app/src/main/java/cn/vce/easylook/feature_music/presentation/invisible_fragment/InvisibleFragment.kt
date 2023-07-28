package cn.vce.easylook.feature_music.presentation.invisible_fragment

import android.os.Bundle
import cn.vce.easylook.base.BaseVmFragment
import cn.vce.easylook.databinding.ActivityMainBinding
/*
* 隐藏的Fragment
* */
class InvisibleFragment : BaseVmFragment<ActivityMainBinding>() {
    private var callback: ((Boolean, List<String>) -> Unit)? = null


    override fun init(savedInstanceState: Bundle?) {
        nav().navigateUp()
    }

    override fun getLayoutId(): Int? = null

    fun requestNow(cb: (Boolean, List<String>) -> Unit, vararg permissions: String) {

    }

}
