package cn.vce.easylook.feature_video.adapters

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import cn.vce.easylook.feature_video.models.HomePageRecommend
import com.bumptech.glide.Glide
import com.youth.banner.adapter.BannerAdapter

class HomeBannerAdapter(data: MutableList<HomePageRecommend.ItemX>? = null) :
    BannerAdapter<HomePageRecommend.ItemX, HomeBannerAdapter.BannerViewHolder>(data) {

    override fun onCreateHolder(
        parent: ViewGroup,
        viewType: Int
    ): BannerViewHolder {
        val img = ImageView(parent.context)
        img.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        img.scaleType = ImageView.ScaleType.CENTER_CROP
        return BannerViewHolder(img)
    }

    override fun onBindView(
        holder: BannerViewHolder,
        data: HomePageRecommend.ItemX,
        position: Int,
        size: Int
    ) {
        val img = holder.itemView as ImageView
        Glide.with(img)
            .load(data.data.content.data.cover.feed)
            .into(img)
    }

    class BannerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}