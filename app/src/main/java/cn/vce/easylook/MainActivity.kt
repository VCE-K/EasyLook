package cn.vce.easylook

import android.annotation.SuppressLint
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.view.View
import cn.vce.common.base.BaseActivity
import cn.vce.easylook.databinding.ActivityMainBinding
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.util.Util
import com.google.gson.Gson
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL

class MainActivity : BaseActivity() {

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var player: SimpleExoPlayer? = null

    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }


    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    public override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            serviceScope.launch{
                initializePlayer()
            }
        }
    }

    public override fun onResume() {
        super.onResume()
        hideSystemUi()
        if ((Util.SDK_INT < 24 || player == null)) {
            serviceScope.launch{
                initializePlayer()
            }
        }
    }

    public override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24) {
            releasePlayer()
        }
    }

    public override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) {
            releasePlayer()
        }
    }


    private suspend fun initializePlayer() {
        player = SimpleExoPlayer.Builder(this@MainActivity)
            .build()
            .also { exoPlayer ->
                binding.videoView.player = exoPlayer
                withContext(Dispatchers.IO) {
                    val remoteJsonSource: Uri =
                        Uri.parse("https://storage.googleapis.com/uamp/catalog.json")
                    val musicCat = try {
                        downloadJson(remoteJsonSource)
                    } catch (ioException: IOException) {
                        return@withContext null
                    }
                    val mediaItems = ArrayList<MediaItem>()
                    musicCat.music.map { song ->
                        val mediaMetadata = MediaMetadata.Builder()
                            .setTitle(song.title)
                            .setArtist(song.artist)
                            .setArtworkUri(Uri.parse(song.image))
                            .build()
                        val mediaItem = MediaItem.Builder()
                            .setUri(song.source)
                            .setMediaMetadata(mediaMetadata)
                            .build()
                        withContext(Dispatchers.Main){
                            exoPlayer.addMediaItem(mediaItem)
                        }
                    }
                }
                exoPlayer.playWhenReady = playWhenReady
                exoPlayer.seekTo(currentWindow, playbackPosition)
                exoPlayer.prepare()
            }
    }

    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        binding.videoView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }


    private fun releasePlayer() {
        player?.run {
            playbackPosition = this.currentPosition
            currentWindow = this.currentWindowIndex
            playWhenReady = this.playWhenReady
            release()
        }
        player = null
    }

    @Throws(IOException::class)
    private fun downloadJson(catalogUri: Uri): JsonCatalog {
        val catalogConn = URL(catalogUri.toString())
        val reader = BufferedReader(InputStreamReader(catalogConn.openStream()))
        return Gson().fromJson(reader, JsonCatalog::class.java)
    }
}

class JsonCatalog {
    var music: List<JsonMusic> = ArrayList()
}

@Suppress("unused")
class JsonMusic {
    var id: String = ""
    var title: String = ""
    var album: String = ""
    var artist: String = ""
    var genre: String = ""
    var source: String = ""
    var image: String = ""
    var trackNumber: Long = 0
    var totalTrackCount: Long = 0
    var duration: Long = C.TIME_UNSET
    var site: String = ""
}
