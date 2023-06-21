package cn.vce.easylook.feature_music.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cn.vce.easylook.feature_music.models.MusicInfo
import cn.vce.easylook.utils.LogE

abstract class BaseSongAdapter<T : Any>(
    private val layoutId: Int
) : RecyclerView.Adapter<BaseSongAdapter.SongViewHolder>() {

    private var _binding: T? = null

    class SongViewHolder(_binding: Any?, itemView: View) : RecyclerView.ViewHolder(itemView) {

        val binding = _binding
    }

    protected val diffCallback = object : DiffUtil.ItemCallback<MusicInfo>() {
        override fun areItemsTheSame(oldItem: MusicInfo, newItem: MusicInfo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MusicInfo, newItem: MusicInfo): Boolean {
            LogE("oldItem::$oldItem")
            LogE("newItem::$newItem")
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    protected abstract val differ: AsyncListDiffer<MusicInfo>

    var songs: List<MusicInfo>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            layoutId,
            parent,
            false
        )
        _binding = DataBindingUtil.bind(view)  // 初始化属性
        return SongViewHolder(_binding, view)
    }


    protected var onItemClickListener: ((MusicInfo) -> Unit)? = null

    fun setItemClickListener(listener: (MusicInfo) -> Unit) {
        onItemClickListener = listener
    }

    override fun getItemCount(): Int {
        return songs.size
    }



}
