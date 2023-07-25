package cn.vce.easylook.binding

import android.support.v4.media.session.PlaybackStateCompat
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter
import cn.vce.easylook.R
import cn.vce.easylook.extension.load
import cn.vce.easylook.feature_music.exoplayer.isPlaying
import cn.vce.easylook.feature_music.other.MusicConfigManager

object ImageViewBinding {
    @JvmStatic
    @BindingAdapter("loadUrl", requireAll = false)  //bind后的名字任意起，注方法一定要为静态，否则报错
    fun loadUrl(view: ImageView, url: String?){
        view.load(url)
    }

    @JvmStatic
    @BindingAdapter("android:src")
    fun setImageRes(image: ImageView, @DrawableRes drawable: Int) {
        image.setImageResource(drawable)
    }

    @JvmStatic
    @BindingAdapter("setPlaying",  requireAll = false)
    fun setPlaying(image: ImageView, playbackState: PlaybackStateCompat?) {
        playbackState?.let {
            val drawable = if (it?.isPlaying == true) R.drawable.ic_pause else R.drawable.ic_play
            image.setImageResource(drawable)
        }
    }

    @JvmStatic
    @BindingAdapter("setPlayMode",  requireAll = false)
    fun setPlayMode(image: ImageView, playMode: Int) {
        when (playMode) {
            MusicConfigManager.REPEAT_MODE_ALL -> {
                image.setImageResource(R.drawable.ic_repeat)
            }
            MusicConfigManager.REPEAT_MODE_ONE -> {
                image.setImageResource(R.drawable.ic_repeat_one)
            }
            MusicConfigManager.PLAY_MODE_RANDOM -> {
                image.setImageResource(R.drawable.ic_shuffle)
            }
        }
    }

    @JvmStatic
    @BindingAdapter("srcPlayState",  requireAll = false)
    fun srcPlayState(image: ImageView, playbackState: PlaybackStateCompat?) {
        image.setImageResource(if (playbackState?.isPlaying==true){
            R.drawable.ic_pause
        }else{
            R.drawable.ic_play
        })
    }
}