package cn.vce.easylook.feature_music.adapters

import androidx.recyclerview.widget.AsyncListDiffer
import cn.vce.easylook.R
import cn.vce.easylook.databinding.SwipeItemBinding
import cn.vce.easylook.utils.ConvertUtils

class SwipeSongAdapter : BaseSongAdapter<SwipeItemBinding>(R.layout.swipe_item) {

    override val differ = AsyncListDiffer(this, diffCallback)

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val musicInfo = songs[position]
        holder.binding.apply {
            this as SwipeItemBinding  // 添加类型转换
            val text = musicInfo.name
            tvPrimary.text = text
            var artistNames = ConvertUtils.getArtist(musicInfo.artists)
            tvPrimary2.text =  "$artistNames - ${musicInfo.album?.name}"
            root.setOnClickListener {
                onItemClickListener?.let { click ->
                    click(musicInfo)
                }
            }
        }
    }

}



















