package cn.vce.easylook.feature_music.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import cn.vce.easylook.feature_music.data.entities.Song

abstract class BaseSongAdapter<T : Any>(
    private val layoutId: Int
) : RecyclerView.Adapter<BaseSongAdapter.SongViewHolder>() {

    private var _binding: T? = null

    class SongViewHolder(_binding: Any?, itemView: View) : RecyclerView.ViewHolder(itemView) {

        val binding = _binding
    }

    protected val diffCallback = object : DiffUtil.ItemCallback<Song>() {
        override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.mediaId == newItem.mediaId
        }

        override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    protected abstract val differ: AsyncListDiffer<Song>

    var songs: List<Song>
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


    protected var onItemClickListener: ((Song) -> Unit)? = null

    fun setItemClickListener(listener: (Song) -> Unit) {
        onItemClickListener = listener
    }

    override fun getItemCount(): Int {
        return songs.size
    }
}
