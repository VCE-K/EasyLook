package cn.vce.easylook.feature_music.presentation.home_music

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import cn.vce.easylook.R
import cn.vce.easylook.base.BaseVmFragment
import cn.vce.easylook.databinding.FragmentHomeMusicBinding
import cn.vce.easylook.feature_music.adapters.HomePagerAdapter
import cn.vce.easylook.feature_music.presentation.home_music.bli_music_list.BliMusicListFragment
import cn.vce.easylook.feature_music.presentation.home_music.charts.ChartsFragment
import cn.vce.easylook.feature_music.presentation.home_music.music_local.MusicLocalFragment
import cn.vce.easylook.feature_music.presentation.home_music.personalized_playlist.PersonalizedPlaylistFragment
import com.google.android.material.tabs.TabLayoutMediator


class HomeMusicFragment : BaseVmFragment<FragmentHomeMusicBinding>() {
    override fun init(savedInstanceState: Bundle?) {
        initView()
    }
    override fun initView() {
        setHasOptionsMenu(true)
        binding.apply {
            musicViewPage.adapter = HomePagerAdapter(childFragmentManager, lifecycle, listOf(ChartsFragment(),
                PersonalizedPlaylistFragment(), BliMusicListFragment(),  MusicLocalFragment())
            )
            TabLayoutMediator(tabs, musicViewPage) { tab, position ->
                when (position) {
                    //0 -> tab.text = this@HomeMusicFragment.getString(R.string.music_home_tab_search)
                    0 -> tab.text = this@HomeMusicFragment.getString(R.string.music_home_tab_charts)
                    1 -> tab.text = "推荐歌单"
                    2 -> tab.text = "BliMusic"
                    3 -> tab.text = this@HomeMusicFragment.getString(R.string.music_home_tab_my)
                }
            }.attach()
        }
    }

    override fun getLayoutId(): Int?  = R.layout.fragment_home_music

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_search, menu)
    }

}















