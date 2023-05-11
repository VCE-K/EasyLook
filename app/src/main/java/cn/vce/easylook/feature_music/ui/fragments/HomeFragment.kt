package cn.vce.easylook.feature_music.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import cn.vce.easylook.R
import cn.vce.easylook.databinding.FragmentHomeBinding
import cn.vce.easylook.feature_music.adapters.SongAdapter
import cn.vce.easylook.feature_music.data.remote.MusicDatabase
import cn.vce.easylook.feature_music.exoplayer.FirebaseMusicSource
import cn.vce.easylook.feature_music.other.Status
import cn.vce.easylook.feature_music.ui.viewmodels.MainViewModel
import cn.vce.noteapp.feature_note.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment(R.layout.fragment_home) {

    lateinit var mainViewModel: MainViewModel

    @Inject
    lateinit var songAdapter: SongAdapter

    lateinit var binding: FragmentHomeBinding

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
    @Inject
    lateinit var firebaseMusicSource: FirebaseMusicSource

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

        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        setupRecyclerView()
        subscribeToObservers()

        songAdapter.setItemClickListener {
            mainViewModel.playOrToggleSong(it)
        }
    }

    private fun setupRecyclerView() = binding.apply {
        swipeRefresh.setOnRefreshListener {
            serviceScope.launch {
                firebaseMusicSource.fetchMediaData()
            }
            swipeRefresh.isRefreshing = true
        }
        rvAllSongs.apply {
            adapter = songAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun subscribeToObservers() {
        mainViewModel.mediaItems.observe(viewLifecycleOwner) { result ->
            when(result.status) {
                Status.SUCCESS -> {
                    binding.allSongsProgressBar.isVisible = false
                    result.data?.let { songs ->
                        songAdapter.songs = songs
                    }
                }
                Status.ERROR -> Unit
                Status.LOADING -> binding.allSongsProgressBar.isVisible = true
            }
        }
    }
}
















