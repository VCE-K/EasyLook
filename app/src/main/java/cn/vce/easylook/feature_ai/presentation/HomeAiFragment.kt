package cn.vce.easylook.feature_ai.presentation

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import cn.vce.easylook.R
import cn.vce.easylook.base.BaseFragment
import cn.vce.easylook.databinding.FragmentHomeAiBinding
import cn.vce.easylook.feature_ai.presentation.ai_collection.AiCollectionFragment
import cn.vce.easylook.feature_ai.presentation.ai_list.AiListFragment
import cn.vce.easylook.feature_music.ui.fragments.ChartsFragment
import cn.vce.easylook.feature_music.ui.fragments.MyMusicFragment
import com.google.android.material.tabs.TabLayoutMediator


class HomeAiFragment : BaseFragment() {
    //private val binding: FragmentHomeAiBinding by lazy { FragmentHomeAiBinding.inflate(layoutInflater)}
    private lateinit var binding: FragmentHomeAiBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeAiBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            musicViewPage.adapter = HomePagerAdapter(childFragmentManager, lifecycle)
            TabLayoutMediator(tabs, musicViewPage) { tab, position ->
                when (position) {
                    0 -> tab.text = this@HomeAiFragment.getString(R.string.ai_home_tab_ai_collection)
                    1 -> tab.text = this@HomeAiFragment.getString(R.string.ai__home_tab_ai_list)
                }
            }.attach()
        }
    }


    private inner  class HomePagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
        FragmentStateAdapter(fragmentManager, lifecycle) {

        //我的收藏和AI市场列表
        private val fragments = listOf(AiCollectionFragment(), AiListFragment())

        override fun getItemCount() = fragments.size

        override fun createFragment(position: Int) = fragments[position]


    }
}