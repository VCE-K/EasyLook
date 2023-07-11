package cn.vce.easylook.feature_music.presentation.now_playing

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.widget.SeekBar
import cn.vce.easylook.MainEvent
import cn.vce.easylook.R
import cn.vce.easylook.base.BaseVmFragment
import cn.vce.easylook.databinding.FragmentSongBinding
import cn.vce.easylook.feature_music.exoplayer.isPlaying
import cn.vce.easylook.feature_music.exoplayer.toMusicInfo
import cn.vce.easylook.feature_music.models.MusicInfo
import cn.vce.easylook.feature_music.other.MusicConfigManager
import cn.vce.easylook.feature_music.other.Status.SUCCESS
import cn.vce.easylook.feature_music.presentation.bottom_music_list.PlaylistDialogFragment
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class SongFragment : BaseVmFragment<FragmentSongBinding>() {

    @Inject
    lateinit var glide: RequestManager

    


    private var playbackState: PlaybackStateCompat? = null

    private var shouldUpdateSeekbar = true


    private var curPlayingMusic: MusicInfo? = null

    override fun init(savedInstanceState: Bundle?) {
        initView()
    }

    override fun getLayoutId(): Int?  = R.layout.fragment_song


    override fun initActivityViewModel() {
        mainVM = getActivityViewModel()
    }


    override fun observe() {
        subscribeToObservers()

        mainVM.playMode.observe(viewLifecycleOwner){ playMode ->
            binding.apply {
                when (playMode) {
                    MusicConfigManager.REPEAT_MODE_ALL -> {
                        ivPlayMode.setImageResource(R.drawable.ic_repeat)
                    }
                    MusicConfigManager.REPEAT_MODE_ONE -> {
                        ivPlayMode.setImageResource(R.drawable.ic_repeat_one)
                    }
                    MusicConfigManager.PLAY_MODE_RANDOM -> {
                        ivPlayMode.setImageResource(R.drawable.ic_shuffle)
                    }
                }
            }
        }
    }

    override fun initView() {

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
        binding.ivPlayMode.setOnClickListener {
            mainVM.onEvent(MainEvent.UpdatePlayMode)
        }
        binding.ivPlaylist.setOnClickListener {
            PlaylistDialogFragment().show(mActivity)
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
            curPlayingMusic?.name?.let { it1 -> setActionTitle(it1) }
            updateTitleAndSongImage(curPlayingMusic!!)
        }
        mainVM.playbackState.observe(viewLifecycleOwner) {
            playbackState = it
            binding.ivPlayPauseDetail.setImageResource(
                if(playbackState?.isPlaying == true) R.drawable.ic_pause else R.drawable.ic_play
            )
            binding.seekBar.progress = it?.position?.toInt() ?: 0
        }
        mainVM.curPlayerPosition.observe(viewLifecycleOwner) {
            if(shouldUpdateSeekbar) {
                binding.seekBar.progress = it.toInt()
                setCurPlayerTimeToTextView(it)
            }
        }
        mainVM.curSongDuration.observe(viewLifecycleOwner) {
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





















