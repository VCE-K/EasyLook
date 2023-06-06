package cn.vce.easylook.feature_music.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import cn.vce.easylook.R
import cn.vce.easylook.base.BaseFragment
import cn.vce.easylook.databinding.FragmentHomeMusicBinding
import com.google.android.material.tabs.TabLayoutMediator


class HomeMusicFragment : BaseFragment() {


    lateinit var binding: FragmentHomeMusicBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeMusicBinding.inflate(layoutInflater)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            musicViewPage.adapter = HomePagerAdapter(childFragmentManager, lifecycle)
            TabLayoutMediator(tabs, musicViewPage) { tab, position ->
                 when (position) {
                     0 -> tab.text = this@HomeMusicFragment.getString(R.string.music_home_tab_my)
                     1 -> tab.text = this@HomeMusicFragment.getString(R.string.music__home_tab_charts)
                 }
            }.attach()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.menu_search -> {
                true
            }
            else -> false
        }



    private inner  class HomePagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
        FragmentStateAdapter(fragmentManager, lifecycle) {

        private val fragments = listOf(MyMusicFragment(),ChartsFragment())

        override fun getItemCount() = fragments.size

        override fun createFragment(position: Int) = fragments[position]
    }

}
















