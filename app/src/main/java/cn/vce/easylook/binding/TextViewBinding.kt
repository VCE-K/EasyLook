package cn.vce.easylook.binding

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import cn.vce.easylook.feature_music.models.MusicInfo
import cn.vce.easylook.feature_video.extension.load
import cn.vce.easylook.utils.ConvertUtils

object TextViewBinding {
    @JvmStatic
    @BindingAdapter("android:artistAndAlbum")  //bind后的名字任意起，注方法一定要为静态，否则报错
    fun setArtistAndAlbum(view: TextView, m: MusicInfo){
        view.text = m.artists?.let {
            ConvertUtils.getArtistAndAlbum(m.artists, m.album?.name)
        }
    }
}