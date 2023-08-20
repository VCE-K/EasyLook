package cn.vce.easylook.feature_image.presentation

import android.os.Bundle
import cn.vce.easylook.R
import cn.vce.easylook.base.BaseVmFragment
import cn.vce.easylook.databinding.FragmentImageBinding
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.grid
import com.drake.brv.utils.setup


class ImageFragment : BaseVmFragment<FragmentImageBinding>() {

    override fun init(savedInstanceState: Bundle?) {
        binding.apply {
            rv.grid(3).setup {
                addType<Image>(R.layout.image_item)
                onClick(R.id.item){
                    val image = getModel() as Image
                    val bundle = Bundle().apply {
                        putSerializable("image", image)
                    }
                    nav().navigate(R.id.action_nav_image_to_imageDetailFragment, bundle)
                }
            }
            page.onRefresh {
                if (index <= 1){
                    initImages()
                }
                showContent()
            }

        }
    }

    override fun getLayoutId(): Int? = R.layout.fragment_image

    private val images = mutableListOf(Image("移动竖图", imageUrl = "https://t.mwm.moe/mp"),
        Image("萌版竖图", imageUrl = "https://t.mwm.moe/moemp"),
        Image("原神竖图", "https://t.mwm.moe/ysmp"),
        Image("头像方图", "https://t.mwm.moe/tx"),
        Image("七濑胡桃", "https://t.mwm.moe/lai"),
        Image("小狐狸", "https://t.mwm.moe/xhl"))

    val imageList = ArrayList<Image>()


    private fun initImages() {
        imageList.clear()
        repeat(50) {
            val index = (0 until images.size).random()
            //images[index].imageUrl = images[index].imageUrl + "?postId=" +  (0 until 100).random()
            imageList.add(images[index])
        }
        binding.rv.bindingAdapter.models = imageList
    }


}
