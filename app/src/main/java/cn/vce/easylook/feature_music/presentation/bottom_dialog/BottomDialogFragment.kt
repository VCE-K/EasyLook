package cn.vce.easylook.feature_music.presentation.bottom_dialog

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import cn.vce.easylook.MainActivity
import cn.vce.easylook.R
import cn.vce.easylook.base.BaseBottomSheetDialogFragment
import cn.vce.easylook.databinding.DialogLayoutBinding
import cn.vce.easylook.databinding.ItemDialogBinding
import cn.vce.easylook.feature_music.models.MusicInfo
import cn.vce.easylook.feature_music.models.PlaylistType
import cn.vce.easylook.utils.ConvertUtils
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * 歌曲操作类
 *
 */
@AndroidEntryPoint
class BottomDialogFragment : BaseBottomSheetDialogFragment<DialogLayoutBinding>() {


    private lateinit var viewModel: BottomDialogVM
    private var itemData = mutableMapOf(
        R.string.popup_play_next to R.drawable.ic_queue_play_next,
        R.string.popup_add_to_collection to R.drawable.ic_add_love,
        R.string.popup_add_to_playlist to R.drawable.ic_playlist_add,
        R.string.popup_album to R.drawable.ic_album,
        R.string.popup_artist to R.drawable.ic_art_track,
        R.string.popup_delete to R.drawable.ic_delete
    )

    private lateinit var musicInfo: MusicInfo

    val data = mutableListOf<PopupItemBean>()


    override fun getLayoutId(): Int? = R.layout.dialog_layout


    override fun initFragmentViewModel() {
        viewModel = getFragmentViewModel()
    }

    override fun init(savedInstanceState: Bundle?) {
        initView()
    }

    override fun observe() {
        viewModel.eventFlow.onEach { event ->
            when(event){
                is BottomDialogVM.UiEvent.SaveMusic -> {
                    lifecycleScope.launch {
                        val rootView = (activity as MainActivity).findViewById<View>(R.id.drawer_layout)
                        Snackbar.make(rootView, "${musicInfo.name}" + getString(R.string.popup_add_to_collection), Snackbar.LENGTH_SHORT)
                            .setAction("Undo") {
                                viewModel.onEvent(BottomDialogEvent.RestoreMusicToPlaylist(musicInfo))
                                Toast.makeText(mActivity, "撤销${musicInfo.name}" + getString(R.string.popup_add_to_collection),
                                    Toast.LENGTH_SHORT).show()
                            }.show()
                        hide()
                    }
                }
                is BottomDialogVM.UiEvent.ShwoSnackbar -> {
                    Toast.makeText(mActivity, event.message, Toast.LENGTH_LONG).show()
                }
            }
        }.launchIn(lifecycleScope)
    }

    private var mBehavior: BottomSheetBehavior<*>? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mBehavior = BottomSheetBehavior.from(binding.root?.parent as View)
    }


    override fun initView() {
        this.isHidden
        itemData.forEach(){
            if (it.key == R.string.popup_add_to_collection){
                data.add(PopupItemBean(getString(it.key) , it.value))
            }else if (it.key == R.string.popup_album){
                data.add(PopupItemBean(getString(it.key, musicInfo.album?.name), it.value))
            }else if (it.key == R.string.popup_artist){
                data.add(PopupItemBean(getString(it.key, ConvertUtils.getArtist(musicInfo?.artists)) , it.value))
            }else{
                data.add(PopupItemBean(getString(it.key), it.value))
            }
        }
        binding.apply{
            titleTv?.text = musicInfo.name//music?.title
            subTitleTv?.text = ConvertUtils.getArtistAndAlbum(musicInfo?.artists,
                musicInfo?.album?.name
            )
            bottomSheetRv.linear().setup {
                addType<PopupItemBean>(R.layout.item_dialog)
                onBind {
                    val model = getModel<PopupItemBean>()
                    val binding = getBinding<ItemDialogBinding>()
                    /*binding.apply {
                        tvTitle.text = model.title
                        ivIcon.setImageResource(model.icon)
                        //mBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
                    }*/
                }
                R.id.item.onFastClick {
                    val model = getModel<PopupItemBean>()
                    if (model.title == getString(R.string.popup_add_to_collection)){
                        musicInfo.apply {
                            pid = PlaylistType.LOVE.toString()
                            viewModel.onEvent(BottomDialogEvent.SaveMusicToPlaylist(musicInfo))
                        }
                    }
                }
            }.models = data
        }
    }

    fun show(context: Context, musicInfo: MusicInfo) {
        this.musicInfo = musicInfo
        mActivity = context as AppCompatActivity
        mContext = context
        val ft = mActivity.supportFragmentManager.beginTransaction()
        ft.add(this, tag)
        ft.commitAllowingStateLoss()
    }

}

data class PopupItemBean(val title: String = "", val icon: Int = 0, val fragment:Fragment?= null)