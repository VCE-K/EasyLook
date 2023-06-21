package cn.vce.easylook.feature_music.presentation.bottom_dialog

import android.os.Bundle
import android.view.View
import cn.vce.easylook.R
import cn.vce.easylook.base.BaseBottomSheetDialogFragment
import cn.vce.easylook.databinding.DialogLayoutBinding
import cn.vce.easylook.databinding.ItemDialogBinding
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.google.android.material.bottomsheet.BottomSheetBehavior

/**
 * 歌曲操作类
 *
 */
class BottomDialogFragment : BaseBottomSheetDialogFragment<DialogLayoutBinding>() {


    private var itemData = mutableMapOf(
        R.string.popup_play_next to R.drawable.ic_queue_play_next,
        R.string.popup_add_to_playlist to R.drawable.ic_playlist_add,
        R.string.popup_album to R.drawable.ic_album,
        R.string.popup_artist to R.drawable.ic_art_track,
        R.string.popup_delete to R.drawable.ic_delete
    )

    val data = mutableListOf<PopupItemBean>()


    override fun getLayoutId(): Int? = R.layout.dialog_layout



    override fun init(savedInstanceState: Bundle?) {
        initView()
    }



    private var mBehavior: BottomSheetBehavior<*>? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mBehavior = BottomSheetBehavior.from(binding.root?.parent as View)
    }


    override fun initView() {
        itemData.forEach(){
            data.add(PopupItemBean(getString(it.key), it.value))
        }
        binding.apply{
            titleTv?.text = "歌名"//music?.title
            subTitleTv?.text = "作者名-专辑名称"//ConvertUtils.getArtistAndAlbum(music?.artist, music?.album)
            bottomSheetRv.linear().setup {
                addType<PopupItemBean>(R.layout.item_dialog)
                onBind {
                    val model = getModel<PopupItemBean>()
                    val binding = getBinding<ItemDialogBinding>()
                    binding.apply {
                        tvTitle.text = model.title
                        ivIcon.setImageResource(model.icon)
                        //mBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
                    }
                }
            }.models = data
        }
    }


}

data class PopupItemBean(val title: String = "", val icon: Int = 0)