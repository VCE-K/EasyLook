package cn.vce.easylook.feature_music.ui.fragments

import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import cn.vce.easylook.R
import cn.vce.easylook.databinding.FragmentHomeBinding
import cn.vce.easylook.feature_music.adapters.SongAdapter
import cn.vce.easylook.feature_music.data.Repository
import cn.vce.easylook.feature_music.data.remote.MusicDatabase
import cn.vce.easylook.feature_music.data.remote.MusicNetWork
import cn.vce.easylook.feature_music.exoplayer.FirebaseMusicSource
import cn.vce.easylook.feature_music.other.Status
import cn.vce.easylook.feature_music.ui.viewmodels.MainViewModel
import cn.vce.noteapp.feature_note.base.BaseFragment
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject


class HomeFragment : BaseFragment(R.layout.fragment_home) {


    lateinit var binding: FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            musicViewPage.adapter = MusicPagerAdapter(childFragmentManager, lifecycle)
            TabLayoutMediator(tabs, musicViewPage) { tab, position ->
                 when (position) {
                     0 -> tab.text = this@HomeFragment.getString(R.string.tab_my)
                     1 -> tab.text = this@HomeFragment.getString(R.string.tab_charts)
                 }
            }.attach()
        }
    }


    private inner  class MusicPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
        FragmentStateAdapter(fragmentManager, lifecycle) {

        private val fragments = listOf(MyMusicFragment(),ChartsFragment())

        override fun getItemCount() = fragments.size

        override fun createFragment(position: Int) = fragments[position]
    }

}
















