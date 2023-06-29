package cn.vce.easylook.binding

import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter
import cn.vce.easylook.feature_video.extension.load

object ImageViewBinding {
    @JvmStatic
    @BindingAdapter("android:imageUrl")  //bind后的名字任意起，注方法一定要为静态，否则报错
    fun setImageUrl(view: ImageView, url: String?){
        view.load(url)
    }

    @JvmStatic
    @BindingAdapter("android:src")
    fun setImageRes(image: ImageView, @DrawableRes drawable: Int) {
        image.setImageResource(drawable)
    }
}