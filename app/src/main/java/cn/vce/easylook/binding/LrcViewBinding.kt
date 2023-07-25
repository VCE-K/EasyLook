package cn.vce.easylook.binding

import androidx.databinding.BindingAdapter

import me.wcy.lrcview.LrcView

object LrcViewBinding {

    @JvmStatic
    @BindingAdapter("loadLrc", requireAll = false)  //bind后的名字任意起，注方法一定要为静态，否则报错
    fun loadLrc(view: LrcView, lyric: String?){
        view.setLabel("聆听好音乐")
        lyric?.run {
            view.loadLrc(lyric)
        }
    }

    @JvmStatic
    @BindingAdapter("updateTime", requireAll = false)  //bind后的名字任意起，注方法一定要为静态，否则报错
    fun updateTime(view: LrcView, progress: Long?){
        if (progress != null) {
            view.updateTime(progress)
        }
    }
}