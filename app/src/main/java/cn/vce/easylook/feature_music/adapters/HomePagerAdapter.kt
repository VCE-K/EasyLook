package cn.vce.easylook.feature_music.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class HomePagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle, val fragments: List<Fragment>) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    /*private inner  class HomePagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
        FragmentStateAdapter(fragmentManager) {*/

    private val list = fragments

    override fun getItemCount() = list.size

    override fun createFragment(position: Int) = list[position]
}