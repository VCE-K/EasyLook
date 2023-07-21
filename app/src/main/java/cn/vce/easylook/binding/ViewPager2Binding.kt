package cn.vce.easylook.binding

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import cn.vce.easylook.feature_music.adapters.SwipeSongAdapter
import cn.vce.easylook.feature_music.models.MusicInfo
import cn.vce.easylook.feature_music.other.Resource
import cn.vce.easylook.feature_music.other.Status
import cn.vce.easylook.utils.LogE

object ViewPager2Binding {
    @JvmStatic
    @BindingAdapter(value =  ["adapter", "data", "curPlaying"], requireAll = false)
    fun bind(
        view: ViewPager2,
        adapter: RecyclerView.Adapter<*>?,
        data: Resource<List<MusicInfo>>?,
        curPlayingMusic: MusicInfo?
    ){
        adapter?.let{
            view.adapter = it
        }
        data?.let {
            when(adapter) {
                is SwipeSongAdapter -> {
                    it?.let { result ->
                        when (result.status) {
                            Status.SUCCESS -> {
                                result.data?.let { songs ->
                                    adapter.songs = songs
                                }
                            }
                            Status.ERROR -> Unit
                            Status.LOADING -> Unit
                        }
                    }
                }
                else -> Unit
            }
        }

        curPlayingMusic?.let {
            when(adapter){
                is SwipeSongAdapter -> {
                    val newItemIndex = adapter.songs.indexOfFirst {
                        curPlayingMusic.id == it.id
                    }
                    if (newItemIndex != -1) {
                        view.setCurrentItem(newItemIndex, false)
                        LogE("vpage2.currentItem::${newItemIndex} - ${view.currentItem}")
                    }
                }
            }
        }

    }
}