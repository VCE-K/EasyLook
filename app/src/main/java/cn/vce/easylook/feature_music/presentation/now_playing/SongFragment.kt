package cn.vce.easylook.feature_music.presentation.now_playing

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import cn.vce.easylook.R
import cn.vce.easylook.databinding.FragmentSongBinding
import cn.vce.easylook.feature_music.exoplayer.isPlaying
import cn.vce.easylook.feature_music.other.Status.SUCCESS
import com.bumptech.glide.RequestManager
import cn.vce.easylook.MainViewModel
import cn.vce.easylook.base.BaseFragment
import cn.vce.easylook.feature_music.models.MusicInfo
import cn.vce.easylook.feature_music.exoplayer.toMusicInfo
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class SongFragment : BaseFragment() {

    @Inject
    lateinit var glide: RequestManager

    private val mainVM: MainViewModel by activityViewModels()

    private val songViewModel: SongViewModel by viewModels()


    private var playbackState: PlaybackStateCompat? = null

    private var shouldUpdateSeekbar = true

    lateinit var binding: FragmentSongBinding

    private var curPlayingMusic: MusicInfo? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSongBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()

        binding.ivPlayPauseDetail.setOnClickListener {
            curPlayingMusic?.let {
                mainVM.playOrToggleSong(it, true)
            }
        }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser) {
                    setCurPlayerTimeToTextView(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                shouldUpdateSeekbar = false
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.let {
                    mainVM.seekTo(it.progress.toLong())
                    shouldUpdateSeekbar = true
                }
            }
        })

        binding.ivSkipPrevious.setOnClickListener {
            mainVM.skipToPreviousSong()
        }

        binding.ivSkip.setOnClickListener {
            mainVM.skipToNextSong()
        }
    }

    private fun updateTitleAndSongImage(musicInfo: MusicInfo) {
        val title = "${musicInfo.name} - ${musicInfo.album?.name}"
        binding.tvSongName.text = title
        glide.load(musicInfo.album?.cover).into(binding.ivSongImage)
    }

    private fun subscribeToObservers() {
        mainVM.mediaItems.observe(viewLifecycleOwner) {
            it?.let { result ->
                when(result.status) {
                    SUCCESS -> {
                        result.data?.let { musicInfos ->
                            if(curPlayingMusic == null && musicInfos.isNotEmpty()) {
                                curPlayingMusic =musicInfos[0]
                                updateTitleAndSongImage(musicInfos[0])
                            }
                        }
                    }
                    else -> Unit
                }
            }
        }
        mainVM.curPlayingSong.observe(viewLifecycleOwner) {
            if(it == null) return@observe
            curPlayingMusic = it.toMusicInfo()
            updateTitleAndSongImage(curPlayingMusic!!)
        }
        mainVM.playbackState.observe(viewLifecycleOwner) {
            playbackState = it
            binding.ivPlayPauseDetail.setImageResource(
                if(playbackState?.isPlaying == true) R.drawable.ic_pause else R.drawable.ic_play
            )
            binding.seekBar.progress = it?.position?.toInt() ?: 0
        }
        songViewModel.curPlayerPosition.observe(viewLifecycleOwner) {
            if(shouldUpdateSeekbar) {
                binding.seekBar.progress = it.toInt()
                setCurPlayerTimeToTextView(it)
            }
        }
        songViewModel.curSongDuration.observe(viewLifecycleOwner) {
            binding.seekBar.max = it.toInt()
            val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
            binding.tvSongDuration.text = dateFormat.format(it)
        }
    }

    private fun setCurPlayerTimeToTextView(ms: Long) {
        val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
        binding.tvCurTime.text = dateFormat.format(ms)
    }
}





















