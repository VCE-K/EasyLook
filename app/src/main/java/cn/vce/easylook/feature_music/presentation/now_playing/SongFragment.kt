package cn.vce.easylook.feature_music.presentation.now_playing

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.widget.SeekBar
import cn.vce.easylook.MainEvent
import cn.vce.easylook.R
import cn.vce.easylook.base.BaseVmFragment
import cn.vce.easylook.databinding.FragmentSongBinding
import cn.vce.easylook.feature_music.exoplayer.toMusicInfo
import cn.vce.easylook.feature_music.models.MusicInfo
import cn.vce.easylook.feature_music.db.MusicConfigManager
import cn.vce.easylook.feature_music.presentation.bottom_music_list.PlaylistDialogFragment
import cn.vce.easylook.utils.ConvertUtils
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class SongFragment : BaseVmFragment<FragmentSongBinding>() {

    @Inject
    lateinit var glide: RequestManager
    private lateinit var viewModel: SongViewModel


    private var playbackState: PlaybackStateCompat? = null

    private var shouldUpdateSeekbar = true


    private var curPlayingMusic: MusicInfo? = null

    override fun init(savedInstanceState: Bundle?) {
        initView()
    }

    override fun getLayoutId(): Int?  = R.layout.fragment_song


    override fun initViewModel() {
        mainVM = getActivityViewModel()
        viewModel = getFragmentViewModel()
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
        binding.run {
            m = mainVM
            vm = viewModel
            ivPlayPauseDetail.setOnClickListener {
                curPlayingMusic?.let {
                    mainVM.playOrToggleSong(it, true)
                }
            }

            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
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

            lyricViewX.run {
                setDraggable(true
                ) {  v, time ->
                    seekBar?.let {
                        mainVM.seekTo(time)
                        return@setDraggable true
                    }
                    false
                }
            }

            tvSongName.setOnClickListener{
                viewModel.setLyricShow(true)
            }
            ivSongImage.setOnClickListener{
                viewModel.setLyricShow(true)
            }


            ivSkipPrevious.setOnClickListener {
                mainVM.skipToPreviousSong()
            }

            ivSkip.setOnClickListener {
                mainVM.skipToNextSong()
            }
            ivPlayMode.setOnClickListener {
                mainVM.onEvent(MainEvent.UpdatePlayMode)
            }
            ivPlaylist.setOnClickListener {
                PlaylistDialogFragment().show(mActivity)
            }
        }
    }



    private fun updateTitleAndSongImage(musicInfo: MusicInfo) {
        //val artistName = ConvertUtils.getArtist(musicInfo.artists)
        val title = musicInfo.name
        binding.tvSongName.text = title
        glide.load(musicInfo.album?.cover).into(binding.ivSongImage)
    }

    private fun subscribeToObservers() {

        mainVM.curPlayingSong.observe(viewLifecycleOwner) {
            if(it == null) return@observe
            curPlayingMusic = it.toMusicInfo()
            curPlayingMusic?.name?.let { it1 -> setActionTitle(it1) }
            updateTitleAndSongImage(curPlayingMusic!!)
        }
        mainVM.playbackState.observe(viewLifecycleOwner) {
            playbackState = it
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





















