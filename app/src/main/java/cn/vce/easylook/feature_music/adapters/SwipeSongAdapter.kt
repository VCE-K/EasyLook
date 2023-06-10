package cn.vce.easylook.feature_music.adapters

import androidx.recyclerview.widget.AsyncListDiffer
import cn.vce.easylook.R
import cn.vce.easylook.databinding.SwipeItemBinding

class SwipeSongAdapter : BaseSongAdapter<SwipeItemBinding>(R.layout.swipe_item) {

    override val differ = AsyncListDiffer(this, diffCallback)

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]
        holder.binding.apply {
            this as SwipeItemBinding  // 添加类型转换
            val text = song.title
            tvPrimary.text = text
            tvPrimary2.text =  "${song.artistNames} - ${song.subtitle}"
            root.setOnClickListener {
                onItemClickListener?.let { click ->
                    click(song)
                }
            }
        }
    }

}



















