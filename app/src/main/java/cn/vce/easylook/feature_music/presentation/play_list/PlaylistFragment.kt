package cn.vce.easylook.feature_music.presentation.play_list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import cn.vce.easylook.R
import cn.vce.easylook.databinding.FragmentPlaylistBinding
import cn.vce.easylook.feature_music.domain.entities.Song
import cn.vce.easylook.base.BaseFragment
import com.google.android.material.tabs.TabLayoutMediator

class PlaylistFragment : BaseFragment() {

    private lateinit var binding: FragmentPlaylistBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaylistBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViewPage()
    }

    fun setUpViewPage() = binding.apply {
        pager.adapter = PagerAdapter<Song>(emptyList(), childFragmentManager, lifecycle)
        TabLayoutMediator(tabs, pager) { _, _ ->

        }.attach()
    }

    private inner class PagerAdapter<T>(data: List<T>, fragmentManager: FragmentManager, lifecycle: Lifecycle) :
        FragmentStateAdapter(fragmentManager, lifecycle) {

        private val fragments = MutableList(data.size) {
            PlaylistFragment()
        }

        override fun getItemCount() = fragments.size

        override fun createFragment(position: Int) = fragments[position]
    }
}