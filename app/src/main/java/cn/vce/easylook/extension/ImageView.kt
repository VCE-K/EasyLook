package cn.vce.easylook.extension

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import cn.vce.easylook.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.scwang.smart.refresh.layout.util.SmartUtil.dp2px
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

/**
 * Glide加载图片，可以指定圆角弧度。
 *
 * @param url 图片地址
 * @param round 圆角，单位dp
 * @param cornerType 圆角角度
 */
fun ImageView.load(url: String?, round: Float = 0f, cornerType: RoundedCornersTransformation.CornerType = RoundedCornersTransformation.CornerType.ALL) {
    if (round == 0f) {
        Glide.with(this.context)
            .setDefaultRequestOptions(
                RequestOptions()
                    .placeholder(R.drawable.ic_image)
                    .error(R.drawable.ic_image)
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
            ).load(url).into(this)
    } else {
        val option = RequestOptions.bitmapTransform(RoundedCornersTransformation(dp2px(round), 0, cornerType)).placeholder(
            R.drawable.shape_album_loading_bg)
        Glide.with(this.context)
            .setDefaultRequestOptions(
                RequestOptions()
                    .placeholder(R.drawable.ic_image)
                    .error(R.drawable.ic_image)
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
            ).load(url).apply(option).into(this)
    }
}

/**
 * Glide加载图片，可以定义配置参数。
 *
 * @param url 图片地址
 * @param options 配置参数
 */
fun ImageView.load(url: String?, options: RequestOptions.() -> RequestOptions) {
    Glide.with(this.context).setDefaultRequestOptions(
        RequestOptions()
            .placeholder(R.drawable.ic_image)
            .error(R.drawable.ic_image)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
    ).load(url)
        .into(this)
}



/**
 * Glide加载图片，可以指定圆角弧度。
 *
 * @param url 图片地址
 * @param round 圆角，单位dp
 * @param cornerType 圆角角度
 */
fun ImageView.load(res: Int, round: Float = 0f, cornerType: RoundedCornersTransformation.CornerType = RoundedCornersTransformation.CornerType.ALL) {
    if (round == 0f) {
        Glide.with(this.context).setDefaultRequestOptions(
            RequestOptions()
                .placeholder(R.drawable.ic_image)
                .error(R.drawable.ic_image)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
        ).load(res)
            .into(this)
    } else {
        val option = RequestOptions.bitmapTransform(RoundedCornersTransformation(dp2px(round), 0, cornerType))
            .placeholder(R.drawable.ic_image)
            .error(R.drawable.ic_image)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
        Glide.with(this.context).load(res).apply(option).into(this)
    }
}

/**
 * Glide加载图片，可以定义配置参数。
 *
 * @param url 图片地址
 * @param options 配置参数
 */
fun ImageView.load(res: Int, options: RequestOptions.() -> RequestOptions) {
    Glide.with(this.context).setDefaultRequestOptions(
        RequestOptions()
            .placeholder(R.drawable.ic_image)
            .error(R.drawable.ic_image)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
    ).load(res)
        .into(this)
}