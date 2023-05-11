package cn.vce.easylook.feature_music.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import cn.vce.easylook.R
import cn.vce.easylook.databinding.ListItemBinding
import com.bumptech.glide.RequestManager
import javax.inject.Inject

class SongAdapter @Inject constructor(
    private val glide: RequestManager
) : BaseSongAdapter<ListItemBinding>(R.layout.list_item) {

    override val differ = AsyncListDiffer(this, diffCallback)

    override fun onBindViewHolder(holder: BaseSongAdapter.SongViewHolder, position: Int) {
        val song = songs[position]
        holder.binding.apply {
            this as ListItemBinding  // 添加类型转换
            tvPrimary.text = song.title
            tvSecondary.text = song.subtitle
            glide.load(song.imageUrl).into(ivItemImage)
            root.setOnClickListener {
                onItemClickListener?.let { click ->
                    click(song)
                }
            }
        }
    }
}



















