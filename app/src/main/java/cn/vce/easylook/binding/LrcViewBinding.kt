package cn.vce.easylook.binding

import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter

import me.wcy.lrcview.LrcView

object LrcViewBinding {

    @JvmStatic
    @BindingAdapter("loadLrc", requireAll = false)  //bind后的名字任意起，注方法一定要为静态，否则报错
    fun loadLrc(view: LrcView, lyric: String?){
        if (lyric != null) {
            view.loadLrc(lyric)
        }else{
            view.setLabel("聆听好音乐")
        }
    }

    @JvmStatic
    @BindingAdapter("updateTime", requireAll = false)  //bind后的名字任意起，注方法一定要为静态，否则报错
    fun updateTime(view: LrcView, progress: Long?){
        progress?.run {
            if (view.isVisible){
                view.updateTime(progress)
            }
        }
    }
}